package com.library.dao;

import com.library.exception.DataAccessException;
import com.library.model.Loan;
import com.library.model.MemberRankingRow;
import com.library.model.MemberType;
import com.library.model.OverdueReportRow;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link Loan} 的資料存取物件，另含兩支報表查詢（F6）。
 */
public class LoanDAO {

    /** 新增借閱紀錄，回填主鍵並回傳同一物件。 */
    public Loan insert(Loan loan) {
        String sql = """
                INSERT INTO loans (book_id, member_id, loan_date, due_date, return_date, fine)
                VALUES (?, ?, ?, ?, ?, ?)""";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, loan.getBookId());
            ps.setLong(2, loan.getMemberId());
            ps.setDate(3, Date.valueOf(loan.getLoanDate()));
            ps.setDate(4, Date.valueOf(loan.getDueDate()));
            ps.setDate(5, loan.getReturnDate() == null
                    ? null : Date.valueOf(loan.getReturnDate()));
            ps.setBigDecimal(6, loan.getFine());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    loan.setId(keys.getLong(1));
                }
            }
            return loan;
        } catch (SQLException e) {
            throw new DataAccessException("新增借閱紀錄失敗", e);
        }
    }

    /** 更新借閱紀錄（歸還時寫入 return_date 與 fine）。 */
    public void update(Loan loan) {
        String sql = "UPDATE loans SET return_date = ?, fine = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, loan.getReturnDate() == null
                    ? null : Date.valueOf(loan.getReturnDate()));
            ps.setBigDecimal(2, loan.getFine());
            ps.setLong(3, loan.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("更新借閱紀錄失敗", e);
        }
    }

    /** 查詢一筆尚未歸還的借閱紀錄。 */
    public Optional<Loan> findActiveById(long loanId) {
        String sql = "SELECT * FROM loans WHERE id = ? AND return_date IS NULL";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("查詢借閱紀錄失敗", e);
        }
    }

    /** 會員目前未還書數（用於借閱上限檢查）。 */
    public int countActiveByMember(long memberId) {
        String sql = "SELECT COUNT(*) FROM loans "
                + "WHERE member_id = ? AND return_date IS NULL";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("統計借閱數失敗", e);
        }
    }

    /**
     * 會員是否有逾期未還的書（用於逾期封鎖檢查）。
     * 條件：未歸還且到期日早於今天。
     */
    public boolean hasOverdue(long memberId) {
        String sql = "SELECT 1 FROM loans "
                + "WHERE member_id = ? AND return_date IS NULL AND due_date < CURDATE() "
                + "LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("查詢逾期狀態失敗", e);
        }
    }

    /** 全部尚未歸還的借閱紀錄。 */
    public List<Loan> findAllActive() {
        String sql = "SELECT * FROM loans WHERE return_date IS NULL ORDER BY due_date";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Loan> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("列出借閱紀錄失敗", e);
        }
    }

    // ── 報表查詢（F6）────────────────────────────────────────

    /** 逾期借閱清單，按逾期天數由多到少排序。 */
    public List<OverdueReportRow> overdueReport() {
        String sql = """
                SELECT m.name AS member_name,
                       b.title AS book_title,
                       l.due_date AS due_date,
                       DATEDIFF(CURDATE(), l.due_date) AS overdue_days
                FROM loans l
                JOIN books b   ON b.id = l.book_id
                JOIN members m ON m.id = l.member_id
                WHERE l.return_date IS NULL
                  AND l.due_date < CURDATE()
                ORDER BY overdue_days DESC""";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<OverdueReportRow> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new OverdueReportRow(
                        rs.getString("member_name"),
                        rs.getString("book_title"),
                        rs.getDate("due_date").toLocalDate().toString(),
                        rs.getLong("overdue_days")));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("產生逾期報表失敗", e);
        }
    }

    /** 會員借閱排行（借閱總次數），取前 N 名。 */
    public List<MemberRankingRow> memberRanking(int limit) {
        String sql = """
                SELECT m.name AS member_name,
                       m.type AS member_type,
                       COUNT(*) AS loan_count
                FROM loans l
                JOIN members m ON m.id = l.member_id
                GROUP BY m.id, m.name, m.type
                ORDER BY loan_count DESC
                LIMIT ?""";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<MemberRankingRow> list = new ArrayList<>();
                while (rs.next()) {
                    MemberType type = MemberType.valueOf(rs.getString("member_type"));
                    list.add(new MemberRankingRow(
                            rs.getString("member_name"),
                            type.label(),
                            rs.getLong("loan_count")));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("產生借閱排行失敗", e);
        }
    }

    private Loan mapRow(ResultSet rs) throws SQLException {
        Date ret = rs.getDate("return_date");
        BigDecimal fine = rs.getBigDecimal("fine");
        return new Loan(
                rs.getLong("id"),
                rs.getLong("book_id"),
                rs.getLong("member_id"),
                rs.getDate("loan_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                ret == null ? null : ret.toLocalDate(),
                fine == null ? BigDecimal.ZERO : fine);
    }
}