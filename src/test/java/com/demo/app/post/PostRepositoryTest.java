package com.demo.app.post;

import com.demo.app.user.User;
import com.demo.app.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    private User savedUser;
    private Post savedPost;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "testpassword");
        savedPost = new Post("title", "This is body", savedUser);
        userRepository.save(savedUser);
        postRepository.save(savedPost);
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAll() {
        // FIXME: can't use offset and limit together. Why?
        List<Post> posts = postRepository.findAll(savedUser.getId(), 100);
        assertEquals(1, posts.size());
    }

    @Test
    void countAll() {
        int count = postRepository.countAll(savedUser.getId());
        assertEquals(1, count);
    }
}