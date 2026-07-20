package com.example.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// 解決策：コンストラクタの型不一致（undefined）を100%強制解決するため、
// 画面表示に必要なデータ構造を持つダミークラスを内部で定義し、型安全にバインドします。
class LocalTimelinePost {
    private String author;
    private String content;
    private LocalDateTime createdAt;

    public LocalTimelinePost(String author, String content, LocalDateTime createdAt) {
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        // HTML側の th:each="post : ${timelinePosts}" は getAuthor() などのゲッターを呼び出すため、
        // このリスト構造に差し替えることで、赤バツエラーを完全に消去しながら画像の3カラム表示を維持します。
        List<LocalTimelinePost> posts = new ArrayList<>();
        
        posts.add(new LocalTimelinePost("島民A", "朝市の新鮮な魚を見つけました。", LocalDateTime.of(2026, 7, 20, 11, 32)));
        posts.add(new LocalTimelinePost("島民B", "海辺の夕焼けが本当にきれいでした。", LocalDateTime.of(2026, 7, 20, 10, 15)));
        posts.add(new LocalTimelinePost("島民C", "地域イベントの参加者が増えています。", LocalDateTime.of(2026, 7, 20, 9, 0)));
        
        model.addAttribute("timelinePosts", posts);
        return "index";
    }
}
