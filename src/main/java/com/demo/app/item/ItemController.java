package com.demo.app.item;

import com.demo.app.user.User;
import com.demo.app.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @GetMapping(produces = "application/json")
    public List<Item> getAllItems(final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        int offset = 0;
        if (request.getParameter("offset") != null) {
            offset = Integer.parseInt(request.getParameter("offset"));
        }
        return itemService.getAll(user.getId(), offset);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ItemDto addNewItem(@RequestBody ItemDto.ItemCreate itemCreate, final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        Item item = new Item(itemCreate.title, itemCreate.body, user);
        itemService.addItem(item);
        return new ItemDto(item);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public ItemDto updateItem(@RequestBody ItemDto.ItemUpdate item, final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        Item i = itemService.getById(item.id);
        if (!user.getId().equals(i.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        i.setText(item.body);
        itemService.updateItem(i);
        return new ItemDto(i);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public String deleteItem(@PathVariable Long id, final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        Item n = itemService.getById(id);
        if (!n.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        itemService.deleteItemById(id);
        return "Deleted %d".formatted(id);
    }

}
