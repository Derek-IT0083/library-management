package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.LoanDAO;
import com.library.dao.MemberDAO;
import com.library.exception.BookNotAvailableException;
import com.library.exception.BorrowLimitExceededException;
import com.library.exception.EntityNotFoundException;
import com.library.exception.OverdueBlockException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 借閱業務邏輯（F3 借書、F4 還書）與報表（F6）。
 * <p>本層是業務規則的落腳處：三道借書檢查、到期日計算、
 * 逾期罰金結算全部集中在此。
 */
public class LoanService {

    private final BookDAO bookDao;
    private final MemberDAO memberDao;
    private final LoanDAO loanDao;

    public LoanService(BookDAO bookDao, MemberDAO memberDao, LoanDAO loanDao) {
        this.bookDao = bookDao;
        this.memberDao = memberDao;
        this.loanDao = loanDao;
    }

    /**
     * 借書（F3）。依序通過三道檢查後才寫入借閱紀錄並扣減份數。
     *
     * @throws BookNotAvailableException     已無可借份數
     * @throws BorrowLimitExceededException  達同時借書上限
     * @throws OverdueBlockException         有逾期未還書籍
     */
    public Loan borrow(long bookId, long memberId) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("找不到書籍 id=" + bookId));
        Member member = memberDao.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("找不到會員 id=" + memberId));

        // 檢查 1：可借份數（電子書不限份數）
        if (!book.hasAvailableCopy()) {
            throw new BookNotAvailableException(book);
        }
        // 檢查 2：同時借書上限
        if (loanDao.countActiveByMember(memberId) >= member.borrowLimit()) {
            throw new BorrowLimitExceededException(member);
        }
        // 檢查 3：逾期封鎖
        if (loanDao.hasOverdue(memberId)) {
            throw new OverdueBlockException(member);
        }

        // 到期日 = 今天 + 書的借期 + 會員延長天數
        LocalDate today = LocalDate.now();
        LocalDate due = today.plusDays(book.loanDays() + member.extraDays());

        // 先寫紀錄再扣份數；任一檢查失敗都不會走到這裡，不留半套狀態
        Loan loan = loanDao.insert(new Loan(bookId, memberId, today, due));
        if (book.getType().isCopyLimited()) {
            bookDao.decrementAvailable(bookId);
        }
        return loan;
    }

    /**
     * 還書（F4）。計算逾期天數與罰金，更新紀錄並回補份數。
     *
     * @param loanId 借閱紀錄 id
     * @throws EntityNotFoundException 找不到未歸還的借閱紀錄
     */
    public ReturnResult returnBook(long loanId) {
        Loan loan = loanDao.findActiveById(loanId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "找不到未歸還的借閱紀錄 id=" + loanId));
        Book book = bookDao.findById(loan.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "借閱對應的書籍不存在 id=" + loan.getBookId()));

        LocalDate today = LocalDate.now();
        long overdueDays = loan.overdueDays(today);

        // 罰金 = 逾期天數 × 類型費率（電子書費率為 0）
        BigDecimal fine = BigDecimal.valueOf(overdueDays * (long) book.finePerDay());

        loan.close(today, fine);
        loanDao.update(loan);

        // 電子書不占份數，不需回補
        if (book.getType().isCopyLimited()) {
            bookDao.incrementAvailable(book.getId());
        }
        return new ReturnResult(loan, overdueDays, fine);
    }

    /** 全部未歸還的借閱紀錄。 */
    public List<Loan> listActiveLoans() {
        return loanDao.findAllActive();
    }

}
