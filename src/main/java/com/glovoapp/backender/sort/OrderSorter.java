package com.glovoapp.backender.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.comparators.ComparatorChain;

import com.glovoapp.backender.Courier;
import com.glovoapp.backender.DistanceCalculator;
import com.glovoapp.backender.Order;

public class OrderSorter implements IOrderSorter {

	ComparatorChain<Order> chain = new ComparatorChain<>();

	/**
	 * Method to sort orders by propertie definition.
	 * 
	 * @param courier This param is used to find the orders available for courier.
	 */

	public int sortOrdersByFields(Courier courier,  List<String> sort_fields) {
		for (String sortField : sort_fields) {
			switch (sortField.toUpperCase()) {
			case "LOCATION":
				chain.addComparator(sortByLocation(courier));

			case "VIP":
				chain.addComparator(sortByVip());

			case "FOOD":
				chain.addComparator(sortByFood());

			}
		}
		return 0;
	}

	/**
	 * Method to separate in slot of 500 meters all orders.
	 * 
	 * @param courier filteredOrders the courier param is user to find the distance
	 *                and filteredOrders is a repository to separate in slots.
	 * @return Map<Integer, List<Order>> Returns a list Orders.
	 */

	public Map<Integer, List<Order>> slots500Meters(List<Order> filteredOrders, Courier courier,
			List<Integer> max_slots_count, int distance_slot, List<String> sort_fields) {

		Map<Integer, List<Order>> ordersBySlotDistance = new LinkedHashMap<>();
		for (Integer distanceSlot : max_slots_count) {
			List<Order> orders = filteredOrders.stream().filter(
					x -> (DistanceCalculator.calculateDistance(courier.getLocation(), x.getPickup()) > distanceSlot)
							&& (DistanceCalculator.calculateDistance(courier.getLocation(),
									x.getPickup()) < (distanceSlot + distance_slot)))
					.collect(Collectors.toList());
			this.sortOrdersByFields(courier, sort_fields);
			Collections.sort(orders, chain);
			chain = new ComparatorChain<>();
			ordersBySlotDistance.put(distanceSlot, orders);
		}
		return ordersBySlotDistance;
	}

	/**
	 * Method to sort orders by location
	 * 
	 * @param courier the courier param is user to get distance.
	 * @return Comparator<Order>.
	 */

	public Comparator<Order> sortByLocation(Courier c) {
		return Comparator
				.comparingDouble(order -> DistanceCalculator.calculateDistance(c.getLocation(), order.getPickup()));
	}

	/**
	 * Method to sort orders by vip
	 * 
	 * @return Comparator<Order>.
	 */

	public Comparator<Order> sortByVip() {
		return Comparator.comparing(Order::getVip).reversed();
	}

	/**
	 * Method to sort orders by food
	 * 
	 * @return Comparator<Order>.
	 */

	private Comparator<Order> sortByFood() {
		return Comparator.comparing(Order::getFood).reversed();
	}

}
