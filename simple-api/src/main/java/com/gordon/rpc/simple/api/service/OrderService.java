package com.gordon.rpc.simple.api.service;

import com.gordon.rpc.simple.api.dto.OrderDTO;

public interface OrderService {
	
	OrderDTO getOrderById(String orderNo);
}
