package com.library.dao;

import com.library.exception.DataAccessException;
import com.library.model.Book;
import com.library.model.BookType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link Book} 的資料存取物件。
 * <p>
 * 職責只有一個：物件 ↔ 資料列的轉換（CRUD）。
 * 不做業務驗證、不判斷重複 —— 那些屬於 Service 層。
 * 所有查詢一律使用 {@link PreparedStatement} 佔位符，杜絕 SQL 注入。
 */

public class BookDAO {
    /** 新增藏書，並將資料庫產生的主鍵回填到傳入物件。 */
    public void insert(Book book) {
        String sql = """
                INSERT INTO books
                    (isbn, title, author, type, total_copies, available_copies)
                VALUES (?, ?, ?, ?, ?, ?)""";
    }

    /** 依主鍵查詢。 */
    public Optional<Book> findById(long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("查詢藏書失敗", e);
        }
    }

    /** 依 ISBN 查詢（用於重複檢查）。 */
    public Optional<Book> findByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        return null;
    }

    /** 全部藏書，依書名排序。 */
    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY title";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<Book> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("列出藏書失敗", e);
        }
    }

    /**
     * 依書名／作者／類型組合條件查詢 （F5）
     * 任一參數為 null 或空字串代表「該條件不限」。
     * SQL 骨架動態拼接，但值一律走佔位符。
     */
    public List<Book> search(String title, String author, BookType type) {
        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1 = 1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + title.trim() + "%");
        }
        if (author != null && !author.isBlank()) {
            sql.append(" AND author LIKE ?");
            params.add("%" + author.trim() + "%");
        }
        if (type != null) {
            sql.append(" AND type = ?");
            params.add(type.name());
        }
        sql.append(" ORDER BY title");

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Book> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("查詢藏書失敗", e);
        }
    }

    /** 借出一份：可用份數 −1（僅在 available_copies &gt; 0 時成立）。 */
    public void decrementAvailable(long bookId) {
        String sql = "UPDATE books SET available_copies = available_copies - 1 "
                + "WHERE id = ? AND available_copies > 0";
        updateCopies(sql, bookId, "扣減可用份數失敗");
    }

    /** 歸還一份：可用份數 +1（不超過 total_copies）。 */
    public void incrementAvailable(long bookId) {
        String sql = "UPDATE books SET available_copies = available_copies + 1 "
                + "WHERE id = ? AND available_copies < total_copies";
        updateCopies(sql, bookId, "回補可用份數失敗");
    }

    private void updateCopies(String sql, long bookId, String errorMsg) {
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(errorMsg, e);
        }
    }

    /** 將目前 ResultSet 游標所在列轉為 Book 物件。 */
    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getLong("id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                BookType.valueOf(rs.getString("type")),
                rs.getInt("total_copies"),
                rs.getInt("available_copies"));
    }

}
