package com.angelini.flyTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angelini.fly.FlyDB;
import com.angelini.fly.HttpRequest;
import com.angelini.fly.HttpResponse;
import com.angelini.fly.RoutedServlet;
import com.angelini.fly.Router;

public class ProductRoutes implements RoutedServlet {

	private FlyDB db;
	private static Logger log = LoggerFactory.getLogger(ProductRoutes.class);
	
	public void init(FlyDB db) {
		this.db = db;
	}
	
	@Router(route = "/init", verb = Router.GET)
	public void init(HttpRequest req, HttpResponse resp) throws IOException {
		try {
			int userId = 1;
			
			Connection conn = db.getConn();
			List<String> types = ProductService.getTypes(conn);
			List<Product> cart = ProductService.getUserCart(conn, userId, ProductService.IN_CART);
			conn.close();
			
			types.add(0, "");
			resp.sendObject(new InitObject(userId, types, cart));
			
		} catch (SQLException e) {
			resp.setStatus(500);
			log.error("Type fetch exception", e);
		}
	}
	
	@Router(route = "/", verb = Router.GET)
	public void allProducts(HttpRequest req, HttpResponse resp) throws IOException {
		String name = req.getQuery("name");
		String type = req.getQuery("type");
		Date date = req.getQueryDate("date");
		
		try {
			Connection conn = db.getConn();
			List<Product> products = ProductService.getProducts(conn, name, date, type);
			conn.close();
			
			resp.sendObject(products);
		
		} catch (SQLException e) {
			resp.setStatus(500);
			log.error("Products fetch exception", e);
		}
	}
	
	@Router(route = "/cart/:userId", verb = Router.GET)
	public void addCart(HttpRequest req, HttpResponse resp) throws IOException {
		String productStr = req.getQuery("rowSelect");
		String userStr = req.getParam("userId");
		
		if (productStr == null || userStr == null) {
			resp.setStatus(400);
			return;
		}
		
		try {
			int userId = Integer.parseInt(userStr);
			int productId = Integer.parseInt(productStr);
			
			Connection conn = db.getConn();
			List<Product> products = ProductService.addToCart(conn, userId, productId);
			conn.close();
			
			resp.sendObject(products);
			
		} catch (SQLException e) {
			resp.setStatus(500);
			log.error("Add to cart exception", e);
		}
		
		resp.setStatus(200);
	}
	
	@Router(route = "/purchase/:userId", verb = Router.GET)
	public void purchase(HttpRequest req, HttpResponse resp) throws IOException {
		String userStr = req.getParam("userId");
		
		if (userStr == null) {
			resp.setStatus(404);
			return;
		}
		
		try {
			int userId = Integer.parseInt(userStr);
			
			Connection conn = db.getConn();
			List<Product> products = ProductService.purchase(conn, userId);
			conn.close();
			
			resp.sendObject(new Receipt(products));
			
		} catch (SQLException e) {
			resp.setStatus(500);
			log.error("Add to cart exception", e);
		}
	}

}
