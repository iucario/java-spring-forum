package com.demo.app.item;

import com.demo.app.user.User;
import com.demo.app.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @GetMapping(produces = "application/json")
    public List<Item> getAllItems(@RequestParam(value = "userid") Long userId) throws Exception {
        return itemService.getAll(userId);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Item addNewItem(@RequestBody Item.ItemCreate item) throws Exception {
        User user = userService.getById(item.user_id);
        Item n = new Item(item.text, item.images, user);
        itemService.addItem(n);
        return n;
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public Item updateItem(@RequestBody Item.ItemUpdate item) throws Exception {
        Item n = itemService.getById(item.item_id);
        if (n.getUser().getId() != item.user_id) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        n.setText(item.text);
        itemService.updateItem(n);
        return n;
    }

    @DeleteMapping(consumes = "application/json")
    public String deleteItem(@RequestBody Item.ItemUpdate item) throws Exception {
        Item n = itemService.getById(item.item_id);
        if (n.getUser().getId() != item.user_id) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        itemService.deleteItemById(item.item_id);
        return "Deleted";
    }

}
