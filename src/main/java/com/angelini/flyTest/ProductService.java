package com.angelini.flyTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductService {
	
	public static final int IN_CART = 0;
	public static final int PURCHASED = 1;

	private static Product toSingleProduct(ResultSet result) throws SQLException {
		Product product = new Product();
		
		product.setId(result.getLong("id"));
		product.setName(result.getString("name"));
		product.setPrice(result.getDouble("price"));
		product.setDescription(result.getString("description"));
		product.setReleaseDate(result.getDate("release_date"));
		product.setType(result.getString("type"));
		
		return product;
	}
	
	private static List<Product> toProductList(ResultSet results) throws SQLException {
		List<Product> products = new ArrayList<Product>();
		
		while (results.next()) {
			products.add(toSingleProduct(results));
		}
		
		return products;
	}
	
	public static List<String> getTypes(Connection conn) throws SQLException {
		String sql = " SELECT distinct(type)" +
					 " FROM products";
		
		PreparedStatement query = conn.prepareStatement(sql);
		ResultSet results = query.executeQuery();
		
		List<String> types = new ArrayList<String>();
		
		while (results.next()) {
			types.add(results.getString("type"));
		}
		
		return types;
	}

	public static List<Product> getProducts(Connection conn, String name, Date date, String type) throws SQLException {
		int i = 0;
		int nameInd = 0;
		int dateInd = 0;
		int typeInd = 0;
		
		String sql = " SELECT id, name, price, description, release_date, type" +
					 " FROM products" +
					 " WHERE null is null";
		
		if (name != null && name.length() != 0) {
			nameInd = ++i;
			sql += " AND name = ?";
		}
		
		if (date != null) {
			dateInd = ++i;
			sql += " AND release_date = ?";
		}
		
		if (type != null && type.length() != 0) {
			typeInd = ++i;
			sql += " AND type = ?";
		}
		
		PreparedStatement query = conn.prepareStatement(sql);
		if (name != null && name.length() != 0) query.setString(nameInd, name);
		if (date != null) query.setDate(dateInd, new java.sql.Date(date.getTime()));
		if (type != null && type.length() != 0) query.setString(typeInd, type);
		
		ResultSet results = query.executeQuery();
		
		return toProductList(results);
	}
	
	public static List<Product> getUserCart(Connection conn, int userId, int state) throws SQLException {
		String sql = " SELECT p.id, p.name, p.price, p.description, p.release_date, p.type" +
				  	 " FROM cart c" +
				  	 " INNER JOIN products p" +
				  	 "         ON p.id = c.product_id" +
				  	 " WHERE state = ?" +
				  	 "   AND c.user_id = ?";
		
		PreparedStatement query = conn.prepareStatement(sql);
		query.setInt(1, state);
		query.setInt(2, userId);
		
		return toProductList(query.executeQuery());
	}

	public static List<Product> addToCart(Connection conn, int userId, int productId) throws SQLException {
		String sql = " INSERT INTO cart (product_id, user_id, state)" +
					 " VALUES (?, ?, ?)";
		
		PreparedStatement query = conn.prepareStatement(sql);
		query.setInt(1, productId);
		query.setInt(2, userId);
		query.setInt(3, IN_CART);
		
		query.executeUpdate();
		
		return getUserCart(conn, userId, IN_CART);
	}

	public static List<Product> purchase(Connection conn, int userId) throws SQLException {
		List<Product> purchased = getUserCart(conn, userId, IN_CART);
		
		String sql = " UPDATE cart" +
					 " SET state = ?" +
					 " WHERE user_id = ?" +
					 "   AND state = ?";
		
		PreparedStatement query = conn.prepareStatement(sql);
		query.setInt(1, PURCHASED);
		query.setInt(2, userId);
		query.setInt(3, IN_CART);
		
		query.executeUpdate();
		
		return purchased;
	}
	
	
	
}
