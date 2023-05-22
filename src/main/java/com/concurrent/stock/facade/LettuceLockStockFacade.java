package com.concurrent.stock.facade;

import org.springframework.stereotype.Component;

import com.concurrent.stock.repository.RedisLockRepository;
import com.concurrent.stock.service.StockService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class LettuceLockStockFacade {

	private RedisLockRepository redisLockRepository;

	private StockService stockService;

	public void decrease(Long key, Long quantity) throws InterruptedException {
		while(!redisLockRepository.lock(key)) {
			// redis 부하를 줄이기 위해 스레드 슬립을 줌
			Thread.sleep(100);
		}

		try {
			stockService.decrease(key, quantity);
		} finally {
			redisLockRepository.unlock(key);
		}
	}
}
