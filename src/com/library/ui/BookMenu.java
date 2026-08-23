package com.library.ui;

import java.util.List;

import com.library.dao.BookDAO;
import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.BookType;
import com.library.service.BookService;

public class BookMenu {

    private final BookService bookService;

    public BookMenu() {
         BookDAO bookDao = new BookDAO();
         this.bookService = new BookService(bookDao);
    }

    public void bookMenu() {
        while (true) {
            System.out.println("""

                    ──── 藏書管理 ────
                     1. 新增藏書
                     2. 依 ISBN 查詢
                     3. 藏書列表
                     4. 組合條件查詢
                     9. 回上層""");
            switch (InputHandler.input("請選擇")) {
                case "1" -> addBook();
                case "2" -> findBookByIsbn();
                case "3" -> listBooks();
                case "4" -> System.out.println("To Do ....");
                case "9" -> {
                    return;
                }
                default -> System.out.println("✘ 無效選項");
            }
        }
    }

    private void addBook() {
        try {
            String isbn = InputHandler.input("ISBN");
            String title = InputHandler.input("書名");
            String author = InputHandler.input("作者");
            BookType type = InputHandler.inputBookType();
            int copies = InputHandler.inputInt("館藏份數");
            Book book = bookService.addBook(new Book(isbn, title, author, type, copies));
            System.out.printf("✔ 新增成功：%s（%s，共 %d 份）%n",
                    book.getTitle(), type.label(), copies);
        } catch (LibraryException | IllegalArgumentException e) {
            System.out.println("✘ " + e.getMessage());
        }
    }

    private void findBookByIsbn() {
        String isbn = InputHandler.input("ISBN");
        bookService.findByIsbn(isbn).ifPresentOrElse(
                b -> System.out.println("  " + b),
                () -> System.out.println("查無此書"));
    }

    private void listBooks() {
        printBookList(bookService.listAll());
    }

    
    private void printBookList(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("（無資料）");
            return;
        }
        System.out.println("共 " + books.size() + " 筆：");
        books.forEach(b -> System.out.println("  " + b));
    }

}
