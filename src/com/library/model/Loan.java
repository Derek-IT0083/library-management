package com.library.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 借閱紀錄實體，對應資料表 {@code loans} 的一列。
 * <p>{@code returnDate == null} 代表尚未歸還（仍在借閱中）。
 * 逾期判斷與逾期天數的計算封裝於本類別。
 */

public class Loan {

    private long id;
    private long bookId;
    private long memberId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;   // null 代表未還
    private BigDecimal fine = BigDecimal.ZERO;

    public Loan() {
    }

    /** 新借出用：借閱日與到期日由 Service 計算後帶入。 */
    public Loan(long bookId, long memberId, LocalDate loanDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    /** 從資料庫載入用：帶完整欄位。 */
    public Loan(long id, long bookId, long memberId, LocalDate loanDate,
                LocalDate dueDate, LocalDate returnDate, BigDecimal fine) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine == null ? BigDecimal.ZERO : fine;
    }

    // ── 業務行為 ─────────────────────────────────────────────

    /** 是否尚未歸還。 */
    public boolean isActive() {
        return returnDate == null;
    }

    /**
     * 相對於指定日期是否已逾期（僅在尚未歸還時成立）。
     * @param asOf 判斷基準日，通常為今天
     */
    public boolean isOverdue(LocalDate asOf) {
        return isActive() && asOf.isAfter(dueDate);
    }

    /**
     * 相對於指定日期的逾期天數；未逾期回傳 0。
     * @param asOf 判斷基準日，通常為今天
     */
    public long overdueDays(LocalDate asOf) {
        long days = ChronoUnit.DAYS.between(dueDate, asOf);
        return Math.max(0, days);
    }

    /**
     * 結算歸還：設定歸還日與罰金。
     * @param returnDate 歸還日期
     * @param fine       結算後的罰金金額
     */
    public void close(LocalDate returnDate, BigDecimal fine) {
        this.returnDate = returnDate;
        this.fine = fine == null ? BigDecimal.ZERO : fine;
    }

    // ── getter / setter ─────────────────────────────────────

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getFine() {
        return fine;
    }

    public void setFine(BigDecimal fine) {
        this.fine = fine == null ? BigDecimal.ZERO : fine;
    }

}
