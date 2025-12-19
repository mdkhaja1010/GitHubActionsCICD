package programs;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBUtils {
	private static Properties prop = new Properties();
    private static String DB_URL;
    private static String USER;
    private static String PASSWORD;
    
    static {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/dbconfig.properties");
            prop.load(fis);

            DB_URL = prop.getProperty("DB_URL");
            USER = prop.getProperty("USER");
            PASSWORD = prop.getProperty("PASSWORD");

            System.out.println("DB Config Loaded Successfully");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load database configuration");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Connecting to DB: " + DB_URL);
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
    public static ResultSet executeQuery(String query) throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }
}