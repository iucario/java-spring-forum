package com.demo.app.post;

import com.demo.app.auth.AuthService;
import com.demo.app.user.User;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(PostService.class);

    private final PostService postService;
    private final AuthService authService;

    public PostController(PostService postService, AuthService authService) {
        this.postService = postService;
        this.authService = authService;
    }

    @GetMapping(produces = "application/json")
    public List<PostDto.PostListDto> getPostList(@RequestParam(required = false) Integer offset,
                                                 @RequestParam(required = false) Integer size) {
        return postService.getPostList(offset, size);
    }

    @GetMapping(value = "/user/{id}", produces = "application/json")
    public List<PostDto> getUserPosts(@PathVariable Long id,
                                      @RequestParam(required = false, defaultValue = "0") Integer offset,
                                      @RequestParam(required = false, defaultValue = "20") Integer size) {
        int validOffset = Math.max(0, offset);
        int validSize = Math.max(offset, size);
        return postService.getUserPosts(id, validOffset, validSize);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public PostDto.PostDetail getPostDetail(@PathVariable Long id) {
        return postService.getPostDetail(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public PostDto addNewPost(@RequestBody PostDto.PostCreate postCreate) {
        final User user = authService.getCurrentUser();
        Post post = new Post(postCreate.title, postCreate.body, user);
        return postService.addPost(post, user);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public PostDto updatePost(@RequestBody PostDto.PostUpdate postUpdate) {
        final User user = authService.getCurrentUser();
        logger.info("Updating post %s".formatted(postUpdate));
        switch (postUpdate.action) {
            case "favorite":
                postService.favoritePost(user, postUpdate.id);
                break;
            case "unfavorite":
                postService.unfavoritePost(user, postUpdate.id);
                break;
            case "update":
                return postService.updatePost(postUpdate, user);
            default:
                break;
        }
        return null;
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity deletePost(@PathVariable Long id) {
        final User user = authService.getCurrentUser();
        postService.deletePostById(id, user);
        return ResponseEntity.noContent().build();
    }

}
