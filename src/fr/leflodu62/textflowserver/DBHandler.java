package fr.leflodu62.textflowserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBHandler {
	
	private static final String URL = "jdbc:sqlite:users.db";
	
	private Connection connection = null;
	
	protected DBHandler() {}
	
	public boolean isConnected() {
		try {
			if (connection == null || connection.isClosed()) {
				return false;
			} else {
				return true;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void connect() {
		if (!isConnected()) {
			try {
				connection = DriverManager.getConnection(URL);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void disconnect() {
		if (isConnected()) {
			try {
				connection.close();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createTableIfDoesntExist(){
		connect();
		try {
			final PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (username VARCHAR(20), password VARCHAR)");
			ps.execute();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}
	
	public boolean isInDatabase(String username) {
		boolean contains = false;
		connect();
		try {
			final PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
			ps.setString(1, username);
			contains = ps.executeQuery().next();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		return contains;
	}
	
	public String getEncryptedPassword(String username) {
		String password = null;
		connect();
		try {
			final PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
			ps.setString(1, username);
			final ResultSet result = ps.executeQuery();

			while(result.next()){
				password = result.getString("password");
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		return password;
	}
	
	public void addUser(String username, String password) {
		connect();
		try {
			final PreparedStatement ps = connection.prepareStatement("INSERT INTO users values (?, ?)");
			ps.setString(1, username);
			ps.setString(2, password);
			ps.executeUpdate();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}

	public void removeUser(String username) {
		connect();
		try {
			final PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE username = ?");
			ps.setString(1, username);
			ps.executeUpdate();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}
	
}
