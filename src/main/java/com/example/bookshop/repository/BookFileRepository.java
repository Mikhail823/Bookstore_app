package com.example.bookshop.repository;

import com.example.bookshop.struct.book.file.BookFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookFileRepository extends JpaRepository<BookFileEntity, Integer> {
    BookFileEntity findBookFileEntityByHash(String hash);

}
