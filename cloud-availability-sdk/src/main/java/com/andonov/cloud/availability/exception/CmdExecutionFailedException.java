package com.andonov.cloud.availability.exception;

public class CmdExecutionFailedException extends RuntimeException {

    public CmdExecutionFailedException(String cmd, String error){
        super(String.format("The execution of cmd: [%s] failed with: %s", cmd, error));
    }
}
