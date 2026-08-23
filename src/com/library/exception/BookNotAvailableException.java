package com.library.exception;

import com.library.model.Book;

/**
 * 借書時該書已無可借份數（紙本／有聲書借完）。
 */
public class BookNotAvailableException extends LibraryException {

    public BookNotAvailableException(Book book) {
        super("《" + book.getTitle() + "》目前已無可借館藏，請稍後再試");
    }
}
