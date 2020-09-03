package com.unifun.workers;


import com.unifun.db.DBlayer;
import com.unifun.model.SmsData;
import com.unifun.services.QueueService;
import com.unifun.utils.TransactionIdGenerator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BulkAddQueueWorker {
	private static TransactionIdGenerator transactionIdGenerator = new TransactionIdGenerator();

	private static int tps = 820;
	private static long sendingPeriodSec = 10;

	private static int maxQueueSize = 20000;

	private static DBlayer dBlayer = DBlayer.getInstance();
	private static QueueService queueService = QueueService.getInstance();
	static Runnable bulkProcess = new Runnable() {


		@Override
		public void run() {
			int sizeBulk = queueService.queueSize();

			if(sizeBulk <= maxQueueSize){
				logger.info("BulQuee now size is: " + sizeBulk);

				addToQueue();
			}else{
				logger.info("BulQuee is full | Now size is: " + sizeBulk);
			}
		}
	};
	private static ScheduledExecutorService bulkSendingService = Executors.newScheduledThreadPool(1);

	private static final Logger logger = LogManager.getLogger(BulkAddQueueWorker.class);

	private static volatile ConcurrentHashMap<Integer, AtomicInteger> processCounters;

	private static void addToQueue(){
		try {
			CachedRowSet requests = dBlayer.getBulkRequests(Integer.parseInt(String.valueOf(tps * sendingPeriodSec)));

			while (requests.next()) {
				String sourceAddress = "BULK INTERFACE";


				int request_id = requests.getInt("id");
				int compaign_id = requests.getInt("compaign_id");
				long msisdn = requests.getLong("msisdn");
				String messageText = requests.getString("message");

				int transaction_id = transactionIdGenerator.getNewTransactionID();
				SmsData smsData = new SmsData(transaction_id,
						sourceAddress,
						"5",
						"0",
						msisdn,
						"1",
						"1",
						messageText,
						(short) (messageText.length() / 70 + 1),
						(short) 8,
						(short) 0,
						new Timestamp(System.currentTimeMillis()),
						null,
						0,
						"0",
						"0",
						"1",
						0
				);

				if (queueService.addToQueue(smsData)) {
					dBlayer.addToUpdateBatch(transaction_id, request_id);
					AtomicInteger pCount = processCounters.get(compaign_id);
					if (pCount == null) {
						pCount = new AtomicInteger(0);
						processCounters.put(compaign_id, pCount);
					}
					pCount.incrementAndGet();
				} else {
					logger.error("BulkSendingWorker - TransactionID: " + smsData.getTransactionId()
							+ " NOT accepted by Main controller");
				}

			}
			dBlayer.executeUpdateBach();
			if (requests.size() > 0) {
				processCounters.forEach((k, v) -> {
					DBlayer.getInstance().updateCampaignCounters(k, v.get());
					v.set(0);
				});
			}


		} catch (Exception e) {
			logger.error(e.toString() + ": " + Arrays.toString(e.getStackTrace()));
		}
	}



	public static void startBulkSendingWorker() {
		processCounters = new ConcurrentHashMap<Integer, AtomicInteger>();
		bulkSendingService.scheduleAtFixedRate(bulkProcess, 0, sendingPeriodSec, TimeUnit.SECONDS);
	}

	public static void stopBulkSendingWorker() {
		bulkSendingService.shutdown();
	}

}
