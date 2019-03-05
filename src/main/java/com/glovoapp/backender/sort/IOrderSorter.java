package com.glovoapp.backender.sort;

import java.util.List;
import java.util.Map;

import com.glovoapp.backender.Courier;
import com.glovoapp.backender.Order;

public interface IOrderSorter {
	
	
	public Map<Integer, List<Order>> slots500Meters(List<Order> filteredOrders, Courier courier,
			List<Integer> max_slots_count, int distance_slot, List<String> sort_fields);
	
	

}
