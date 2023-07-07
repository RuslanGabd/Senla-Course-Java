package com.ruslan.data.repository;

import com.ruslan.data.book.Book;
import com.ruslan.data.book.BookCounted;
import com.ruslan.data.book.BookStatus;
import com.ruslan.data.repository.rinterface.IBookRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookRepository implements IBookRepository {

    private Map<Integer, Book> booksMap = new HashMap<>();


    public void saveBook(Book book) {
        int idBook = BookCounted.generateNewId();
        book.setId(idBook);
        booksMap.put(idBook, book);
    }

    public Book getById(int id) {
        return booksMap.get(id);
    }


    @Override
    public void updateStatus(int id, BookStatus status) {
        booksMap.get(id).setStatus(status);
    }

    @Override
    public void removeById(int idBook) {
        booksMap.remove(idBook);
    }

    @Override
    public List<Book> getBooksList() {
        return new ArrayList<>(booksMap.values());
    }

    public Map<Integer, Book> getStock() {
        return booksMap;
    }

    public void setStock(Map<Integer, Book> stock) {
        this.booksMap = stock;
    }
}