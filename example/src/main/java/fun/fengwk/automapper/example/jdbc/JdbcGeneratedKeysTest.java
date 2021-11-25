package fun.fengwk.automapper.example.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author fengwk
 */
public class JdbcGeneratedKeysTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://mysql.fengwk.fun:3306/test?allowMultiQueries=true", "fengwk", "a123");
        PreparedStatement ps = conn.prepareStatement(
                "insert into example (sort) values (1); insert into example (sort) values (2)",
                Statement.RETURN_GENERATED_KEYS);
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        while (rs.next()) {
            System.out.println(rs.getLong(1));
        }
        conn.close();
    }

}
