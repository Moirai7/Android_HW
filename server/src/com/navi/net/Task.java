package com.navi.net;

public abstract class Task implements Runnable {
	private long taskId;
	 protected abstract boolean needExecuteImmediate();
	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	@Override
	public void run() {
		System.out.println("run");
	}
	
}