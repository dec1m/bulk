package com.unifun.model;

public class DeliveryStatus {
	private int transactionId;
	private long remoteId;
	private String state;

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public long getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(int remoteId) {
		this.remoteId = remoteId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public DeliveryStatus(int transactionId, long remoteId, String state) {
		this.transactionId = transactionId;
		this.remoteId = remoteId;
		this.state = state;
	}

	public DeliveryStatus(int transactionId, long remoteId) {
		this.transactionId = transactionId;
		this.remoteId = remoteId;
	}

	public DeliveryStatus(long remoteId, String state) {
		this.remoteId = remoteId;
		this.state = state;
	}

	@Override
	public String toString() {
		return "DeliveryStatus{" +
				"transactionId=" + transactionId +
				", remoteId=" + remoteId +
				", state='" + state + '\'' +
				'}';
	}
}
