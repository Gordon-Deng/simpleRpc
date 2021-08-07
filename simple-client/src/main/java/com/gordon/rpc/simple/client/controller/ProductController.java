package com.gordon.rpc.simple.client.controller;

import com.gordon.rpc.annotation.SRpcConsumer;
import com.gordon.rpc.simple.api.dto.ProductDTO;
import com.gordon.rpc.simple.api.service.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

	@SRpcConsumer(version = "1.0.0")
	private ProductService productService;
	
	@RequestMapping("/getById")
	public ProductDTO getProduct(@RequestParam(required = true) Long id) {
		ProductDTO productDTO = productService.getProductById(id);
		System.out.println(Optional.ofNullable(productDTO).map(p -> p.getProductName()).orElse(null));
		return productDTO;
	}
}
