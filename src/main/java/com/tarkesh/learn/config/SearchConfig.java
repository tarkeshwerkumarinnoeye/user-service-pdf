package com.tarkesh.learn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchConfig {
    
    @Bean
    public CsvSearchConfig csvSearchConfig() {
        var config = new CsvSearchConfig();
        config.setMaxNumRecordsPerFile(10);
        config.setZipFile("zipFile");
        return config;
    }
}
