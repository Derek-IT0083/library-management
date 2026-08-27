package com.library.ui;

import com.library.exception.LibraryException;
import com.library.model.MemberType;
import com.library.service.MemberService;
import com.library.model.Member;

public class MemberMenu {
    private final MemberService memberService;

    public MemberMenu(MemberService memberService) {
        this.memberService = memberService;
    }

    // ── 會員管理（F2）────────────────────────────────────────
    public void memberMenu() {
        while (true) {
            System.out.println("""

                    ──── 會員管理 ────
                     1. 新增會員
                     2. 依會員編號查詢
                     3. 會員列表
                     9. 回上層""");
            switch (InputHandler.input("請選擇")) {
                case "1" -> addMember();
                case "2" -> findMember();
                case "3" -> memberService.listAll().forEach(m -> System.out.println("  " + m));
                case "9" -> {
                    return;
                }
                default -> System.out.println("✘ 無效選項");
            }
        }
    }

    private void addMember() {
        try {
            String no = InputHandler.input("會員編號");
            String name = InputHandler.input("姓名");
            MemberType type = InputHandler.inputMemberType();
            String email = InputHandler.inputOptional("電子郵件");
            Member member = memberService.addMember(new Member(no, name, type, email));
            System.out.printf("✔ 新增成功：%s（%s）%n", member.getName(), type.label());
        } catch (LibraryException | IllegalArgumentException e) {
            System.out.println("✘ " + e.getMessage());
        }
    }

    private void findMember() {
        String no = InputHandler.input("會員編號");
        memberService.findByMemberNo(no).ifPresentOrElse(
                m -> System.out.println("  " + m),
                () -> System.out.println("查無此會員"));
    }

}
