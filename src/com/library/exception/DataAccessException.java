package com.library.exception;

/**
 * DAO 層資料庫存取失敗時包裝底層 {@link java.sql.SQLException}。
 * <p>把受檢的 SQLException 轉為非受檢例外，讓上層不必到處
 * {@code throws SQLException}，同時保留原始例外做為 cause 供除錯。
 */

public class DataAccessException extends LibraryException {

    public DataAccessException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
