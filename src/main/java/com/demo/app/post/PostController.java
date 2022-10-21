package com.demo.app.post;

import com.demo.app.auth.AuthService;
import com.demo.app.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    private final AuthService authService;

    PostController(PostService postService, AuthService authService) {
        this.postService = postService;
        this.authService = authService;
    }

    @GetMapping(produces = "application/json")
    public List<PostDto> getUserPosts(final HttpServletRequest request) {
        final User user = authService.getUser(request);
        int offset = 0;
        if (request.getParameter("offset") != null) {
            offset = Integer.parseInt(request.getParameter("offset"));
        }
        return postService.getUserPosts(user.getId(), offset, 100);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public PostDto addNewPost(@RequestBody PostDto.PostCreate postCreate, final HttpServletRequest request) {
        final User user = authService.getUser(request);
        Post post = new Post(postCreate.title, postCreate.body, user);
        return postService.addPost(post);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public PostDto updatePost(@RequestBody PostDto.PostUpdate postUpdate, final HttpServletRequest request) {
        final User user = authService.getUser(request);
        Post post = postService.getById(postUpdate.id);
        if (!user.getId().equals(post.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return postService.updatePost(postUpdate);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public String deletePost(@PathVariable Long id, final HttpServletRequest request) {
        final User user = authService.getUser(request);
        Post n = postService.getById(id);
        if (!n.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        postService.deletePostById(id);
        return "Deleted %d".formatted(id);
    }

}
