package com.example.libraryapp;//package com.example.libraryapp;
//
//public class Book {
//    private String bookId;
//    private String bookName;
//    public String numberOfBooks;
//
//    public Book(String bookId, String bookName, String numberOfBooks) {
//        this.bookId = bookId;
//        this.bookName = bookName;
//        this.numberOfBooks = numberOfBooks;
//    }
//}
//
//
//
public class Book {
    private String name;
    private String numberOfBooks;

    public Book() {
        // Default constructor required for Firebase
    }

    public Book(String name, String numberOfBooks) {
        this.name = name;
        this.numberOfBooks = numberOfBooks;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return numberOfBooks;
    }
}

