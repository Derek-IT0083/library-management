package com.library.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static final String url;
    private static final String user;
    private static final String password;

    private DBUtil() {
        // 工具類別，不可實例化
    }

    static {
        Properties props = new Properties();
        try (InputStream in = DBUtil.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("[DBUtil] 讀取 db.properties 失敗，改用預設值：" + e.getMessage());
        }

        url = props.getProperty("db.url",
                "jdbc:mysql://localhost:3306/library_db"
                        + "?useSSL=false&serverTimezone=Asia/Taipei"
                        + "&characterEncoding=utf8&allowPublicKeyRetrieval=true");
        user = props.getProperty("db.user", "root");
        password = props.getProperty("db.password", "");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
  
}
