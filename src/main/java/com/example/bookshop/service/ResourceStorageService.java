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


public interface ResourceStorageService {

    String saveNewBookImage(MultipartFile file, String slug) throws IOException;

    Path getBookFilePath(String hash);

    MediaType getBookFileMime(String hash);

    byte[] getBookFileByteArray(String hash) throws IOException;

}
