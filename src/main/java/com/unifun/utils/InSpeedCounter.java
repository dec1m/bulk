package com.unifun.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class InSpeedCounter {
	private static InSpeedCounter instance;	// singleton
	private static final Logger logger = LogManager.getLogger(InSpeedCounter.class);
	private final AtomicInteger counter;
	private ScheduledExecutorService scheduler;

	/**
	 */
	private InSpeedCounter() {
		counter = new AtomicInteger(0);
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> {
			synchronized(counter) {
				final int currentTPS = counter.get();
				logger.info("TPS: " + currentTPS);
				counter.set(0);
			}
		}, 0, 1, TimeUnit.SECONDS);   // log outgoing transactions each second
	}

	/**
	 */
	public static synchronized InSpeedCounter getInstance() {
		if(instance == null) {
			instance = new InSpeedCounter();
		}
		return instance;
	}

	/**
	 */
	public void increment(int count) {
		counter.set(counter.get() + count);
	}
}