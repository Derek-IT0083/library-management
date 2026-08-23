package com.library.ui;

import com.library.service.LoanService;
import com.library.service.ReturnResult;
import java.util.List;

import com.library.dao.BookDAO;
import com.library.dao.LoanDAO;
import com.library.dao.MemberDAO;
import com.library.exception.LibraryException;
import com.library.model.Loan;

public class LoanMenu {
    private final LoanService loanService;

    public LoanMenu(BookDAO bookDao, MemberDAO memberDao, LoanDAO loanDao) {
        this.loanService = new LoanService(bookDao, memberDao, loanDao);
    }

    // ── 借閱管理（F3 / F4）───────────────────────────────────

    public void loanMenu() {
        while (true) {
            System.out.println("""

                    ──── 借閱管理 ────
                     1. 借書
                     2. 還書
                     3. 未歸還清單
                     9. 回上層""");
            switch (InputHandler.input("請選擇")) {
                case "1" -> borrow();
                case "2" -> returnBook();
                case "3" -> listActiveLoans();
                case "9" -> {
                    return;
                }
                default -> System.out.println("✘ 無效選項");
            }
        }
    }

    private void borrow() {
        try {
            long bookId = InputHandler.inputInt("書籍 id");
            long memberId = InputHandler.inputInt("會員 id");
            Loan loan = loanService.borrow(bookId, memberId);
            System.out.printf("✔ 借閱成功：借閱單 #%d，應還日 %s%n",
                    loan.getId(), loan.getDueDate());
        } catch (LibraryException | IllegalArgumentException e) {
            System.out.println("✘ " + e.getMessage());
        }
    }

    private void returnBook() {
        try {
            long loanId = InputHandler.inputInt("借閱單 id");
            ReturnResult r = loanService.returnBook(loanId);
            if (r.isOverdue()) {
                System.out.printf("✔ 還書完成：逾期 %d 天，罰金 %s 元%n",
                        r.overdueDays(), r.fine().toPlainString());
            } else {
                System.out.println("✔ 還書完成：準時歸還，無罰金");
            }
        } catch (LibraryException | IllegalArgumentException e) {
            System.out.println("✘ " + e.getMessage());
        }
    }

    private void listActiveLoans() {
        List<Loan> loans = loanService.listActiveLoans();
        if (loans.isEmpty()) {
            System.out.println("（目前無未歸還借閱）");
            return;
        }
        System.out.println("未歸還共 " + loans.size() + " 筆：");
        loans.forEach(l -> System.out.printf("  #%d 書 id=%d 會員 id=%d 應還 %s%n",
                l.getId(), l.getBookId(), l.getMemberId(), l.getDueDate()));
    }

}
