package com.unifun.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class TransactionIdGenerator {
	private AtomicInteger transactionIdGenerator = new AtomicInteger();
	public int getNewTransactionID() {
		int transactionID = transactionIdGenerator.incrementAndGet();
		if (transactionID == 2147000000) {
			transactionIdGenerator.set(1);
		}
		return transactionID;
	}

}
