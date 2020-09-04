package com.unifun.workers;


import com.unifun.model.SmsData;
import com.unifun.services.ClientService;
import com.unifun.services.QueueService;
import com.unifun.services.SendService;
import com.unifun.utils.OutSpeedCounter;
import com.unifun.utils.TpsController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class SMPPClientSubmitSMWorker {
	private static final Logger logger = LogManager.getLogger(SMPPClientSubmitSMWorker.class);
	private  AtomicInteger tps = new AtomicInteger();
	private QueueService queueService =  QueueService.getInstance();
	private ClientService clientService = ClientService.getInstance();
	private int speed = 800;
	private ScheduledExecutorService queueWorker;
	private ExecutorService smsExecutor;
	private TpsController tpsController = new TpsController();



	public SMPPClientSubmitSMWorker() {
		queueWorker = Executors.newSingleThreadScheduledExecutor();
		smsExecutor = Executors.newFixedThreadPool(10);

		tpsController.setTps(speed);
	}
	public void start() {
		queueWorker.scheduleAtFixedRate(() -> {
			SmsData sms = null;

			int schedulerExec = tpsController.getSchedulerTimeExecutions();

			if(schedulerExec >= 20){
				tpsController.setSchedulerTimeExecutions(0);
				tpsController.setTps(speed);
			}

			tpsController.incrementSchedulerTimeExecutions(1);
			logger.info("getSchedulerTimeExecutions    " + tpsController.getSchedulerTimeExecutions());
			for (int i = 0; i < speed/20; i++) {
				if(tpsController.getTps() <= 0){
					logger.info("###BREAK ");
					break;

				}

				if (!queueService.isEmpty() && clientService.getSession() != null) {
					sms = queueService.getMessage();

					tpsController.decrementTps(sms.getQuantity());

					smsExecutor.execute(new SendService(sms, tpsController));
				}
			}
		}, 0, 50, TimeUnit.MILLISECONDS);
	}

	public AtomicInteger getTps() {
		return tps;
	}

}