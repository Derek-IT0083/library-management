package com.library.dao;

import com.library.exception.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReservationDAO {

    /**
     * 新增預約紀錄
     */
    public boolean createReservation(long memberId, long bookId, LocalDate reservationDate) {
        String sql = "INSERT INTO reservations (member_id, book_id, reservation_date) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, memberId);
            pstmt.setLong(2, bookId);
            pstmt.setDate(3, java.sql.Date.valueOf(reservationDate));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("新增預約失敗", e);
        }
    }
}
