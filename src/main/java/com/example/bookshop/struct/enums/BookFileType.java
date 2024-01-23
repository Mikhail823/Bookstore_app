package com.example.bookshop.struct.enums;

public enum BookFileType {

    PDF(".pdf"),
    EPUB(".epub"),
    FB2(".fb2");

    private final String fileExtensionString;


    BookFileType(String fileExtensionString) {
        this.fileExtensionString = fileExtensionString;
    }

    public static String getExtensionStringByType(Integer typeId){
        switch (typeId){
            case 1: return PDF.fileExtensionString;
            case 2: return EPUB.fileExtensionString;
            case 3: return FB2.fileExtensionString;
            default: return "";
        }
    }
}
