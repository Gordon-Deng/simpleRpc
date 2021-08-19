package com.gordon.rpc.simple.server.remote;

import com.gordon.rpc.annotation.SRpcProvider;
import com.gordon.rpc.simple.api.dto.ProductDTO;
import com.gordon.rpc.simple.api.service.ProductService;

@SRpcProvider(name="com.gordon.rpc.simple.api.service.ProductService", version = "1.0.0")
public class ProductServiceImpl implements ProductService {

    @Override
    public ProductDTO getProductById(Long id) {
        ProductDTO dto = new ProductDTO();
        dto.setCategory("A类");
        dto.setProductName("产品名称");
        return dto;
    }

}
