package com.example.bookshop.service.impl;


import com.example.bookshop.repository.BookFileRepository;
import com.example.bookshop.service.ResourceStorageService;
import com.example.bookshop.struct.book.file.BookFileEntity;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ResourceStorageServiceImpl implements ResourceStorageService {

    @Value("${upload.path}")
    String uploadPath;

    @Value("${download.path}")
    String downloadPath;

    private final BookFileRepository bookFileRepository;



    @Autowired
    public ResourceStorageServiceImpl(BookFileRepository bookFileRepository) {
        this.bookFileRepository = bookFileRepository;

    }

    @Override
    public String saveNewBookImage(MultipartFile file, String slug) throws IOException {

        String resourceURI = null;

        if (!file.isEmpty()) {
            if (!new File(uploadPath).exists()) {
                Files.createDirectories(Paths.get(uploadPath));
            }
            String fileName = slug + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            Path path = Paths.get(uploadPath, fileName);
            resourceURI = "/Users/User/book-covers/" + fileName;
            file.transferTo(path);
        }
        return resourceURI;
    }

    @Override
    public Path getBookFilePath(String hash) {
        BookFileEntity bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        return Paths.get(bookFile.getPath());
    }

    @Override
    public MediaType getBookFileMime(String hash) {
        BookFileEntity bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        String mimeType =
                URLConnection.guessContentTypeFromName(Paths.get(bookFile.getPath()).getFileName().toString());
        if (mimeType != null) {
            return MediaType.parseMediaType(mimeType);
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    @Override
    public byte[] getBookFileByteArray(String hash) throws IOException {
        BookFileEntity bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        Path path = Paths.get(downloadPath, bookFile.getPath());
        return Files.readAllBytes(path);
    }

}
