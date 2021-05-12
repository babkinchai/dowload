package com.example.dowload.controller.websocket;

import com.example.dowload.dto.websocket.DownloadProc;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
@Service
public class WebSocketDown {
    private final SimpMessagingTemplate template;

    public WebSocketDown(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void senMes(DownloadProc webSockDownloadProc) {
        this.template.convertAndSend(webSockDownloadProc);
    }
}

