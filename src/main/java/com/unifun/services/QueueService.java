package com.unifun.services;

import com.unifun.model.SmsData;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService {
	private static QueueService instance;
	private ConcurrentLinkedQueue<SmsData> smsQueue = new ConcurrentLinkedQueue<>();

	public boolean addToQueue(SmsData smppMessage){
		return  smsQueue.add(smppMessage);
	}

	private QueueService() {
	}

	public SmsData getMessage(){
		return smsQueue.poll();
	}
	public boolean isEmpty(){
		return smsQueue.isEmpty();
	}
	public int queueSize(){
		return smsQueue.size();
	}

	public static synchronized QueueService getInstance() {
			if(instance == null) {
				instance = new QueueService();
			}
			return instance;
		}
	}

