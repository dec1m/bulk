package com.unifun.workers;


import com.unifun.model.SmsData;
import com.unifun.services.ClientService;
import com.unifun.services.QueueService;
import com.unifun.services.SendService;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class SMPPClientSubmitSMWorker {
	private  AtomicInteger tpsController = new AtomicInteger();
	private QueueService queueService =  QueueService.getInstance();
	private ClientService clientService = ClientService.getInstance();
	private int speed = 100;
	private ScheduledExecutorService queueWorker;
	private ExecutorService smsExecutor;

	public SMPPClientSubmitSMWorker() {
		queueWorker = Executors.newSingleThreadScheduledExecutor();
		smsExecutor = Executors.newFixedThreadPool(10);
		tpsController.set(speed);
	}
	public void start() {
		queueWorker.scheduleAtFixedRate(() -> {
			SmsData sms = null;
			for (int i = 0; i < speed/20; i++) {
				if(tpsController.get() <= 0){
					break;
				}

				if (!queueService.isEmpty() && clientService.getSession() != null) {
					sms = queueService.getMessage();
					tpsController.decrementAndGet();
					smsExecutor.execute(new SendService(sms, tpsController));
				}
			}
		}, 0, 50, TimeUnit.MILLISECONDS);
	}

	public AtomicInteger getTpsController() {
		return tpsController;
	}

}