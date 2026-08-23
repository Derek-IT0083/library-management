package com.library.exception;

/**
 * 所有業務例外的共同父類別。
 * <p>繼承 {@link RuntimeException}（非受檢例外），
 * 讓 Service 層可自然拋出、UI 層以單一
 * {@code catch (LibraryException)} 一次接住所有業務錯誤，
 * 轉換成友善訊息。
 */

public class LibraryException extends RuntimeException {

    public LibraryException(String message) {
        super(message);
    }
}
