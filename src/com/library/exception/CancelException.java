package com.library.exception;

//回上一動

public class CancelException extends RuntimeException {
    public CancelException() {
        super("已取消操作");
    }
}
