package ru.bfad.bfaApp.bots.configs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:telegram.properties", ignoreResourceNotFound = true)
public class BotConfig {

    @Value("${nameTest}")
//    @Value("${name}")
    String botName;

    @Value("${keyTest}")
//    @Value("${key}")
    String token;
}
