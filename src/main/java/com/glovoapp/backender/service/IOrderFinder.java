package com.glovoapp.backender.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.glovoapp.backender.Courier;
import com.glovoapp.backender.Order;

@Component
public interface IOrderFinder {
	
	public List<Order> findOrders(Courier courier);

}
