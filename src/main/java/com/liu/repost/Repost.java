package com.liu.repost;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;


public class Repost {
	private final static Logger logger = Logger.getLogger(Repost.class);
	private static PoolWorker[] threads;
	private static ConcurrentLinkedQueue<String> queue;
	private final static int THREADS_NUM = 3;
	private static final Repost INSTANCE = new Repost();
	
	private Repost() {
		queue = new ConcurrentLinkedQueue<String>();
		threads = new PoolWorker[THREADS_NUM];
		for (int i = 0; i < THREADS_NUM; i++) {
			threads[i] = new PoolWorker();
		}
		startToWork();
	}
	
	private void startToWork() {
			for (PoolWorker worker : threads)
				worker.start();
			logger.info("Repost begin to work.");
	}
	
	public static Repost getInstance() {
		return INSTANCE;
	}
	
	public void push(String message) {
		queue.add(message);
		queue.notifyAll();
	}
	
	private class PoolWorker extends Thread {
		public void run() {
			while(true) {
				if (!queue.isEmpty()) {
					String message = queue.poll();
				}
			}
		}
	}
}
