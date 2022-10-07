package com.demo.app.item;

import java.util.List;

public interface ItemService {
    List<Item> getAll(Long userId, int offset);

    Item getById(Long id);

    Item addItem(Item item);

    void deleteItemById(Long id);

    void updateItem(Item item);
}
