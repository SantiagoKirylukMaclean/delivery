package com.glovoapp.backender.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.glovoapp.backender.Courier;
import com.glovoapp.backender.CourierRepository;
import com.glovoapp.backender.Location;
import com.glovoapp.backender.Order;
import com.glovoapp.backender.OrderRepository;
import com.glovoapp.backender.service.IOrderFinder;
import com.glovoapp.backender.service.OrderFinder;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
public class OrderFinderTest {

	private List<String> sort_fields;

	@Value("#{'${backender.words_glovo_box}'.split(',')}")
	private List<String> words_glovo_box;

	@Value("${backender.further_than_distance}")
	private Double further_than_distance;

	@Value("#{'${backender.max_slots_count}'.split(',')}")
	List<Integer> max_slots_count;

	@Value("${backender.distance_slot}")
	private int distance_slot;

	@Test
	@DisplayName("Should return order number 11 in the first position and order 9 in the third position because the order of priorities is given by vip, food and location")
	public void testReturnOrder11FirstAndOrder9tThird() throws Exception {
		sort_fields = Arrays.asList("vip", "food", "location");
		Courier courier = new CourierRepository().findById("courier-1");
		OrderRepository orderRepository = new OrderRepository();
		IOrderFinder orderFilter = new OrderFinder(orderRepository, words_glovo_box, further_than_distance, sort_fields,
				max_slots_count, distance_slot);

		List<Order> orders = orderFilter.findOrders(courier);
		Order firstOrderExpected = new Order().withId("order-11").withDescription("Lemon Pie").withFood(true)
				.withVip(true).withPickup(new Location(41.4067, 2.1962397))
				.withDelivery(new Location(41.454834, 2.1125979));

		Order thirdOrderExpected = new Order().withId("order-9").withDescription("Notebook").withFood(false)
				.withVip(true).withPickup(new Location(41.3963422, 2.1962397))
				.withDelivery(new Location(41.454834, 2.1125979));

		assertEquals(firstOrderExpected, orders.get(0));
		assertEquals(thirdOrderExpected, orders.get(2));

	}

	@Test
	@DisplayName("Should return in fist place Order-2. Order priorities is given by vip, food and location for each slot")
	public void testReturnOrder2First() throws Exception {
		sort_fields = Arrays.asList("vip", "food", "location");
		Courier courier = new CourierRepository().findById("courier-2");

		OrderRepository orderRepository = new OrderRepository();
		IOrderFinder orderFilter = new OrderFinder(orderRepository, words_glovo_box, further_than_distance, sort_fields,
				max_slots_count, distance_slot);

		List<Order> orders = orderFilter.findOrders(courier);
		Order firstOrderExpected = new Order().withId("order-2").withDescription("I want a Flamingo").withFood(true)
				.withVip(true).withPickup(new Location(41.4165463, 2.2063997))
				.withDelivery(new Location(41.427834, 2.1875979));

		assertEquals(firstOrderExpected, orders.get(0));

	}

	@Test
	@DisplayName("Should return order number 11 in the first position and order 1 in the third position because the order of priorities is given by food, vip and location")
	public void testReturnOrder1Third() throws Exception {
		sort_fields = Arrays.asList("food", "vip", "location");
		Courier courier = new CourierRepository().findById("courier-1");

		OrderRepository orderRepository = new OrderRepository();
		IOrderFinder orderFilter = new OrderFinder(orderRepository, words_glovo_box, further_than_distance, sort_fields,
				max_slots_count, distance_slot);
		
		Order firstOrderExpected = new Order().withId("order-11").withDescription("Lemon Pie").withFood(true)
				.withVip(true).withPickup(new Location(41.4067, 2.1962397))
				.withDelivery(new Location(41.454834, 2.1125979));

		List<Order> orders = orderFilter.findOrders(courier);
		Order thirdOrderexpected = new Order().withId("order-3").withDescription("I want a big CaKe")
				.withFood(true).withVip(false).withPickup(new Location(41.4465463, 2.2263997))
				.withDelivery(new Location(41.127834, 2.1575979));

		assertEquals(firstOrderExpected, orders.get(0));
		assertEquals(thirdOrderexpected, orders.get(2));

	}
	
	@Test
	@DisplayName("Should return order number 10 in the third position because the order of priorities is given by food, vip and location")
	public void testReturnOrder10Third() throws Exception {
		sort_fields = Arrays.asList("vip", "food", "location");
		Courier courier = new CourierRepository().findById("courier-3");

		OrderRepository orderRepository = new OrderRepository();
		IOrderFinder orderFilter = new OrderFinder(orderRepository, words_glovo_box, further_than_distance, sort_fields,
				max_slots_count, distance_slot);


		List<Order> orders = orderFilter.findOrders(courier);
		Order thirdOrderexpected = new Order().withId("order-10").withDescription("Orange Rugby ball chocolate")
				.withFood(true).withVip(false).withPickup(new Location(40.4365463, 3.2163997))
				.withDelivery(new Location(45.117834, 2.1475979));
	
		assertEquals(thirdOrderexpected, orders.get(2));

	}
	
	@Test
	@DisplayName("Should return 4 orders because the Glover have not a box")
	public void testReturn4Orders() throws Exception {
		sort_fields = Arrays.asList("food", "vip", "location");
		Courier courier = new CourierRepository().findById("courier-4");

		OrderRepository orderRepository = new OrderRepository();
		IOrderFinder orderFilter = new OrderFinder(orderRepository, words_glovo_box, further_than_distance, sort_fields,
				max_slots_count, distance_slot);


		List<Order> orders = orderFilter.findOrders(courier);
		int orderExpected = 4;
	
		assertEquals(orderExpected, orders.size());

	}

}
