package com.tarkesh.learn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CwmImSearchMsConfig {
    
    private CsvSearchConfig csvSearchConfig;

    @Autowired
    public CwmImSearchMsConfig(CsvSearchConfig csvSearchConfig) {
        this.csvSearchConfig = csvSearchConfig;
    }

    public CsvSearchConfig getCsvSearchConfig() {
        return csvSearchConfig;
    }

    public void setCsvSearchConfig(CsvSearchConfig csvSearchConfig) {
        this.csvSearchConfig = csvSearchConfig;
    }   
}
