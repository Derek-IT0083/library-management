package com.library.ui;

import java.util.List;
import com.library.service.LoanService;
import com.library.model.OverdueReportRow;
import com.library.model.MemberRankingRow;

public class ReportMenu {

    private final LoanService loanService;

    public ReportMenu(LoanService loanService) {
         this.loanService = loanService;
    }

    // ── 報表（F6）────────────────────────────────────────────

    public void reportMenu() {
        while (true) {
            System.out.println("""

                    ──── 報表 ────
                     1. 逾期借閱清單
                     2. 會員借閱排行
                     9. 回上層""");
            switch (InputHandler.input("請選擇")) {
                case "1" -> overdueReport();
                case "2" -> memberRanking();
                case "9" -> {
                    return;
                }
                default -> System.out.println("✘ 無效選項");
            }
        }
    }

    /** 逾期借閱清單，按逾期天數由多到少排序。 */
    private void overdueReport() {
        List<OverdueReportRow> rows = loanService.overdueReport();
        if (rows.isEmpty()) {
            System.out.println("（目前無逾期借閱）");
            return;
        }
        System.out.println("逾期借閱清單（按逾期天數）：");
        for (OverdueReportRow r : rows) {
            System.out.printf("  %s — 《%s》 應還 %s，逾期 %d 天%n",
                    r.memberName(), r.bookTitle(), r.dueDate(), r.overdueDays());
        }
    }

    private void memberRanking() {
        List<MemberRankingRow> rows = loanService.memberRanking();
        if (rows.isEmpty()) {
            System.out.println("（尚無借閱紀錄）");
            return;
        }
        System.out.println("會員借閱排行：");
        int rank = 1;
        for (MemberRankingRow r : rows) {
            System.out.printf("  %d. %s（%s）— %d 次%n",
                    rank++, r.memberName(), r.memberType(), r.loanCount());
        }
    }

}
