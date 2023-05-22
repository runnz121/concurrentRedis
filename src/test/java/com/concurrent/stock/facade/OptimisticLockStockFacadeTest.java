package com.concurrent.stock.facade;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.concurrent.stock.domain.Stock;
import com.concurrent.stock.repository.StockRepository;

@SpringBootTest
class OptimisticLockStockFacadeTest {

	@Autowired
	private OptimisticLockStockFacade stockService;

	@Autowired
	private StockRepository stockRepository;

	@BeforeEach
	public void before() {

		Stock stock = new Stock(1L, 100L);
		stockRepository.saveAndFlush(stock);
	}

	@AfterEach
	public void after() {
		stockRepository.deleteAll();;
	}

	@Test
	public void 동시에_100개_요청() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		// 다른 스레드에서 작업하는 것이 끝낼 때까지 대기 시킴
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					stockService.decrease(1L, 1L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown(); // latch 숫자 감소
				}
			});
		}

		latch.await(); // latch 의 숫자가 0이 될 떄까지 기다림

		Stock stock = stockRepository.findById(1L).orElseThrow();

		// 100 - (1 * 100) = 0
		assertEquals(0L, stock.getQuantity());
	}

}