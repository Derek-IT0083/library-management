package com.library.ui;

public class AppMain {
    public static void main(String[] args) {
        new AppMain().run();
    }

    public void run() {
        System.out.println("════════ 圖書館借閱管理系統 ════════");
        while (true) {
            printMainMenu();
            switch (InputHandler.input("請選擇")) {
                case "1" -> new BookMenu().bookMenu();
                case "2" -> new MemberMenu().memberMenu();
                case "3" ->   System.out.println("To Do ....");
                case "4" ->   System.out.println("To Do ....");
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
