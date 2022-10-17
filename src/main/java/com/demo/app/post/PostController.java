package com.demo.app.post;

import com.demo.app.user.User;
import com.demo.app.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    private final UserService userService;

    PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping(produces = "application/json")
    public List<PostDto> getAllItems(final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        int offset = 0;
        if (request.getParameter("offset") != null) {
            offset = Integer.parseInt(request.getParameter("offset"));
        }
        return postService.getAll(user.getId(), offset).stream().map(post -> new PostDto(post)).toList();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public PostDto addNewItem(@RequestBody PostDto.ItemCreate itemCreate, final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        Post post = new Post(itemCreate.title, itemCreate.body, user);
        postService.addItem(post);
        return new PostDto(post);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public PostDto updateItem(@RequestBody PostDto.ItemUpdate item, final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        Post i = postService.getById(item.id);
        if (!user.getId().equals(i.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        i.setText(item.body);
        postService.updateItem(i);
        return new PostDto(i);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public String deleteItem(@PathVariable Long id, final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        Post n = postService.getById(id);
        if (!n.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        postService.deleteItemById(id);
        return "Deleted %d".formatted(id);
    }

}
