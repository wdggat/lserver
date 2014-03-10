package com.liu.msg;

public abstract class Message {
	protected final long time = System.currentTimeMillis();

	public abstract void send();
}
