package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String index(@RequestParam(value = "lang", required = false) String lang, Model model, Locale locale) {
        String language = (lang != null && !lang.isBlank()) ? lang : locale.getLanguage();
        log.info("Rendering home page for locale: {}", language);

        List<TimelinePost> timelinePosts = createTimelinePosts(language);
        model.addAttribute("timelinePosts", timelinePosts);

        if (lang != null && !lang.isBlank()) {
            log.debug("Language override requested: {}", lang);
        }

        return "index";
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
