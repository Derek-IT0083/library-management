package com.library.exception;

/**
 * 新增會員時會員編號已存在。
 */

public class DuplicateMemberNoException extends LibraryException {

    public DuplicateMemberNoException(String memberNo) {
        super("會員編號已存在：" + memberNo);
    }
}
