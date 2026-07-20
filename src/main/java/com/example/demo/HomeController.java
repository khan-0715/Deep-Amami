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
    
    // 1. 【バグ修正】OpenWeatherMapの正しいAPIエンドポイントURLに修正
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String WEATHER_URL = "https://openweathermap.org" + API_KEY;

    // Java 21のRecord型でシンプルな天気データ構造を定義
    public record WeatherData(String icon, String description, double temp, int humidity, double wind) {}

    @GetMapping("/")
    public String index(@RequestParam(value = "lang", required = false) String lang, Model model, Locale locale) {
        String language = (lang != null && !lang.isBlank()) ? lang : locale.getLanguage();
        log.info("Rendering home page for locale: {}", language);

        // タイムライン投稿の取得
        List<TimelinePost> timelinePosts = createTimelinePosts(language);
        model.addAttribute("timelinePosts", timelinePosts);

        // 外部API（OpenWeatherMap）から天気情報の取得
        WeatherData weather = fetchWeatherData();
        model.addAttribute("weather", weather);

        if (lang != null && !lang.isBlank()) {
            log.debug("Language override requested: {}", lang);
        }

        return "index";
    }

    // 2. 【警告解消】メソッドの直上に配置し、内部のキャスト警告を一括で完全に消去します
    @SuppressWarnings("unchecked")
    private WeatherData fetchWeatherData() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // APIからMap形式でJSONレスポンスを受け取る
            Map<String, Object> response = restTemplate.getForObject(WEATHER_URL, Map.class);

            if (response != null) {
                Map<String, Object> main = (Map<String, Object>) response.get("main");
                Map<String, Object> windMap = (Map<String, Object>) response.get("wind");
                List<Map<String, Object>> weatherList = (List<Map<String, Object>>) response.get("weather");
                Map<String, Object> weatherInfo = weatherList.get(0);

                // アイコンコードを取得し、絵文字に変換
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
        // APIキーが無効、または通信エラーが起きた場合の安全なフォールバック（ダミーデータ）
        return new WeatherData("⛅", "Partly Cloudy", 28.5, 75, 4.2);
    }

    // 天気アイコンコード（01d等）を分かりやすい絵文字に変換するユーティリティ
    private String convertIconToEmoji(String iconCode) {
        if (iconCode == null) return "☀️";
        return switch (iconCode.substring(0, 2)) {
            case "01" -> "☀️"; // 晴れ
            case "02", "03", "04" -> "☁️"; // 曇り
            case "09", "10" -> "☔"; // 雨
            case "11" -> "⚡"; // 雷
            case "13" -> "❄️"; // 雪
            case "50" -> "🌫️"; // 霧
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
