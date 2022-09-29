package com.demo.app.service;

import com.demo.app.repository.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAll(Long userId);

    Item getById(Long id);

    Item addItem(Item item);

    void deleteItemById(Long id);

    void updateItem(Item item);
}
