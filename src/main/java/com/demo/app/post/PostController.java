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
    public List<PostDto> getAllPosts(final HttpServletRequest request) {
        final User user = userService.getUser(request);
        int offset = 0;
        if (request.getParameter("offset") != null) {
            offset = Integer.parseInt(request.getParameter("offset"));
        }
        return postService.getAllPosts(user.getId(), offset, 100);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public PostDto addNewPost(@RequestBody PostDto.PostCreate postCreate, final HttpServletRequest request) {
        final User user = userService.getUser(request);
        Post post = new Post(postCreate.title, postCreate.body, user);
        return postService.addPost(post);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public PostDto updatePost(@RequestBody PostDto.PostUpdate postUpdate, final HttpServletRequest request) {
        final User user = userService.getUser(request);
        Post post = postService.getById(postUpdate.id);
        if (!user.getId().equals(post.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return postService.updatePost(postUpdate);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public String deletePost(@PathVariable Long id, final HttpServletRequest request) {
        final User user = userService.getUser(request);
        Post n = postService.getById(id);
        if (!n.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        postService.deletePostById(id);
        return "Deleted %d".formatted(id);
    }

}
