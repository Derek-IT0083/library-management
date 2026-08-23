package com.library.model;

/**
 * 會員身份：學生與教職員的借閱權限差異集中於此。
 */

public enum MemberType {

    /** 學生：最多同時借 3 本，無延長借期。 */
    STUDENT("學生", 3, 0),

    /** 教職員：最多同時借 10 本，享延長借期 7 天。 */
    STAFF("教職員", 10, 7);

    private final String label;
    private final int borrowLimit;
    private final int extraDays;

    MemberType(String label, int borrowLimit, int extraDays) {
        this.label = label;
        this.borrowLimit = borrowLimit;
        this.extraDays = extraDays;
    }

    /** 中文顯示名稱。 */
    public String label() {
        return label;
    }

    /** 同時借書上限（本）。 */
    public int borrowLimit() {
        return borrowLimit;
    }

    /** 借期延長天數。教職員為 7，學生為 0。 */
    public int extraDays() {
        return extraDays;
    }

}
