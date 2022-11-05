package com.demo.app.file;

import com.demo.app.auth.AuthService;
import com.demo.app.user.User;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    private final AuthService authService;

    FileController(FileService fileService, AuthService authService) {
        this.fileService = fileService;
        this.authService = authService;
    }

    @PostMapping("/upload")
    public FileDto handleFileUpload(@RequestParam("file") MultipartFile file) {
        final User user = authService.getCurrentUser();
        return fileService.handleUpload(file, user);
    }

    @GetMapping("/list")
    public List<FileDto> getFiles() {
        User user = authService.getCurrentUser();
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
    public ResponseEntity<String> handleFileDelete(@PathVariable String filename) {
        final User user = authService.getCurrentUser();
        try {
            fileService.delete(filename, user);
            return ResponseEntity.ok("file deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("file delete failed");
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleStorageFileNotFound(RuntimeException exc) {
        return ResponseEntity.notFound().build();
    }

}
