package com.unifun.workers;

import com.unifun.db.DBlayer;
import com.unifun.utils.PropertyReader;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DeliveryStatusWorker {
	private static PropertyReader reader = new PropertyReader();
	private static DBlayer dBlayer = DBlayer.getInstance();
	private static ScheduledExecutorService worker = Executors.newScheduledThreadPool(1);

	public static void start() {
		final Properties properties = reader.readParamFromFile();
		worker.scheduleAtFixedRate(moveAndUpdate, 0, Long.parseLong(properties.getProperty("moveAndUpdatePeriodSec")), TimeUnit.SECONDS);
	}

	static Runnable moveAndUpdate = new Runnable() {
		@Override
		public void run() {
			dBlayer.moveBulkDelivery();
			dBlayer.batchUpdateDeliveryStatusExecute();
		}
	};
}
