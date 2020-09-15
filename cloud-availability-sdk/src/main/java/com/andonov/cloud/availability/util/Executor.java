package com.andonov.cloud.availability.util;

import com.andonov.cloud.availability.constants.LogType;
import com.andonov.cloud.availability.exception.CmdExecutionFailedException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Executor {

    public static String exec(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(cmd);

            StreamGobbler infoGobbler = new StreamGobbler(p.getInputStream(), LogType.INFO);
            infoGobbler.start();

            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), LogType.ERROR);
            errorGobbler.start();

            int exitCode = p.waitFor();

            infoGobbler.join();
            errorGobbler.join();

            String errorResult = errorGobbler.getResult();
            String infoResult = infoGobbler.getResult();

            boolean isFailed = exitCode != 0;
            boolean hasErrorStreamFailing = hasError(errorResult);
            boolean hasInfoStreamFailing = hasError(infoResult);

            if (isFailed || hasErrorStreamFailing || hasInfoStreamFailing) {
                StringBuilder builder = new StringBuilder();

                if (isFailed) {
                    builder.append("exit code: ").append(exitCode);
                }

                if (hasErrorStreamFailing) {
                    builder.append(" ").append(errorResult.replaceAll("\n", " ").trim());
                } else if (hasInfoStreamFailing) {
                    builder.append(" ").append(infoResult.replaceAll("\n", " ").trim());
                }

                throw new CmdExecutionFailedException(cmd, builder.toString());
            }

            return infoResult;

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Failed to execute command: " + cmd, e);
        }

    }

    private static boolean hasError(String input) {
        final String checkStr = input.toLowerCase();
        return checkStr.contains("failed") || checkStr.contains("error") || checkStr.contains("fatal");
    }
}
