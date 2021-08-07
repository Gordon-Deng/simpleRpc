package com.gordon.rpc.simple.client.controller;

import com.gordon.rpc.annotation.SRpcConsumer;
import com.gordon.rpc.simple.api.dto.OrderDTO;
import com.gordon.rpc.simple.api.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {
	
	@SRpcConsumer(version = "1.0.0")
	private OrderService orderService;
	
	@RequestMapping("/getByOrderNo")
	public OrderDTO getProduct(@RequestParam(required = true) String orderNo) {
		OrderDTO orderDTO = orderService.getOrderById(orderNo);
		System.out.println(Optional.ofNullable(orderDTO).map(o -> o.getOrderNo()).orElse(null));
		return orderDTO;
	}
}
