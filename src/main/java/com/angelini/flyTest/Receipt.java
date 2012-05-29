package com.angelini.flyTest;

import java.util.List;

public class Receipt {
	
	private List<Product> purchased;
	
	public Receipt(List<Product> purchased) {
		this.purchased = purchased;
	}

	public List<Product> getPurchased() {
		return purchased;
	}

	public void setPurchased(List<Product> purchased) {
		this.purchased = purchased;
	}
	
}
