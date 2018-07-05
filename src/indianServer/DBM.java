package indianServer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBM {
	private static String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static String URL = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
	private static String USER = "kt";
	private static String PWD = "";
	
	static {
		try {
			Class.forName(DRIVER);
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConn() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(URL, USER, PWD);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public int undateUser(GameUser user) {
		int result = 0;
		Connection conn = null;
		Statement  stmt = null;
		try {
			conn = getConn();
			stmt = conn.createStatement();
			String sql = "UPDATE user SET ";
			
			result = stmt.executeUpdate(sql);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(stmt!=null) stmt.close();
				if(conn!=null) conn.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public void loginUser(String id, String pwd) {
		Connection con = null;
		PreparedStatement ps = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = getConn();
			String sql = "SELECT * FROM user WHERE id=? AND password=?";
			ps = con.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, pwd);
			
			rs = ps.executeQuery();
			while(rs.next()) {
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		//return GameUser
	}
	
}
