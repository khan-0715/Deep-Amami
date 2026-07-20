package com.example.demo;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matcher; // 追加：型明示のためにインポート
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HomeController.class)
@Import(WebConfig.class)
@SuppressWarnings("null") // 追加：外部検証ライブラリ（Hamcrest）のジェネリクス境界によるNull型安全性のミスマッチを抑制
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void indexPageRendersLocalizedContent() throws Exception {
        // 型推論の曖昧さを排除するため、明示的な型を持つローカル変数として定義
        Matcher<String> matcher = containsString("THE DEPTH OF JAPAN THAT GUIDEBOOKS NEVER REACH");
        
        mockMvc.perform(get("/").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string(matcher));
    }

    @Test
    void indexPageUsesEnglishTimelineContent() throws Exception {
        Matcher<String> matcher = containsString("The sunset over the sea was breathtaking.");

        mockMvc.perform(get("/").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string(matcher));
    }

    @Test
    void indexPageRendersPolishedEnglishCopy() throws Exception {
        Matcher<String> matcher = containsString("Local voices and everyday stories keep the community connected.");

        mockMvc.perform(get("/").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string(matcher));
    }

    @Test
    void indexPageRendersJapaneseCopyForJapaneseLocale() throws Exception {
        Matcher<String> matcher = containsString("奄美のまだ見ぬ側面を発見する");

        mockMvc.perform(get("/").param("lang", "ja"))
                .andExpect(status().isOk())
                .andExpect(content().string(matcher));
    }
}
