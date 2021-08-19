package com.gordon.rpc.simple.server.remote;

import com.gordon.rpc.annotation.SRpcProvider;
import com.gordon.rpc.simple.api.dto.OrderDTO;
import com.gordon.rpc.simple.api.service.OrderService;

import java.util.Date;

@SRpcProvider(name="com.gordon.rpc.simple.api.service.OrderService", version = "1.0.0")
public class OrderServiceImpl implements OrderService {

    @Override
    public OrderDTO getOrderById(String orderNo) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderNo(orderNo);
        dto.setTime(new Date().toString());
        return dto;
    }
}
