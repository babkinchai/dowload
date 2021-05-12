package com.example.dowload.services.threads;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyThreadList {
    private static MyThreadList instance = null;
    private final Map<String, MyThread> synmap = Collections.synchronizedMap(new HashMap<>());

    public static MyThreadList getInstance() {
            synchronized (MyThreadList.class) {
                if (instance == null) {
                    instance = new MyThreadList();
                    return instance;
                }
                return instance;
            }
    }

    public void addTh(String key, MyThread myThread) {
        synchronized (MyThreadList.class) {
            if (instance == null) {
                instance = new MyThreadList();
            }
            this.synmap.put(key,myThread);
        }
    }
    public void removeTh(String key) {
        synchronized (MyThreadList.class) {
            if (instance == null) {
                instance = new MyThreadList();
            }
            this.synmap.remove(key);
        }
    }

    public void stopThread(String filename) {
        synmap.get(filename).interrupt();
        System.out.println(filename+" is stoped");
    }
    /*public String startThread(String id) {
           // synmap.get(id).myResume();
        return id;
    }*/
}