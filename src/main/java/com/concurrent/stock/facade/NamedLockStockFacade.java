package com.concurrent.stock.facade;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.concurrent.stock.repository.LockRepository;
import com.concurrent.stock.service.StockService;


@Component
public class NamedLockStockFacade {

	private final LockRepository lockRepository;

	private final StockService stockService;

	public NamedLockStockFacade(LockRepository lockRepository, StockService stockService) {
		this.lockRepository = lockRepository;
		this.stockService = stockService;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {
		try {
			// 락 생성
			lockRepository.getLock(id.toString());
			stockService.decrease(id, quantity);
		} finally {
			// 락 해제
			lockRepository.releaseLock(id.toString());
		}
	}
}
