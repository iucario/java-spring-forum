package com.demo.app.file;

import com.demo.app.user.User;
import com.demo.app.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @Mock
    FileRepository fileRepository;
    @Mock
    UserService userService;
    private FileService fileService;
    private User savedUser;
    private FileEntity savedFile;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "testpassword");
        savedUser.setId(1L);
        String uploadDir = System.getProperty("java.io.tmpdir");
        fileService = new FileService(fileRepository, uploadDir, userService);
        savedFile = new FileEntity("testname", "url", savedUser);
    }

    @Test
    void getAllUserFiles() {
        fileService.getAllUserFiles(1L);
        verify(fileRepository).findAllByUser(1L);
    }
}