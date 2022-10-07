package com.demo.app.item;

import org.springframework.lang.NonNull;

import java.util.List;

public class ItemDto {
    public Long id;
    public String text;
    public Long createdAt;
    public Long updatedAt;
    public List<String> images;
    public Long userId;

    public ItemDto(Item item) {
        this.id = item.getId();
        this.text = item.getText();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
        this.images = List.of();
        if (item.getImages() != null) {
            this.images = item.getImages().stream().map(Image::getData).toList();
        }
        this.userId = item.getUser().getId();
    }

    @Override
    public String toString() {
        return String.format("ItemDto[id=%d, text='%s', images='%s', created_time='%d', updated_time='%d']", id,
                text, images, createdAt, updatedAt);
    }

    public static class ItemCreate {
        public String text;
        public String[] images;

        public ItemCreate(String text, String[] images) {
            this.text = text;
            this.images = images;
        }

        @Override
        public String toString() {
            return "ItemCreate[text=" + text + ", images=" + images + "]";
        }
    }

    public static class ItemUpdate {
        @NonNull
        public Long id;
        @NonNull
        public String text;

        public ItemUpdate(String text, Long id) {
            this.text = text;
            this.id = id;
        }

        @Override
        public String toString() {
            return "ItemUpdate[text=" + text + ", id=" + id + "]";
        }
    }

    public static class ItemDelete {
        @NonNull
        public Long id;
        // fixme: com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of
        //  (although at least one Creator exists): cannot deserialize from Object value

        public ItemDelete(Long id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "ItemDelete[id=" + id + "]";
        }
    }
}
