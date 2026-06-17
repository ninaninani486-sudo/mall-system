package com.delaytask.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImageProxyController {

    @GetMapping("/image/proxy")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            conn.setRequestProperty("Referer", "https://www.baidu.com/");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();

            InputStream is = conn.getInputStream();
            byte[] imageBytes = is.readAllBytes();
            is.close();

            String contentType = conn.getContentType();
            if (contentType == null) contentType = "image/jpeg";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl(CacheControl.maxAge(java.time.Duration.ofHours(24)).getHeaderValue());

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
}
