package com.example.bookshop.struct.book.file;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.enums.BookFileType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "book_file")
@Setter
@Getter
public class BookFileEntity implements Serializable {

    private  static  final Long  serialVersionUID  =  1905122041950289547L ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;
    @Column(name = "type_id")
    private int typeId;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String path;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private BookEntity bookId;

    public String getBookExtensionString(){
        return BookFileType.getExtensionStringByType(typeId);
    }

}
