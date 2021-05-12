package com.example.dowload.services.threads;

import org.springframework.stereotype.Component;

@Component
public class MySuspend {

    private boolean suspendFlag=false;

    public boolean isSuspendFlag() {
        return suspendFlag;
    }

    public synchronized boolean mySuspend() {
        return suspendFlag = true;
    }

}
