package coursePractice.meetingMIS.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    public static String driver = "com.mysql.cj.jdbc.Driver";
    public static String url = "jdbc:mysql://127.0.0.1:3306/meetingdb?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";
    public static String user = "root";
    public static String pwd = "123456";

    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC 驱动未找到，请将 mysql-connector-j.jar 放入 lib 目录。");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pwd);
    }
}
