package com.andonov.cloud.availability.util;

import com.andonov.cloud.availability.constants.LogType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {

    private final InputStream is;
    private final String type;
    private final StringBuilder result;

    StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
        this.result = new StringBuilder();
    }

    @Override
    public void run() {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                print(line);
                result.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert is != null;
                is.close();
                isr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void print(String content) {
        switch (type) {
            case LogType.ERROR:
                System.err.println(content);
                break;
            case LogType.INFO:
            default:
                System.out.println(content);
                break;
        }
    }

    String getResult() {
        return result.toString();
    }
}