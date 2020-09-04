package com.unifun.utils;

public class TpsController {

	private int schedulerTimeExecutions = 0;

	private int tps = 0;

	private int maxThread = 0;

	public synchronized int getTps() {
		return tps;
	}

	public synchronized void incrementTps(int tps) {
		this.tps += tps;
	}

	public synchronized void decrementTps(int tps) {
		this.tps -= tps;
	}

	public synchronized int getMaxThread() {
		return maxThread;
	}

	public synchronized void incrementMaxThreads(int mps) {
		this.maxThread += mps;
	}

	public synchronized void decrementMaxThreads(int mps) {
		this.maxThread -= mps;
	}

	public void setMaxThread(int maxThread) {
		this.maxThread = maxThread;
	}

	public int getSchedulerTimeExecutions() {
		return schedulerTimeExecutions;
	}

	public void setSchedulerTimeExecutions(int schedulerTimeExecutions) {
		this.schedulerTimeExecutions = schedulerTimeExecutions;
	}
	public void incrementSchedulerTimeExecutions(int schedulerTimeExecutions) {
		this.schedulerTimeExecutions += schedulerTimeExecutions;
	}

	public void setTps(int tps) {
		this.tps = tps;
	}
}
