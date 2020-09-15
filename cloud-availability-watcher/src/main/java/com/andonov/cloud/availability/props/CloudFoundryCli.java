package com.andonov.cloud.availability.props;

import com.andonov.cloud.availability.constants.Property;
import com.andonov.cloud.availability.dto.Credentials;
import com.andonov.cloud.availability.util.Executor;
import com.andonov.cloud.availability.util.Util;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.MessageFormat;

@Component
public final class CloudFoundryCli {

    private final CloudFoundryProps cfProps;

    @Autowired
    public CloudFoundryCli(CloudFoundryProps cfProps) {
        this.cfProps = cfProps;
    }

    public String ping(final String api) {
        return MessageFormat.format(Pattern.PING, cfProps.getAppName(), Env.PUBLIC_DOMAIN, api.toLowerCase());
    }

    public String ping(final String appName, final String api) {
        return MessageFormat.format(Pattern.PING, appName.toLowerCase(), Env.PUBLIC_DOMAIN, api.toLowerCase());
    }

    public String scaleTo(int instance) {
        return MessageFormat.format(Pattern.SCALE, cfProps.getAppName(), instance);
    }

    public String scale(int instance, String memory, String disk) {
        return MessageFormat.format(Pattern.SCALE, cfProps.getAppName(), instance);
    }

    public String restart(String appName) {
        return MessageFormat.format(Pattern.RESTART, appName);
    }

    public String logout() {
        return Pattern.LOGOUT;
    }

    public void login(Credentials credentials) {
        byte[] decPass = Util.decrypt(SerializationUtils.deserialize(credentials.getPassword()), cfProps.getEncKey(), cfProps.getEncVec());
        Executor.exec(MessageFormat.format(Pattern.LOGIN, credentials.getApi(), credentials.getUser(), new String(decPass), credentials.getOrg(), credentials.getSpace()));
    }

    public static final class Env {

        private static final String PUBLIC_DOMAIN = System.getProperty(Property.CF_PUBLIC_DOMAIN, "cfapps.sapdr-on-aws.sapxdc.io");
        private static final String APP_NAME = System.getProperty(Property.CF_APP_NAME, "cloud-availability-application");
        private static final String MEMORY = System.getProperty(Property.CF_MEMORY, "2G");
        private static final String DISK_QUOTA = System.getProperty(Property.CF_DISK_QUOTA, "1G");
        private static final String HEALTH_CHECK_TYPE = System.getProperty(Property.CF_HEALTHCHECK_TYPE, "process");
        private static final String WAR_PATH = Property.BASE_DIR + File.separatorChar + "target" + File.separatorChar + "cf.healthcheck.webapp.war";
        private static final String MANIFEST_PATH = Property.PROJECT_BASE_DIR + File.separatorChar + "manifest.yml";
        public static final String INSTANCES = System.getProperty(Property.CF_INSTANCES, "2");
    }

    private static final class Pattern {

        private static final String LOGIN = "./cf login -a {0} -u {1} -p {2} -o {3} -s {4}"; // $0 - url, $1 - username, $2 - password, $3 - organisation, $4 - space
        private static final String PUSH = "./cf push {0} -i {1} -k {2} -m {3} -u {4} -p {5} --no-manifest --no-start"; // $0 - application name, $1 - No. instances, $2 - disk quota, $3 - memory, $4 - healthcheck type, $5 - war path
        private static final String PUSH_WITH_MANIFEST = "./cf push -f {0}"; // $0 - manifest.yml path
        private static final String START = "./cf start {0}"; // $0 - application name
        private static final String RESTART = "./cf restart {0}"; // $0 - application name
        private static final String SCALE = "./cf scale {0} -i {1}"; // $0 - application name, $1 - No. instances
        private static final String PING = "https://{0}.{1}/{2}"; // $0 - application name, $1 - public domain, $2 - api
        private static final String DELETE_APP = "./cf delete -r -f {0}"; // $0 - application name
        private static final String DELETE_SERVICE = "./cf delete-service -f {0}"; // $0 - service name
        private static final String LOGOUT = "./cf logout";
    }
}