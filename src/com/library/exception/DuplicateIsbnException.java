package com.library.exception;

/**
 * 新增藏書時 ISBN 已存在。
 */

public class DuplicateIsbnException extends LibraryException {

    public DuplicateIsbnException(String isbn) {
        super("ISBN 已存在：" + isbn);
    }
}
