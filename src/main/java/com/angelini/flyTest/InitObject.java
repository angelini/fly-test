package com.angelini.flyTest;

import java.util.List;

public class InitObject {
	
	private int userId;
	private List<String> types;
	private List<Product> cart;
	
	public InitObject(int userId, List<String> types, List<Product> cart) {
		this.userId = userId;
		this.types = types;
		this.cart = cart;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public List<Product> getCart() {
		return cart;
	}

	public void setCart(List<Product> cart) {
		this.cart = cart;
	}

}
