package com.example.dowload.services.download;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Data
public class Responce {
    final byte [] bufferedInputStream;
    final HttpStatus status;
    final HttpHeaders headers;
}
