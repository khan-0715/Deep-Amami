package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull; // 追加：SpringのNonNullアノテーション
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 1. デフォルトの言語を「日本語」に固定し、セッションで管理する設定
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.JAPANESE);
        return slr;
    }

    // 2. URLの「?lang=xx」を検知して言語を切り替えるインターセプターの設定
    @Bean
    @NonNull // 👈【24行目に追加】これでこのメソッドは絶対にNullを返さないとVS Codeに証明します
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    // 3. 上記の設定をSpring Bootのシステムに登録する
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) { // 修正：@NonNull を追加
        LocaleChangeInterceptor lci = localeChangeInterceptor();
        registry.addInterceptor(lci); // 👈【38行目の警告箇所】lciがNonNullになったため、ここでエラーが消えます
    }
}
