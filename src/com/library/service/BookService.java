package com.library.service;

import com.library.dao.BookDAO;
import com.library.exception.DuplicateIsbnException;
import com.library.model.Book;
import com.library.model.BookType;

import java.util.List;
import java.util.Optional;

/**
 * 藏書管理業務邏輯（F1）。
 * <p>負責輸入驗證與重複檢查，通過後才委派 DAO 落庫。
 */

public class BookService {

    private final BookDAO bookDao;

    public BookService(BookDAO bookDao) {
        this.bookDao = bookDao;
    }

    /**
     * 新增藏書。
     * @throws IllegalArgumentException  欄位不合法
     * @throws DuplicateIsbnException    ISBN 已存在
     */
    public Book addBook(Book book) {
        // 1. 輸入驗證
        requireText(book.getIsbn(), "ISBN 不可為空");
        requireText(book.getTitle(), "書名不可為空");
        requireText(book.getAuthor(), "作者不可為空");
        if (book.getType() == null) {
            throw new IllegalArgumentException("請指定書籍類型");
        }
        if (book.getTotalCopies() < 1) {
            throw new IllegalArgumentException("館藏份數至少為 1");
        }

        // 2. 重複檢查
        if (bookDao.findByIsbn(book.getIsbn()).isPresent()) {
            throw new DuplicateIsbnException(book.getIsbn());
        }

        // 3. 通過才落庫
        bookDao.insert(book);
        return book;
    }

    /** 依 ISBN 查詢單一藏書。 */
    public Optional<Book> findByIsbn(String isbn) {
        return bookDao.findByIsbn(isbn);
    }

    /** 全部藏書。 */
    public List<Book> listAll() {
        return bookDao.findAll();
    }
    
    /** 組合條件查詢（F5）。 */
    public List<Book> search(String title, String author, BookType type) {
        return bookDao.search(title, author, type);
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

}
