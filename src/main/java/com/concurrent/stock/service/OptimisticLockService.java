package com.concurrent.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.concurrent.stock.domain.Stock;
import com.concurrent.stock.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
public class OptimisticLockService {

	private final StockRepository stockRepository;

	OptimisticLockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {

		Stock stock = stockRepository.findByIdWithOptimisticsticLock(id);

		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);

	}
}
