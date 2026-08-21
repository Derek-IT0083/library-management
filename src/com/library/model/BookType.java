package com.library.model;

public enum BookType {
     /** 紙本書：借期 14 天、逾期每天 5 元、有份數限制。 */
    PAPER("紙本書", 14, 5, true),

    /** 電子書：借期 7 天、到期自動回收無罰金、不限份數可多人同借。 */
    EBOOK("電子書", 7, 0, false),

    /** 有聲書：借期 10 天、逾期每天 3 元、有份數限制。 */
    AUDIO("有聲書", 10, 3, true);

    private final String label;
    private final int loanDays;
    private final int finePerDay;
    private final boolean copyLimited;

    BookType(String label, int loanDays, int finePerDay, boolean copyLimited) {
        this.label = label;
        this.loanDays = loanDays;
        this.finePerDay = finePerDay;
        this.copyLimited = copyLimited;
    }

    /** 中文顯示名稱。 */
    public String label() {
        return label;
    }

    /** 基本借期（天）。實際到期日另加會員的延長天數。 */
    public int loanDays() {
        return loanDays;
    }

    /** 逾期罰金費率（元／天）。電子書為 0。 */
    public int finePerDay() {
        return finePerDay;
    }

    /**
     * 是否受可借份數限制。
     * 電子書回傳 false（不限份數、可多人同時借閱）。
     */
    public boolean isCopyLimited() {
        return copyLimited;
    }
}