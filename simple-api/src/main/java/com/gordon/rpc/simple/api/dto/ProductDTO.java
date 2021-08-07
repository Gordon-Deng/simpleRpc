package com.gordon.rpc.simple.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDTO implements Serializable {

	private String productName;
	
	private String category;
}
