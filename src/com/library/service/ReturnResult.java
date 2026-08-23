package com.library.service;

import com.library.model.Loan;

import java.math.BigDecimal;

/**
 * 還書結果，供 UI 顯示。
 *
 * @param loan        已結算的借閱紀錄
 * @param overdueDays 逾期天數（未逾期為 0）
 * @param fine        罰金金額
 */

public record ReturnResult(Loan loan, long overdueDays, BigDecimal fine) {

    /** 是否逾期。 */
    public boolean isOverdue() {
        return overdueDays > 0;
    }

}
