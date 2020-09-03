package com.unifun;

import com.unifun.services.ClientService;
import com.unifun.workers.BulkAddQueueWorker;
import com.unifun.workers.SMPPClientSubmitSMWorker;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = null;
	public static void main(String[] args) {
		org.apache.log4j.PropertyConfigurator.configureAndWatch("log4j.properties", 10000);
		Main.logger = LogManager.getLogger(Main.class);
		System.out.println("Configured logger....");
		ClientService.getInstance().start();
		BulkAddQueueWorker.startBulkSendingWorker();
		SMPPClientSubmitSMWorker sendWorker = new SMPPClientSubmitSMWorker();
		sendWorker.start();

	}
}

