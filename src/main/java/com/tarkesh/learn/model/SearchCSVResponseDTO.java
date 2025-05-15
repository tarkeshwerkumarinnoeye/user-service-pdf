package com.tarkesh.learn.model;

public class SearchCSVResponseDTO {
    private String fileType;
    private byte[] file;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
