package com.library.model;

/**
 * 逾期借閱報表的一列（F6）。純資料傳輸物件。
 *
 * @param memberName  會員姓名
 * @param bookTitle   書名
 * @param dueDate     到期日（字串）
 * @param overdueDays 逾期天數
 */

public record OverdueReportRow(String memberName, String bookTitle,String dueDate, long overdueDays) {

}
