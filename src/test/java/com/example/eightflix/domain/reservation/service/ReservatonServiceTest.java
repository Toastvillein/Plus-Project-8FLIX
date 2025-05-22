package com.example.eightflix.domain.reservation.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.eightflix.domain.movie.entity.Movie;
import com.example.eightflix.domain.movie.repository.MovieRepository;
import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.entity.Seat;
import com.example.eightflix.domain.reservation.repository.SeatRepository;
import com.example.eightflix.domain.user.entity.User;
import com.example.eightflix.domain.user.entity.UserRole;
import com.example.eightflix.domain.user.repository.UserRepository;
import com.example.eightflix.global.exception.BizException;

@SpringBootTest
class ReservatonServiceTest {
	@Autowired
	private LockService lockService;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private UserRepository userRepository;

	private static final int THREAD_COUNT = 1000;

	private Long movieId;
	private Long userId;

	@BeforeEach
	void setup() {
		// 기본 테스트 데이터 셋업
		Movie movie = movieRepository.save(new Movie("영화"));
		User user = userRepository.save(User.builder()
			.email("email")
			.userId("userId")
			.password("password")
			.nickname("nickname")
			.phoneNumber("phone")
			.role(UserRole.USER)
			.build()
		);
		seatRepository.save(new Seat("A1", movie)); // A1 좌석
		seatRepository.save(new Seat("A2", movie)); // A2 좌석
		seatRepository.save(new Seat("A3", movie)); // A3 좌석

		this.movieId = movie.getMovieId();
		this.userId = user.getId();
	}

	@Test
	@DisplayName("동일한 영화 좌석 예매 시 동시성 제어에 성공한다.")
	void reserveMovieConcurrencyTest() throws Exception {
		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
		CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

		IntStream.range(0, THREAD_COUNT).forEach(e -> executorService.submit(() -> {
			try {
				barrier.await(); // 모든 스레드가 여기서 대기하다가 동시에 실행됨

				ReservationRequest request = new ReservationRequest(
					movieId,
					List.of("A1", "A2")
				);

				lockService.reserveMovieWithLock(userId, request);
				successCount.getAndIncrement();  // 성공
			} catch (BizException ex) {
				if (ex.getErrorCode().getCode().equals("ALREADY_RESERVED_SEAT_ERROR")) {
					failCount.getAndIncrement(); // 좌석 중복 오류
				}
				throw ex;
			} catch (BrokenBarrierException | InterruptedException ex) {
				throw new RuntimeException(ex);
			} finally {
				latch.countDown();
			}
		}));

		latch.await(); // 모든 스레드 종료 대기
		executorService.shutdown();

		System.out.println("성공: " + successCount + ", 실패: " + failCount);
		assertThat(successCount.get()).isEqualTo(1);
		assertThat(failCount.get()).isEqualTo(THREAD_COUNT - 1);
	}

	@Test
	@DisplayName("일부만 겹치는 영화 좌석 예매 시 동시성 제어에 성공한다.")
	void reserveDuplicateSeatConcurrencyTest() throws Exception {
		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
		CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

		List<ReservationRequest> requests = List.of(
			new ReservationRequest(movieId, List.of("A1", "A2")),
			new ReservationRequest(movieId, List.of("A2", "A3"))
		);

		for (int i = 0; i < THREAD_COUNT; i++) {
			int finalI = i%2;
			executorService.submit(() -> {
				try {
					barrier.await(); // 모든 스레드가 여기서 대기하다가 동시에 실행됨
					lockService.reserveMovieWithLock(userId, requests.get(finalI));
					successCount.getAndIncrement();  // 성공
				} catch (BizException e) {
					if (e.getErrorCode().getCode().equals("ALREADY_RESERVED_SEAT_ERROR")) {
						failCount.getAndIncrement(); // 좌석 중복 오류
					}
					throw e;
				} catch (BrokenBarrierException | InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await(); // 모든 스레드 종료 대기
		executorService.shutdown();

		System.out.println("성공: " + successCount + ", 실패: " + failCount);
		assertThat(successCount.get()).isEqualTo(1);
		assertThat(failCount.get()).isEqualTo(THREAD_COUNT-1);
	}

}