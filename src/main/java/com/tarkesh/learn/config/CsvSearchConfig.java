package com.tarkesh.learn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class CsvSearchConfig {
    private Integer maxNumRecordsPerFile;
    private String zipFile;

    public Integer getMaxNumRecordsPerFile() {
        return maxNumRecordsPerFile;
    }

    public void setMaxNumRecordsPerFile(Integer maxNumRecordsPerFile) {
        this.maxNumRecordsPerFile = maxNumRecordsPerFile;
    }

    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }
}