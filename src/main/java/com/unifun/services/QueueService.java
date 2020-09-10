package com.unifun.services;

import com.unifun.model.SmsData;
import com.unifun.utils.PropertyReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService {
	private static QueueService instance;
	private ConcurrentLinkedQueue<SmsData> smsQueue = new ConcurrentLinkedQueue<>();
	private PropertyReader propertyReader = new PropertyReader();
	private static final Logger logger = LogManager.getLogger(QueueService.class);
	private String regExp;

	public boolean addToQueue(SmsData smppMessage) {
		if (smppMessage.getMessage().isEmpty()) {
			return false;
		}
		if (smppMessage.getTransactionId() <= 0) {
			return false;
		}

		if (!smppMessage.getToAD().toString().matches(regExp)) {
			return false;
		}
		if(smppMessage.getFromAD().isEmpty()){
			return false;
		}

		return smsQueue.add(smppMessage);
	}

	private QueueService() {
		final Properties properties = propertyReader.readParamFromFile();
		regExp = properties.getProperty("regExp");
	}

	public SmsData getMessage() {
		return smsQueue.poll();
	}

	public boolean isEmpty() {
		return smsQueue.isEmpty();
	}

	public int queueSize() {
		return smsQueue.size();
	}

	public static synchronized QueueService getInstance() {
		if (instance == null) {
			instance = new QueueService();
		}
		return instance;
	}
}

