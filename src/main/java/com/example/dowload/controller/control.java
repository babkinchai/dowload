package com.example.dowload.controller;

import com.example.dowload.dto.websocket.DownStatus;
import com.example.dowload.services.threads.AsynchronouseService;
import com.example.dowload.services.threads.MyThreadList;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class control {

    private final MyThreadList myThreadList;
    private final AsynchronouseService asynchronouseService;

    public control(AsynchronouseService asynchronouseService, MyThreadList myThreadList) {
        this.asynchronouseService = asynchronouseService;
        this.myThreadList = myThreadList;
    }

    @MessageMapping("/pause")
    public void pauseDown(DownStatus name) {
        myThreadList.stopThread(name.getName());
    }

    @MessageMapping("/resume")
    public void resumeDown(DownStatus  name) {
        myThreadList.stopThread(name.getName());
    }

    @GetMapping("/stop/{id}")
    public String stop(@PathVariable String id) {
        myThreadList.stopThread(id);
        return "thread №"+id+"stop";
    }
/*    @GetMapping("/start/{id}")
    public String start(@PathVariable String id) {
        myThreadList.startThread(id);
        return "thread №"+id+"start";
    }*/

    @GetMapping("/download")
    public String download(@RequestParam(value = "url",required = false) String url) {
        asynchronouseService.setTaskExecutor(url);
        return "thread №"+"start";
    }
}
