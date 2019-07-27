package db.mysql;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation {
	public static void main(String[] args) {
		try {
			System.out.println("Connecting to: " + MySQLDBUtil.URL);
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			Connection connection = DriverManager.getConnection(MySQLDBUtil.URL);
			if (connection == null) {
				return;
			}
			
			Statement statement = connection.createStatement();
			String sql = "DROP TABLE IF EXISTS categories";
			statement.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS history";
			statement.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS items";
			statement.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS users";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE items ("
					+ "item_id VARCHAR(55) NOT NULL,"
					+ "name VARCHAR(55),"
					+ "rating FLOAT,"
					+ "address VARCHAR(55),"
					+ "image_url VARCHAR(55),"
					+ "url VARCHAR(55),"
					+ "distance FLOAT,"
					+ "PRIMARY KEY (item_id)"
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(55) NOT NULL,"
					+ "password VARCHAR(55) NOT NULL,"
					+ "first_name VARCHAR(55),"
					+ "last_name VARCHAR(55),"
					+ "PRIMARY KEY (user_id)"
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE categories ("
					+ "item_id VARCHAR(55) NOT NULL,"
					+ "category VARCHAR(55) NOT NULL,"
					+ "PRIMARY KEY (item_id, category),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE history ("
					+ "user_id VARCHAR(55) NOT NULL,"
					+ "item_id VARCHAR(55) NOT NULL,"
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "INSERT INTO users VALUES('1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')";
			statement.executeUpdate(sql);
			
			connection.close();
			System.out.println("Import done successfully");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
