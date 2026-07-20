package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String WEATHER_URL = "https://openweathermap.org" + API_KEY;

    // Java 21のRecord型でシンプルなデータ構造を定義
    public record WeatherData(String icon, String description, double temp, int humidity, double wind) {}
    // 💡【追加】記事用のデータ構造を定義
    public record Article(Long id, String title, String category, String summary) {}

    @GetMapping("/")
    public String index(@RequestParam(value = "lang", required = false) String lang, Model model, Locale locale) {
        String language = (lang != null && !lang.isBlank()) ? lang : locale.getLanguage();
        log.info("Rendering home page for locale: {}", language);

        // 1. タイムライン投稿の取得
        List<TimelinePost> timelinePosts = createTimelinePosts(language);
        model.addAttribute("timelinePosts", timelinePosts);

        // 2. 外部API（OpenWeatherMap）から天気情報の取得
        WeatherData weather = fetchWeatherData();
        model.addAttribute("weather", weather);

        // 💡【追加】3. 記事データの取得とモデルへの格納
        List<Article> articles = createDummyArticles(language);
        model.addAttribute("articles", articles);

        if (lang != null && !lang.isBlank()) {
            log.debug("Language override requested: {}", lang);
        }

        return "index";
    }

    // 💡【追加】記事のダミーデータを生成するメソッド
    private List<Article> createDummyArticles(String language) {
        if ("en".equalsIgnoreCase(language)) {
            return List.of(
                new Article(1L, "Mystic Mangrove Forests", "Nature", "An immersive guide to kayaking through Amami's pristine mangrove sub-tropical ecosystems."),
                new Article(2L, "The Art of Oshima Tsumugi", "Culture", "Exploring the 1,300-year history of mud-dyeing and the master weavers keeping the tradition alive."),
                new Article(3L, "Traditional Keihan Rice Recipe", "Food", "Discover the rich history behind Amami's most celebrated chicken rice dish.")
            );
        }
        return List.of(
            new Article(1L, "神秘のマングローブ原生林", "自然", "奄美の亜熱帯エコシステムをカヤックで巡る完全ガイド。"),
            new Article(2L, "大島紬を紡ぐ、泥染めの芸術", "文化", "1300年の歴史を持つ泥染めの伝統技術と、それを守る職人たちの物語。"),
            new Article(3L, "伝統料理「鶏飯（けいはん）」のルーツ", "グルメ", "かつて薩摩藩の役人もてなしたとされる、奄美随一の郷土料理の歴史。")
        );
    }

    @SuppressWarnings("unchecked")
    private WeatherData fetchWeatherData() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(WEATHER_URL, Map.class);

            if (response != null) {
                Map<String, Object> main = (Map<String, Object>) response.get("main");
                Map<String, Object> windMap = (Map<String, Object>) response.get("wind");
                List<Map<String, Object>> weatherList = (List<Map<String, Object>>) response.get("weather");
                Map<String, Object> weatherInfo = weatherList.get(0);

                String iconCode = (String) weatherInfo.get("icon");
                String iconEmoji = convertIconToEmoji(iconCode);
                
                String description = (String) weatherInfo.get("description");
                double temp = ((Number) main.get("temp")).doubleValue();
                int humidity = ((Number) main.get("humidity")).intValue();
                double wind = ((Number) windMap.get("speed")).doubleValue();

                return new WeatherData(iconEmoji, description, temp, humidity, wind);
            }
        } catch (Exception e) {
            log.error("Failed to fetch weather data from OpenWeatherMap", e);
        }
        return new WeatherData("⛅", "Partly Cloudy", 28.5, 75, 4.2);
    }

    private String convertIconToEmoji(String iconCode) {
        if (iconCode == null) return "☀️";
        return switch (iconCode.substring(0, 2)) {
            case "01" -> "☀️";
            case "02", "03", "04" -> "☁️";
            case "09", "10" -> "☔";
            case "11" -> "⚡";
            case "13" -> "❄️";
            case "50" -> "🌫️";
            default -> "☀️";
        };
    }

    private List<TimelinePost> createTimelinePosts(String language) {
        if ("en".equalsIgnoreCase(language)) {
            return List.of(
                    new TimelinePost(1001L, "Resident A", "The fresh fish at the morning market looked wonderful.", LocalDateTime.now().minusMinutes(10)),
                    new TimelinePost(1002L, "Resident B", "The sunset over the sea was breathtaking.", LocalDateTime.now().minusHours(1)),
                    new TimelinePost(1003L, "Resident C", "More visitors are joining the local festival this year.", LocalDateTime.now().minusHours(3)));
        }
        return List.of(
                new TimelinePost(1001L, "島民A", "朝市の新鮮な魚を見つけました。", LocalDateTime.now().minusMinutes(10)),
                new TimelinePost(1002L, "島民B", "海辺の夕焼けが本当にきれいでした。", LocalDateTime.now().minusHours(1)),
                new TimelinePost(1003L, "島民C", "地域イベントの参加者が増えています。", LocalDateTime.now().minusHours(3)));
    }
}
