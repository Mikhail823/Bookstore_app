package com.example.bookshop.service;

import com.example.bookshop.repository.BookFileRepository;
import com.example.bookshop.struct.book.file.BookFileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class ResourceStorage {
    @Value("${upload.path}/")
    String uploadPath;

    @Value("${download.path}/")
    String downloadPath;

    private final BookFileRepository bookFileRepository;

    @Autowired
    public ResourceStorage(BookFileRepository bookFileRepository) {
        this.bookFileRepository = bookFileRepository;
    }

    public String saveNewBookImage(MultipartFile file, String slug) throws IOException {

        String resourceURI = null;

        if(!file.isEmpty()){
            if(!new File(uploadPath).exists()){
                Files.createDirectories(Paths.get(uploadPath));
            }
            String fileName = slug + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            Path path = Paths.get(uploadPath,fileName);
            resourceURI = "/Users/User/book-covers/" + fileName;
            file.transferTo(path);
        }
        return resourceURI;
    }

    public Path getBookFilePath(String hash) {
        BookFileEntity bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        return Paths.get(bookFile.getPath());
    }

    public MediaType getBookFileMime(String hash) {
        BookFileEntity bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        String mimeType =
                URLConnection.guessContentTypeFromName(Paths.get(bookFile.getPath()).getFileName().toString());
        if (mimeType != null){
            return MediaType.parseMediaType(mimeType);
        }else{
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public byte[] getBookFileByteArray(String hash) throws IOException {
        BookFileEntity bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        Path path = Paths.get(downloadPath, bookFile.getPath());
        return Files.readAllBytes(path);
    }
}
