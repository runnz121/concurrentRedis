package com.concurrent.stock.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter
@ToString
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long productId;

	private Long quantity;

	// optimistic lock 사용
	@Version
	private Long version;


	public Stock(Long productId, Long quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}


	public void decrease(Long quanity) {
		if (this.quantity - quanity < 0) {
			throw new RuntimeException("error");
		}
		this.quantity = this.quantity - quanity;
	}

}
