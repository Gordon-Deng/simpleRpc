package com.gordon.rpc.simple.server.remote;

import com.gordon.rpc.annotation.SRpcProvider;

import java.util.Date;

@SRpcProvider(name="com.orc.rpc.example.api.service.OrderService", version = "1.0.0")
public class OrderServiceImpl {

    @Override
    public OrderDTO getOrderById(String orderNo) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderNo(orderNo);
        dto.setTime(new Date().toString());
        return dto;
    }
}
