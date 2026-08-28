package com.library.ui;

import com.library.dao.BookDAO;
import com.library.dao.LoanDAO;
import com.library.dao.MemberDAO;
import com.library.dao.ReservationDAO;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.MemberService;

public class AppMain {

    private final BookService bookService;
    private final MemberService memberService;
    private final LoanService loanService; 

    public AppMain() {
        this.bookService = new BookService(new BookDAO());
        this.memberService = new MemberService(new MemberDAO());
        this.loanService = new LoanService(new BookDAO(), new MemberDAO(), new LoanDAO(), new ReservationDAO());
    }
    
    public static void main(String[] args) {
        new AppMain().run();
    }

    public void run() {
        System.out.println("════════ 圖書館借閱管理系統 ════════");
        while (true) {
            printMainMenu();
            switch (InputHandler.input("請選擇")) {
                case "1" -> new BookMenu(this.bookService).bookMenu();
                case "2" -> new MemberMenu(this.memberService).memberMenu();
                case "3" -> new LoanMenu(this.loanService).loanMenu();
                case "4" -> new ReportMenu(this.loanService).reportMenu();
                case "0" -> {
                    System.out.println("再見！");
                    return;
                }
                default -> System.out.println("✘ 無效選項，請重新輸入");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("""

                ──────── 主選單 ────────
                 1. 藏書管理
                 2. 會員管理
                 3. 借閱管理
                 4. 報表
                 0. 離開""");
    }
}
