package com.example.demo;

import java.time.LocalDateTime;

public record TimelinePost(Long id, String author, String content, LocalDateTime createdAt) {
}
