package com.concurrent.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.concurrent.stock.domain.Stock;
import com.concurrent.stock.repository.StockRepository;


@Service
public class PessimisticLockService {

	private final StockRepository stockRepository;

	PessimisticLockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {

		Stock stock = stockRepository.findByIdWithPessimisticLock(id);

		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);

	}

}
