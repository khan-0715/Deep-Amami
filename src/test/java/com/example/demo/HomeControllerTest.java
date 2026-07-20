package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("unchecked") // 警告解決策：Eclipse/VS Codeの厳格なNull分析によるMatcherの型変換警告を抑制
    @Test
    public void testLanguageSwitching() throws Exception {
        
        // 1. 日本語環境のテスト：画像の通りの美しいレイアウト文字がバインドされているか検証
        mockMvc.perform(get("/").param("lang", "ja"))
                .andExpect(status().isOk())
                .andExpect(content().string((Matcher<String>) CoreMatchers.containsString("生きている島の物語")));

        // 2. 英語環境のテスト：日・英の切り替えバインディングが壊れていないか検証
        mockMvc.perform(get("/").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string((Matcher<String>) CoreMatchers.containsString("A living island story")));
    }
}
