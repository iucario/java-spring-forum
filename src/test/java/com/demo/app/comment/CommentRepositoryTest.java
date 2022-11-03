package com.demo.app.comment;

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

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    private User savedUser;
    private Post savedPost;
    private Comment savedComment;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "testpassword");
        savedPost = new Post("title", "This is body", savedUser);
        userRepository.save(savedUser);
        postRepository.save(savedPost);
        savedComment = new Comment("This is comment", savedPost, savedUser);
        commentRepository.save(savedComment);
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findByPostId() {
        List<Comment> comments = commentRepository.findByPostId(savedPost.getId(), 0, 100);
        assertEquals(1, comments.size());
    }

    @Test
    void findByUserId() {
        List<Comment> comments = commentRepository.findByUserId(savedUser.getId());
        assertEquals(1, comments.size());
    }

    @Test
    void findByUserName() {
        List<Comment> comments = commentRepository.findByUserName(savedUser.getName());
        assertEquals(1, comments.size());
    }

    @Test
    void findByItemAndUser() {
        List<Comment> comment = commentRepository.findByPostAndUser(savedPost.getId(), savedUser.getId());
        assertEquals(1, comment.size());
    }
}