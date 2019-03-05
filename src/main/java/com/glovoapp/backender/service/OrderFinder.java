package com.glovoapp.backender.service;

import static com.glovoapp.backender.Vehicle.ELECTRIC_SCOOTER;
import static com.glovoapp.backender.Vehicle.MOTORCYCLE;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.glovoapp.backender.Courier;
import com.glovoapp.backender.DistanceCalculator;
import com.glovoapp.backender.Order;
import com.glovoapp.backender.OrderRepository;
import com.glovoapp.backender.Vehicle;
import com.glovoapp.backender.sort.IOrderSorter;
import com.glovoapp.backender.sort.OrderSorter;

/**
 * This class have the filters funtionality
 * 
 * @author: Santiago Kiryluk Maclean
 * @version: March 2019
 */

@Service
public class OrderFinder implements IOrderFinder {

	private final OrderRepository orderRepository;

	private List<String> words_glovo_box;
	private Double further_than_distance;
	private List<String> sort_fields;
	List<Integer> max_slots_count;
	private int distance_slot;

	@Autowired
	public OrderFinder(OrderRepository orderRepository,
			@Value("#{'${backender.words_glovo_box}'.split(',')}") List<String> wordsGlovoBox,
			@Value("${backender.further_than_distance}") double furtherThanDistance,
			@Value("#{'${backender.sort_fields}'.split(',')}") List<String> sortFields,
			@Value("#{'${backender.max_slots_count}'.split(',')}") List<Integer> maxSlotsCount,
			@Value("${backender.distance_slot}") int distanceSlot) {
		this.orderRepository = orderRepository;
		this.words_glovo_box = wordsGlovoBox;
		this.further_than_distance = furtherThanDistance;
		this.sort_fields = sortFields;
		this.max_slots_count = maxSlotsCount;
		this.distance_slot = distanceSlot;
	}

	/**
	 * Method to find the orders by Courrier
	 * 
	 * @param courier This param is used to find the orders available for corrier.
	 * @return List<Order> Returns a list of Orders.
	 */

	public List<Order> findOrders(Courier courier) {

		Assert.notNull(courier, "the courier is mandatory.");
		IOrderSorter orderSorter = new OrderSorter();

		List<Order> ordersFiltered = filteredOrders(courier);
		Map<Integer, List<Order>> ordersByDistanceSlots = orderSorter.slots500Meters(ordersFiltered, courier,
				max_slots_count, distance_slot, sort_fields);
		return ordersByDistanceSlots.entrySet().stream().flatMap(e -> e.getValue().stream())
				.collect(Collectors.toList());

	}

	/**
	 * Method to search which order need box and distance.
	 * 
	 * @param courier This param is used to find the orders available for courier.
	 * @return List<Order> Returns a list Orders.
	 */

	public List<Order> filteredOrders(Courier courier) {
		return orderRepository.findAll().stream().filter(haveBox(courier)).filter(distanceForMove(courier))
				.collect(Collectors.toList());
	}

	/**
	 * Method to search which order need box.
	 * 
	 * @param courier This param is used to find the orders available for courier.
	 * @return List<Order> Returns a list Orders.
	 */

	private Predicate<Order> haveBox(Courier courier) {
		return order -> {
			String desc = order.getDescription().toLowerCase();
			boolean word = words_glovo_box.stream().anyMatch(desc::contains);
			return !word || word && courier.getBox();
		};
	}

	/**
	 * Method to search which order are near to use diferents vehicle.
	 * 
	 * @param courier This param is used to find the orders available for courier.
	 * @return List<Order> Returns a list Orders.
	 */

	private Predicate<Order> distanceForMove(Courier courier) {
		return order -> {
			double distanceFromCourrier = DistanceCalculator.calculateDistance(courier.getLocation(),
					order.getPickup());
			Vehicle vehicle = courier.getVehicle();
			return distanceFromCourrier <= further_than_distance
					|| (vehicle.equals(MOTORCYCLE) || vehicle.equals(ELECTRIC_SCOOTER));
		};
	}

}
