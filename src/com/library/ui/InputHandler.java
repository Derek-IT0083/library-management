package com.library.ui;

import java.util.Scanner;
import com.library.model.BookType;
import com.library.model.MemberType;

public class InputHandler {

    private static final Scanner in = new Scanner(System.in);

    // ── 輸入輔助 ─────────────────────────────────────────────
    // 一律以 nextLine() 讀整行再自行轉型，避免 nextInt() 殘留換行的陷阱。

    public static String input(String label) {
        System.out.print(label + "：");
        return in.nextLine().trim();
    }

    /** 允許空白的輸入，回傳 null 代表未填。 */
    public static String inputOptional(String label) {
        String value = input(label);
        return value.isBlank() ? null : value;
    }

    public static int inputInt(String label) {
        while (true) {
            try {
                return Integer.parseInt(input(label));
            } catch (NumberFormatException e) {
                System.out.println("✘ 請輸入數字");
            }
        }
    }

    public static BookType inputBookType() {
        while (true) {
            String s = input("類型 (1)紙本 (2)電子 (3)有聲");
            switch (s) {
                case "1":
                    return BookType.PAPER;
                case "2":
                    return BookType.EBOOK;
                case "3":
                    return BookType.AUDIO;
                default:
                    System.out.println("✘ 請輸入 1~3");
            }
        }
    }

    public static BookType inputOptionalBookType() {
        String s = input("類型 (1)紙本 (2)電子 (3)有聲，Enter 代表不限");
        return switch (s) {
            case "1" -> BookType.PAPER;
            case "2" -> BookType.EBOOK;
            case "3" -> BookType.AUDIO;
            default -> null;
        };
    }

    public static MemberType inputMemberType() {
        while (true) {
            String s = input("身份 (1)學生 (2)教職員");
            switch (s) {
                case "1":
                    return MemberType.STUDENT;
                case "2":
                    return MemberType.STAFF;
                default:
                    System.out.println("✘ 請輸入 1 或 2");
            }
        }
    }

}
