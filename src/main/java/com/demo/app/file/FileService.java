package com.demo.app.file;

import com.demo.app.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private FileRepository fileRepository;

    String store(MultipartFile multipartFile, User user) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String extension = fileExtension(multipartFile.getOriginalFilename());
        String filename = uuid + extension;
        String path = Paths.get(uploadDir, filename).toString();
        System.out.println("=> path: " + path);
        multipartFile.transferTo(new File(path));
        fileRepository.save(new FileUpload(filename, path, user));
        return path;
    }

    Stream<String> loadAll(User user) {
        return fileRepository.findAllByUser(user.getId()).stream().map(FileUpload::getUrl);
    }

    Resource loadAsResource(String filename) throws FileNotFoundException {
        String path = Paths.get(uploadDir, filename).toString();
        InputStream inputStream = new FileInputStream(path);
        return new InputStreamResource(inputStream);
    }

    void delete(String filename, User user) { // TODO: better error handling
        FileUpload fileUpload = fileRepository.findByName(filename).orElse(null);
        if (fileUpload == null) {
            throw new RuntimeException("file not found");
        }
        if (!fileUpload.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        String path = Paths.get(uploadDir, filename).toString();
        File file = new File(path);
        if (file.isFile()) {
            file.delete();
            fileRepository.delete(fileUpload);
        } else {
            throw new RuntimeException("Not a file");
        }
    }

    private String fileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

}