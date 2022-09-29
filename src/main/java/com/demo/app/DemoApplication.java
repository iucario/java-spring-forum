package com.demo.app;

import com.demo.app.repository.Item;
import com.demo.app.repository.User;
import com.demo.app.service.ItemService;
import com.demo.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@SpringBootApplication
@RestController
public class DemoApplication {
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @SuppressWarnings("rawtypes")
    @Bean
    public FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/api/*");

        return registrationBean;
    }


    @GetMapping(value = "/")
    public String index() {
        return "Hello";
    }


    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public String addNewUser(@RequestBody User.UserCreate user) throws Exception {
        User n = new User(user.name, user.password);
        userService.saveUser(n);
        return "Saved";
    }

    @GetMapping(value = "/items", produces = "application/json")
    public List<Item> getAllItems(@RequestParam(value = "userid") Long userId) throws Exception {
        return itemService.getAll(userId);
    }

    @PostMapping(value = "/items", consumes = "application/json", produces = "application/json")
    public Item addNewItem(@RequestBody Item.ItemCreate item) throws Exception {
        User user = userService.getById(item.user_id);
        Item n = new Item(item.text, item.images, user);
        itemService.addItem(n);
        return n;
    }

    @PutMapping(value = "/items", consumes = "application/json", produces = "application/json")
    public Item updateItem(@RequestBody Item.ItemUpdate item) throws Exception {
        Item n = itemService.getById(item.item_id);
        if (n.getUser().getId() != item.user_id) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        n.setText(item.text);
        itemService.updateItem(n);
        return n;
    }

    @DeleteMapping(value = "/items", consumes = "application/json")
    public String deleteItem(@RequestBody Item.ItemUpdate item) throws Exception {
        Item n = itemService.getById(item.item_id);
        if (n.getUser().getId() != item.user_id) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        itemService.deleteItemById(item.item_id);
        return "Deleted";
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}