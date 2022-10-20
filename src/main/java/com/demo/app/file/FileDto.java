package com.demo.app.file;

public class FileDto {
    public String name;
    public String url;
    public Long createdAt;
    public String username;

    public FileDto(FileEntity file) {
        this.name = file.getName();
        this.url = "/file/filename/" + file.getName();
        this.createdAt = file.getCreatedAt();
        this.username = file.getUser().getName();
    }

    @Override
    public String toString() {
        return String.format("FileDto[name=%s, url=%s, created_at=%d, username=%d]", name, url, createdAt, username);
    }
}
