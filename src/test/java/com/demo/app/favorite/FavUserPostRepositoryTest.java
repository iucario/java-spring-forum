package com.demo.app.favorite;

import com.demo.app.post.Post;
import com.demo.app.post.PostRepository;
import com.demo.app.user.User;
import com.demo.app.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class FavUserPostRepositoryTest {
    @Autowired
    FavUserPostRepository favUserPostRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    User savedUser;
    Post savedPost;

    @BeforeEach
    void setUp() {
        savedUser = new User("name", "password");
        userRepository.save(savedUser);
        savedPost = new Post("title", "post body", savedUser);
        postRepository.save(savedPost);
        FavUserPost favUserPost = new FavUserPost(savedUser, savedPost);
        favUserPostRepository.save(favUserPost);
    }

    @AfterEach
    void tearDown() {
        favUserPostRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByUser() {
        List<FavUserPost> maybe = favUserPostRepository.findByUser(savedUser.getId());
        assertNotNull(maybe);
        assertEquals(1, maybe.size());
    }

    @Test
    void findByPost() {
        List<FavUserPost> maybe = favUserPostRepository.findByPost(savedPost.getId());
        assertNotNull(maybe);
        assertEquals(1, maybe.size());
    }
}