package com.demo.app.file;

import com.demo.app.user.User;
import com.demo.app.user.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService storageService;
    private final UserService userService;

    FileController(FileService storageService, UserService userService) {
        this.storageService = storageService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                   final HttpServletRequest request) {
        final User user = userService.getUser(request);
        try {
            String path = storageService.store(file, user);
            return ResponseEntity.ok(path);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("file upload failed");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<String> getFiles(final HttpServletRequest request) {
        User user = userService.getUser(request);
        List<String> fileList = storageService.getAllUserFiles(user.getId()).toList();
        return ResponseEntity.ok(fileList.toString());
    }

    @GetMapping("/filename/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource file = storageService.loadAsResource(filename);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{filename:.+}")
    public ResponseEntity<String> handleFileDelete(@PathVariable String filename,
                                                   final HttpServletRequest request) {
        final User user = userService.getUser(request);
        try {
            storageService.delete(filename, user);
            return ResponseEntity.ok("file deleted");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("file delete failed");
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleStorageFileNotFound(RuntimeException exc) {
        return ResponseEntity.notFound().build();
    }

}
