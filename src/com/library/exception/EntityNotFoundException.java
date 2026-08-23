package com.library.exception;

/**
 * 找不到指定的實體（書、會員或借閱紀錄）。
 */

public class EntityNotFoundException extends LibraryException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
