package com.library.model;

/**
 * 會員借閱排行報表的一列（F6）。純資料傳輸物件。
 *
 * @param memberName 會員姓名
 * @param memberType 身份中文名稱
 * @param loanCount  累計借閱次數
 */

public record MemberRankingRow(String memberName, String memberType, long loanCount) {

}
