package com.library.ui;

import com.library.service.LoanService;
import com.library.service.ReturnResult;
import java.util.List;

import com.library.exception.CancelException;
import com.library.exception.LibraryException;
import com.library.model.Loan;

public class LoanMenu {
    private final LoanService loanService;

    public LoanMenu(LoanService loanService) {
        this.loanService = loanService;
    }

    // ── 借閱管理（F3 / F4）───────────────────────────────────

    public void loanMenu() {
        while (true) {
            System.out.println("""

                    ──── 借閱管理 ────
                     1. 借書
                     2. 還書
                     3. 未歸還清單
                     4. 預約借書
                     5. 續借
                     9. 回上層""");
            switch (InputHandler.input("請選擇")) {
                case "1" -> borrow();
                case "2" -> returnBook();
                case "3" -> listActiveLoans();
                case "4" -> reserveBook();
                case "5" -> renewBook();
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
        } catch (CancelException e) {
            System.out.println("\n✘ " + e.getMessage() + "，返回上層。");
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
        } catch (CancelException e) {
            System.out.println("\n✘ " + e.getMessage() + "，返回上層。");
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

    /**
     * 新增功能：書籍續借
     */
    private void renewBook() {
        long loanId = InputHandler.inputInt("請輸入要續借的借閱紀錄編號");        
        try {
            loanService.renewLoan(loanId);
            System.out.println("✔ 續借成功！還書期限已延長。");
        } catch (LibraryException e) {
            System.out.println("✘ " + e.getMessage());
        }
    }

    /**
     * 新增功能：預約借書
     */
    private void reserveBook() {    
        try {    
            // 1. 讀取並轉成 long 型態（配合你的 InputHandler）    
            long memberId = Long.parseLong(InputHandler.inputOrCancel("請輸入預約會員編號"));    
            long bookId = Long.parseLong(InputHandler.inputOrCancel("請輸入欲預約的書本編號"));                        
            // 2. 依照你 LoanService 定義的參數順序傳遞 (bookId, memberId)        
            loanService.reserve(bookId, memberId);        
            System.out.println("✔ 預約成功！當書籍歸還時將優先保留給您。");    
        } catch (NumberFormatException e) {    
            System.out.println("✘ 請輸入有效的數字編號");    
        } catch (LibraryException e) {    
            System.out.println("✘ " + e.getMessage());    
        }
    }
}
