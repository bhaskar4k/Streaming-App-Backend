package com.app.streaming.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @GetMapping(produces = "video/mp4")
    public ResponseEntity<Resource> getVideoChunk(@RequestParam int start, @RequestParam int count) {
        List<FileSystemResource> videoChunks = new ArrayList<>();

        for (int i = start; i < start + count; i++) {
            File file = new File("D:\\Streaming-App-Data\\Streaming-App-Resized-Video\\UserID-1\\07fcd269-570c-4838-8708-3affb5582ecd\\1440p\\" + i + ".mp4");
            if (file.exists()) {
                videoChunks.add(new FileSystemResource(file));
            }
        }

        if (videoChunks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .body(videoChunks.get(0)); // Modify for seamless streaming
    }
}
