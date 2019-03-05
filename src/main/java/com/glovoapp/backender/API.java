package com.glovoapp.backender;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.glovoapp.backender.service.IOrderFinder;

@Controller
@ComponentScan("com.glovoapp.backender")
@EnableAutoConfiguration
public class API {
	private final String welcomeMessage;
	private final OrderRepository orderRepository;
	private final CourierRepository courierRepository;
	private final IOrderFinder orderFilter;


	ComparatorChain<Order> chain = new ComparatorChain<>();

	@Autowired
	API(@Value("${backender.welcome_message}") String welcomeMessage, OrderRepository orderRepository,
			CourierRepository courierRepository, IOrderFinder orderFilter) {
		this.welcomeMessage = welcomeMessage;
		this.orderRepository = orderRepository;
		this.courierRepository = courierRepository;
		this.orderFilter = orderFilter;

	}

	@RequestMapping("/")
	@ResponseBody
	String root() {
		return welcomeMessage;
	}

	@RequestMapping("/orders")
	@ResponseBody
	List<OrderVM> orders() {
		return orderRepository
				.findAll().stream().map(order -> new OrderVM(order.getId(), order.getDescription()))
				.collect(Collectors.toList());
	}

	@RequestMapping("/orders/{courierId}")
	@ResponseBody
	List<OrderVM> ordersByCourier(@PathVariable String courierId) {
		Courier courier = courierRepository.findById(courierId);
		Assert.notNull(courier, "courier "+courierId+ " not Exist");
		return orderFilter.findOrders(courier).stream()
				.map(order -> new OrderVM(order.getId(), order.getDescription()))
				.collect(Collectors.toList());
	}

	public static void main(String[] args) {
		SpringApplication.run(API.class);
	}
}
