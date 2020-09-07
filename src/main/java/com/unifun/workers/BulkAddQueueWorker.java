package com.unifun.workers;


import com.unifun.db.DBlayer;
import com.unifun.model.SmsData;
import com.unifun.services.QueueService;
import com.unifun.utils.PropertyReader;
import com.unifun.utils.TransactionIdGenerator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BulkAddQueueWorker {
	private static TransactionIdGenerator transactionIdGenerator =  TransactionIdGenerator.getInstance();

	private static int bulkSelectSize;
	private static int selectPeriodSec;
	private static int maxQueueSize;
	private static PropertyReader reader = new PropertyReader();
	private static DBlayer dBlayer = DBlayer.getInstance();
	private static QueueService queueService = QueueService.getInstance();
	static Runnable bulkProcess = new Runnable() {
		@Override
		public void run() {
			int sizeBulk = queueService.queueSize();

			if(sizeBulk <= maxQueueSize){
				logger.info("BulQuee now size is: " + sizeBulk);
				final long start = System.currentTimeMillis();
				addToQueue();
				final long stop = System.currentTimeMillis();
				logger.info("Adding to the queue: Time :  " +  (stop - start) + " batch size : " + + sizeBulk);

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
			CachedRowSet requests = dBlayer.getBulkRequests(Integer.parseInt(String.valueOf( (int) ((double) bulkSelectSize * selectPeriodSec * 1.2d))));

			while (requests.next()) {
				int request_id = requests.getInt("id");
				int compaign_id = requests.getInt("compaign_id");
				long msisdn = requests.getLong("msisdn");
				String messageText = requests.getString("message");
				String sourceAddress = requests.getString("short_code");

				int transaction_id = transactionIdGenerator.getNewTransactionID();

				SmsData smsData = new SmsData(transaction_id,
						sourceAddress,
						"5",
						"0",
						msisdn,
						"1",
						"1",
						messageText,
						getMessageParts(messageText),
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

	private static int getMessageParts(String text){
		double nrOfChars = text.length();
		int codding = 1;

		try {
			byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
			int sizeInBytes = bytes.length;

			codding = nrOfChars == sizeInBytes ? 2 : 1;
		} catch(Exception e) {
			logger.warn("Bulk count tps error" + e);
		}

		double messageParts;

		if(codding == 1){
			 messageParts = (nrOfChars / 65d);
		}else{
			 messageParts = (nrOfChars / 134d);
		}

		int roundPartMax = (int) Math.ceil(messageParts);

		if(roundPartMax <= 0){
			roundPartMax = 1;
		}

		return roundPartMax;
	}

	public static void startBulkSendingWorker() {
		final Properties properties = reader.readParamFromFile();
		bulkSelectSize = Integer.parseInt(properties.getProperty("bulkSelectSize"));
		selectPeriodSec = Integer.parseInt(properties.getProperty("selectPeriodSec"));
		maxQueueSize = Integer.parseInt(properties.getProperty("maxQueueSize"));
		processCounters = new ConcurrentHashMap<>();
		bulkSendingService.scheduleAtFixedRate(bulkProcess, 0, selectPeriodSec, TimeUnit.SECONDS);
	}

	public static void stopBulkSendingWorker() {
		bulkSendingService.shutdown();
	}

}
