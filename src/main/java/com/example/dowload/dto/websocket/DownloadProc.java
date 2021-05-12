package com.example.dowload.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownloadProc {

    private String name;
    private int currentSize;
    private double speed;
    private int fullSize;
}
