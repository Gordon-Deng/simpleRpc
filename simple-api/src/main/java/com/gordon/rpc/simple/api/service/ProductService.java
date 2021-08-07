package com.gordon.rpc.simple.api.service;

import com.gordon.rpc.simple.api.dto.ProductDTO;

public interface ProductService {
	
	ProductDTO getProductById(Long id);
}
