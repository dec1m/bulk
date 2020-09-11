package com.unifun.workers;


import com.unifun.db.DBlayer;
import com.unifun.model.SmsData;
import com.unifun.services.ClientService;
import com.unifun.services.QueueService;
import com.unifun.services.SendService;
import com.unifun.utils.InSpeedCounter;
import com.unifun.utils.PropertyReader;
import com.unifun.utils.TpsController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class SMPPClientSubmitSMWorker {
	private static final Logger logger = LogManager.getLogger(SMPPClientSubmitSMWorker.class);
	private AtomicInteger tps = new AtomicInteger();
	private QueueService queueService = QueueService.getInstance();
	private ClientService clientService = ClientService.getInstance();
	private int speed;
	private int poolSize;
	private ScheduledExecutorService queueWorker;
	private ExecutorService smsExecutor;
	private TpsController tpsController = new TpsController();
	private PropertyReader reader = new PropertyReader();
	private static final int SRI_PACKET = 1;
	private DBlayer dBlayer = DBlayer.getInstance();
	public SMPPClientSubmitSMWorker() {
		final Properties properties = reader.readParamFromFile();
		poolSize = Integer.parseInt(properties.getProperty("threadPoolSize"));
		speed = Integer.parseInt(properties.getProperty("tpsToSend"));

		queueWorker = Executors.newSingleThreadScheduledExecutor();
		smsExecutor = Executors.newFixedThreadPool(poolSize);

		tpsController.setTps(speed);
	}

	public void start() {
		queueWorker.scheduleAtFixedRate(() -> {
			SmsData sms;

			int schedulerExec = tpsController.getSchedulerTimeExecutions();

			if (schedulerExec >= 20) {
				tpsController.setSchedulerTimeExecutions(0);
				tpsController.setTps(speed);
			}

			tpsController.incrementSchedulerTimeExecutions(1);
			for (int i = 0; i < speed / 20; i++) {
				if (tpsController.getTps() <= 0) {
					break;
				}

				if (!queueService.isEmpty() && clientService.getSession() != null) {
					sms = queueService.getMessage();
					// Message parts + SRI, SRI is one packet
					tpsController.decrementTps(sms.getQuantity() + SRI_PACKET);
					//dBlayer.addTransactionIdForUpdateSendDate(sms.getTransactionId());
					InSpeedCounter.getInstance().increment(sms.getQuantity());
					smsExecutor.execute(new SendService(sms));
				}
			}
		}, 0, 50, TimeUnit.MILLISECONDS);
	}

	public AtomicInteger getTps() {
		return tps;
	}

}