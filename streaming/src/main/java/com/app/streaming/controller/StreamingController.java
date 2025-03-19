package com.app.streaming.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
public class StreamingController {

    @GetMapping("/hello")
    public String hello(){
        return "HELLO STREAMING";
    }

    @GetMapping("fetch_video_chunk")
    public ResponseEntity<List<String>> getVideoChunk(@RequestParam int start, @RequestParam int count) {
        List<String> videoChunkUrls = new ArrayList<>();

        for (int i = start; i < start + count; i++) {
            File file = new File("D:\\Streaming-App-Data\\Streaming-App-Resized-Video\\UserID-1\\07fcd269-570c-4838-8708-3affb5582ecd\\1440p\\" + i + ".mp4");
            if (file.exists()) {
                String chunkUrl = "/video_chunk?index=" + i; // Update based on your actual endpoint
                videoChunkUrls.add(chunkUrl);
            }
        }

        if (videoChunkUrls.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(videoChunkUrls);
    }
}
