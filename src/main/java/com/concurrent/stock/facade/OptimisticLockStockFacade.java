package com.concurrent.stock.facade;

import org.springframework.stereotype.Service;

import com.concurrent.stock.service.OptimisticLockService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OptimisticLockStockFacade {

	private OptimisticLockService optimisticLockService;

	// 낙관락은 실패했을 떄 재시도 처리가 필요
	public void decrease(Long id, Long quantity) throws InterruptedException {

		while (true) {
			try {
				optimisticLockService.decrease(id, quantity);
				// 정상 업데이크면 종료
				break;
			} catch (Exception e) {
				Thread.sleep(50);
			}
		}
	}
}
