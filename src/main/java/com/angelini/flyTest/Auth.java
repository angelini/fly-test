package com.angelini.flyTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angelini.fly.AuthenticationCheck;
import com.angelini.fly.FlyDB;

public class Auth implements AuthenticationCheck {

	private FlyDB db;
	
	private static Logger log = LoggerFactory.getLogger(Auth.class);
	
	public void init(FlyDB db) {
		this.db = db;
	}

	public boolean check(String username, String password) {
		try {
			Connection conn = db.getConn();
			String sql = " SELECT password" +
						 " FROM users" +
						 " WHERE username = ?";
			
			PreparedStatement query = conn.prepareStatement(sql);
			query.setString(1, username);
			
			ResultSet result = query.executeQuery();
			if (result.next() && result.getString("password").equals(password)) {
				return true;
			}
			
		} catch (SQLException e) {
			log.error("Error verifying username & password", e);
			return false;
		}
		
		return false;
	}

}
