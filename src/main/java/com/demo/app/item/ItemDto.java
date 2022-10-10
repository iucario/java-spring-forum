package com.demo.app.item;

import org.springframework.lang.NonNull;

import java.util.List;

public class ItemDto {
    public Long id;
    public String title;
    public String body;
    public Long createdAt;
    public Long updatedAt;
    public List<String> images;
    public Long userId;

    public ItemDto(Item item) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.body = item.getText();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
        this.images = List.of();
        this.userId = item.getUser().getId();
    }

    @Override
    public String toString() {
        return String.format("ItemDto[id=%d, title='%s' body=%s created_time='%d', updated_time='%d']", id,
                title, body, createdAt, updatedAt);
    }

    public static class ItemCreate {
        public String title;
        public String body;
        public String[] images;

        public ItemCreate(String body, String[] images) {
            this.body = body;
            this.images = images;
        }

        @Override
        public String toString() {
            return "ItemCreate[title=" + title + ", body=" + body + "]";
        }
    }

    public static class ItemUpdate {
        @NonNull
        public Long id;
        @NonNull
        public String body;

        public ItemUpdate(@NonNull String body, @NonNull Long id) {
            this.body = body;
            this.id = id;
        }

        @Override
        public String toString() {
            return "ItemUpdate[body=" + body + ", id=" + id + "]";
        }
    }

    public static class ItemDelete {
        @NonNull
        public Long id;
        // fixme: com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of
        //  (although at least one Creator exists): cannot deserialize from Object value

        public ItemDelete(@NonNull Long id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "ItemDelete[id=" + id + "]";
        }
    }
}
