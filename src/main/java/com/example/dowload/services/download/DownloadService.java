package com.example.dowload.services.download;

import com.example.dowload.controller.websocket.WebSocketDown;
import com.example.dowload.dto.websocket.DownloadProc;
import com.example.dowload.services.threads.MySuspend;
import com.example.dowload.services.threads.MyThread;
import com.example.dowload.services.threads.MyThreadList;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static java.lang.System.err;

@Service
@Data
public class DownloadService {

    private final MyThreadList myThreadList;
    private final SimpMessagingTemplate simpMessagingTemplate;
    final private RestTemplate restTemplate;
    private final MySuspend mySuspend;

    @Value("${download.filepath}")
    private String FILE_PATH;
    private static final int HTTP_PARTIAL_CONTENT = 206;
    private static final String HEADER_RANGE = "range";
    private static final String RANGE_FORMAT = "bytes=%d-%d";

    public DownloadService(RestTemplate restTemplate, WebSocketDown webSocketService, SimpMessagingTemplate simpMessagingTemplate, MySuspend mySuspend, MyThreadList myThreadList) {
        this.restTemplate = restTemplate;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.mySuspend = mySuspend;
        this.myThreadList = myThreadList;
    }

    public Responce download(String uri, int firstBytePos, int lastBytePos) throws  IOException, InterruptedException {
        return restTemplate.execute(
                uri,
                HttpMethod.GET,
                clientHttpRequest -> clientHttpRequest.getHeaders().set(
                        HEADER_RANGE,
                        String.format(RANGE_FORMAT, firstBytePos, lastBytePos)),
                clientHttpResponse -> {
                    return new Responce(clientHttpResponse.getBody().readAllBytes(), clientHttpResponse.getStatusCode(), clientHttpResponse.getHeaders());
                });
    }

    public Long contentLenght(String url) throws URISyntaxException, IOException,IllegalArgumentException, HttpClientErrorException {
        HttpHeaders headers = restTemplate.headForHeaders(url);
        return headers.getContentLength();
    }

    public void download(final String url,int chunkSize,String filename) throws InterruptedException, IOException, URISyntaxException {

        int expectiongLength=0;
        try {
            expectiongLength = Math.toIntExact(contentLenght(url));
        }
        catch (IOException | URISyntaxException|IllegalArgumentException |HttpClientErrorException e){
            System.out.println(e);
            return;
        }
        int firstBytePos=0;
        int lastBytePos=chunkSize-1;
        int downloadedSLength=0;
        int attempts=0;
        FileOutputStream file = new FileOutputStream(FILE_PATH+ filename, true);
        while (downloadedSLength < expectiongLength&&!Thread.currentThread().isInterrupted()) {
            long startTime = System.nanoTime();
            Responce responce;
            try {
                responce = download(url, firstBytePos, lastBytePos);
            } catch (IOException e) {
                attempts++;
                err.printf("I/O error has occurred. %s%n", e);
                System.out.printf("Going to do %d attempt%n", attempts);
                continue;
            }
            try {
                byte[] chunkedBytes = responce.bufferedInputStream;
                downloadedSLength += chunkedBytes.length;
                if (isPartial(responce)) {
                    file.write(chunkedBytes);
                    firstBytePos = lastBytePos + 1;
                    lastBytePos = Math.min(lastBytePos + chunkSize, expectiongLength - 1);
                }
                double endTime = ((double) System.nanoTime()-startTime)/1000000000;
                webSocketSpeedSend(filename,chunkedBytes.length,endTime, downloadedSLength, expectiongLength);
            } catch (IOException e) {
                attempts++;
                err.printf("I/O error has occurred. %s%n", e);
            }
        }
        myThreadList.removeTh(filename);
        file.close();
    }

    private boolean isPartial(Responce responce) {
        return responce.status.value()==HTTP_PARTIAL_CONTENT;
    }

    private double speed(double chunkedBytesSize) {
        return (chunkedBytesSize / 1024.0 > 1024.0) ? chunkedBytesSize / 1024.0/1024.0 : chunkedBytesSize/1024.0;
    }

    private void webSocketSpeedSend(String filename, int chunkedBytesSize,double time,int downloadedSLength, int expectiongLength) {
        double speed = speed((double)chunkedBytesSize/time);
        DownloadProc webSockDownloadProc=new DownloadProc(filename,downloadedSLength,speed,expectiongLength);
        simpMessagingTemplate.convertAndSend("/topic/download",webSockDownloadProc);
    }
    
}
