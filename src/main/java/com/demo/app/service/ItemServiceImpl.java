package com.demo.app.service;

import com.demo.app.repository.Item;
import com.demo.app.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<Item> getAll(Long userId) {
        return (List<Item>) itemRepository.getAll(userId);
    }

    @Override
    public Item getById(Long id) {
        Optional<Item> optional = itemRepository.findById(id);
        Item item = null;
        if (optional.isPresent()) {
            item = optional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found for id :: " + id);
        }
        return item;
    }

    @Override
    public Item addItem(Item item) {
        return this.itemRepository.save(item);
    }

    @Override
    public void deleteItemById(Long id) {
        this.itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateItem(Item item) {
        itemRepository.findById(item.getId()).ifPresent(existingItem -> {
            existingItem.setText(item.getText());
            existingItem.updateTime();
        });
    }
}
