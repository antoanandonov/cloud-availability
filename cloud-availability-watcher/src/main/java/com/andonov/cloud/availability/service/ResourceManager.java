package com.andonov.cloud.availability.service;

import com.andonov.cloud.availability.dto.Quota;
import com.andonov.cloud.availability.props.CloudFoundryCli;
import com.andonov.cloud.availability.util.Executor;
import com.andonov.cloud.availability.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ResourceManager {

    private final CloudFoundryCli cloudFoundryCli;

    @Autowired
    public ResourceManager(CloudFoundryCli cloudFoundryCli) {
        this.cloudFoundryCli = cloudFoundryCli;
    }

    public Quota checkHealthStatus(String appName, Quota quota, String cfResponse) {
        List<Map<String, String>> resources = obtainCloudResources(cfResponse);

        if (resources.isEmpty()) {
            Executor.exec(cloudFoundryCli.restart(appName));
            Util.sleep(10, TimeUnit.SECONDS);
            return quota;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("./cf scale ").append(appName);

        if (!isMemoryOk(resources)) {
            long memory = convertDataSizeToMB(quota.getMemory()) + 512;
            quota.setMemory(memory + "M");
            builder.append(" -m ").append(memory).append("M");
        }
        if (!isDiskOk(resources)) {
            long disk = convertDataSizeToMB(quota.getDisk()) + 512;
            quota.setDisk(disk + "M");
            builder.append(" -k ").append(disk).append("M");
        }
        if (!isCpuOk(resources) || !areAllInstancesRunning(resources)) {
            int instances = resources.size() + 1;
            quota.setInstances(instances);
            builder.append(" -i ").append(instances);
        }

        Executor.exec(builder.append(" -f").toString());

        return quota;
    }

    private List<Map<String, String>> obtainCloudResources(String cfResponse) {
        return Arrays.stream(cfResponse.split(System.lineSeparator()))
                     .filter(l -> l.startsWith("#"))
                     .map(l -> {
                         final String[] res = l.split(StringUtils.SPACE + "{3}");

                         Map<String, String> result = new HashMap<>();
                         result.put("state", res[1]);
                         result.put("cpu", res[3]);
                         result.put("memory", res[4]);
                         result.put("disk", res[5]);

                         return result;
                     }).collect(Collectors.toList());
    }

    private boolean areAllInstancesRunning(List<Map<String, String>> resources) {
        return resources.stream().noneMatch(r -> "running".equals(r.get("state").trim()));
    }

    private boolean isMemoryOk(List<Map<String, String>> resources) {
        return resources.stream().allMatch(r -> {
            final String[] memoryMetrics = r.get("memory").split(" of ");
            return isHealthy(memoryMetrics[0], memoryMetrics[1]);
        });
    }

    private boolean isCpuOk(List<Map<String, String>> resources) {
        return resources.stream().allMatch(r -> {
            final String cpuMetric = r.get("cpu");
            final double cpu = Double.parseDouble(cpuMetric.substring(0, cpuMetric.length() - 1).trim());
            return cpu < 95;
        });
    }

    private boolean isDiskOk(List<Map<String, String>> resources) {
        return resources.stream().allMatch(r -> {
            final String[] diskMetrics = r.get("disk").split(" of ");
            return isHealthy(diskMetrics[0], diskMetrics[1]);
        });
    }

    private boolean isHealthy(String metric1, String metric2) {
        return convertDataSizeToMB(metric1).doubleValue() / convertDataSizeToMB(metric2).doubleValue() * 100 < 90;
    }

    private static Long convertDataSizeToMB(String dataSize) {
        long size = Double.valueOf(dataSize.substring(0, dataSize.length() - 1)).longValue();

        if (dataSize.endsWith("T") || dataSize.endsWith("TB")) {
            size *= (1024 * 1024);
        } else if (dataSize.endsWith("G") || dataSize.endsWith("GB")) {
            size *= 1024;
        } else if (dataSize.endsWith("B") || dataSize.endsWith("b")) {
            size /= 1024;
        }
        return size;
    }
}
