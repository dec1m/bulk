package com.unifun.utils;

import com.unifun.db.DBlayer;

import java.util.concurrent.atomic.AtomicInteger;

public class TransactionIdGenerator {
	private static TransactionIdGenerator instance;
	private static AtomicInteger transactionIdGenerator = new AtomicInteger();

	private TransactionIdGenerator() {
	}

	public int getNewTransactionID() {
		int transactionID = transactionIdGenerator.incrementAndGet();
		if (transactionID == 2147000000) {
			transactionIdGenerator.set(1);
		}
		return transactionID;
	}

	public static void initBeginTransactionID() {
		transactionIdGenerator = new AtomicInteger(DBlayer.getInstance().getMaxTransactionID() + 10000);
	}


	public static synchronized TransactionIdGenerator getInstance() {
		if (instance == null) {
			instance = new TransactionIdGenerator();
		}
		return instance;
	}
}
