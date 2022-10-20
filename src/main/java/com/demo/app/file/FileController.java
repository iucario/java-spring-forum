package com.demo.app.file;

import com.demo.app.user.User;
import com.demo.app.user.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    private final UserService userService;

    FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public FileDto handleFileUpload(@RequestParam("file") MultipartFile file,
                                    final HttpServletRequest request) {
        final User user = userService.getUser(request);
        return fileService.handleUpload(file, user);
    }

    @GetMapping("/list")
    public List<FileDto> getFiles(final HttpServletRequest request) {
        User user = userService.getUser(request);
        return fileService.getAllUserFiles(user.getId());
    }

    @GetMapping(value = "/filename/{filename:.+}", produces = "application/octet-stream")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource file = fileService.loadAsResource(filename);
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
            fileService.delete(filename, user);
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
