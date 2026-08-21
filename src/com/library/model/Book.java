package com.library.model;

public class Book {
    private long id;
    private String isbn;
    private String title;
    private String author;
    private BookType type;
    private int totalCopies;
    private int availableCopies;

    public Book() {}

    /** 新增藏書用：尚未有 id，可用份數預設等於總份數。 */
    public Book(String isbn, String title, String author, BookType type, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.type = type;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    /** 從資料庫載入用：帶完整欄位。 */
    public Book(long id, String isbn, String title, String author,
                BookType type, int totalCopies, int availableCopies) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.type = type;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // ── 委派 BookType 的業務規則 ─────────────────────────────

    /** 基本借期（天），由書籍類型決定。 */
    public int loanDays() {
        return type.loanDays();
    }

    /** 逾期罰金費率（元／天），由書籍類型決定。 */
    public int finePerDay() {
        return type.finePerDay();
    }

    /** 是否還有可借份數。電子書不限份數，恆為 true。 */
    public boolean hasAvailableCopy() {
        return !type.isCopyLimited() || availableCopies > 0;
    }

    // ── getter / setter ─────────────────────────────────────

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BookType getType() {
        return type;
    }

    public void setType(BookType type) {
        this.type = type;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s / %s（%s，可借 %d/%d）ISBN=%s",
                id, title, author, type.label(), availableCopies, totalCopies, isbn);
    }

}
