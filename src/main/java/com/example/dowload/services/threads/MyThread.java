package com.example.dowload.services.threads;

import com.example.dowload.services.download.DownloadService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Scope("prototype")
@Data
public class MyThread extends Thread {

    private String url;
    private String filename=null;

    @Value("${download.chunkSize}")
    private int chunkSize;

    private final DownloadService downloadService;

    public MyThread(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @Override
    public void run() {
        try {
                downloadService.download(this.url, this.chunkSize, filename);
        } catch (InterruptedException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
