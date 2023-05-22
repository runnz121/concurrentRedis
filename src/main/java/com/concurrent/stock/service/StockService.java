package com.concurrent.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.concurrent.stock.domain.Stock;
import com.concurrent.stock.repository.StockRepository;

import lombok.AllArgsConstructor;

@Service
public class StockService {

	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}


	// 1. java 의 synchronized는 각 프로세스 에서만 보장이됨 따라서 서버가 2대이상이면 synchronized는 소용 없음

	// public synchronized void decrease(Long id, Long quantity) {
	//
	// 	Stock stock = stockRepository.findById(id).orElseThrow();
	//
	// 	stock.decrease(quantity);
	//
	// 	stockRepository.saveAndFlush(stock);
	// }


	// named lock은 부모 트랜잭션과 다른 새로운 트랜잭션에 적용되어야 함으로 설정
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void decrease(Long id, Long quantity) {

		Stock stock = stockRepository.findById(id).orElseThrow();

		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
