package dao;

import exception.DBException;
import exception.ServerException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public final class DBUtils {
    private final static String DB_DRIVER = "org.h2.Driver";
    private final static String DB_URL = "jdbc:h2:~/test";
    private final static String DB_USERNAME ="sa";
    private final static String DB_PASSWORD = "";
    static DataSource datasource;
    static DBUtils instance;
    public static synchronized DBUtils getInstance() throws DBException {
        if (instance == null) {
            instance = new DBUtils();
        }

        return instance;
    }
    static{
		/*try{
			Class.forName(DB_DRIVER);
			System.out.println("Driver loaded");
		} catch(ClassNotFoundException e){
			System.out.println("unable to downlodal driver");
		}

		Statement st = null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		try {

			st = conn.createStatement();
			st.execute("drop table if exists users; " +
					"create table if not exists " +
					"users(" +
					"login varchar(20)," +
					"password varchar(100)," +
					"name varchar(100)," +
					"passport varchar(100)," +
					"number varchar(100)," +
					"email varchar(100)," +
					"role_id INT," +
					"balance INT," +
					"avatar varchar(250)" +
					");"
					);

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				close(conn);
			} catch (ServerException e) {
				e.printStackTrace();
			}
		}*/
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /*
    private DBUtils() throws DBException{


		/*try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			datasource = (DataSource) envContext.lookup("jdbc/user");
			LOG.trace("Data source ==> " + datasource);
		} catch (NamingException ex) {
			LOG.error(Messages.ERR_CANNOT_OBTAIN_DATA_SOURCE, ex);
			throw new DBException(Messages.APP_ERR_CANNOT_ACCESS_TO_USERS, ex);
		}

    }*/
    public static void close(Connection con) throws ServerException {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                throw new ServerException(Messages.ERR_CANNOT_CLOSE_CONNECTION, e);
            }
        }
    }

    public static void close(Statement stmt) throws ServerException {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new ServerException(Messages.ERR_CANNOT_CLOSE_STATEMENT, e);
            }
        }
    }

    public static void close(ResultSet rs) throws ServerException {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new ServerException(Messages.ERR_CANNOT_CLOSE_RESULTSET, e);
            }
        }
    }

    public static void rollback(Connection con) throws ServerException {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e) {
                throw new ServerException(Messages.ERR_CANNOT_ROLLBACK_TRANSACTION, e);
            }
        }
    }
    public static Connection getСonn(DataSource ds) throws DBException, SQLException {
        //Connection con =  DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/advanced_autumn_2020?user=root&password=04022001&serverTimezone=UTC&useSSL=false");
        con.setAutoCommit(false);
        return con;
    }

    public static SimpleDateFormat getSimpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd hh:mm");
    }
}
