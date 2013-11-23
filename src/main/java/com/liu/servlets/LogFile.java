package com.liu.servlets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * <p>
 * the class used to log the query.
 * </p>
 * 
 * @author qiangqi
 * @since 2007-7-11
 * @version 1.0
 */
public class LogFile {
	private static final Logger logger = Logger.getLogger(LogFile.class);
	// private static final org.slf4j.Logger logger =
	// LoggerFactory.getLogger(LogFile.class);

	private static final long DAY = 24 * 60 * 60 * 1000;

	private long startTime = 0l;

	private long current = 0l;

	// time interval for which write thread sleep when write queue is empty
	private static long SLEEPTIME = 10;

	private BufferedWriter m_writer = null;

	// recent number of records in the log file
	private int m_count = 0;

	// number of records written to harddisk eachTime
	private int m_writeNum_eachTime = 10000;

	// capacity of each logfile
	private int m_maxLogCount = 100000;

	private int m_queueLen = 10000000;

	// name of the log file
	private String m_fileName = null;

	private boolean m_close = false;

	private PriorityBlockingQueue<String> m_writeQueue = new PriorityBlockingQueue<String>();

	private Thread m_writeThread = null;

	// number of neglected records
	private long m_neglectCount = 0;

	// number of records which have been written in log files
	private long m_writeCount = 0;

	// number of log files
	private long m_fileCount = 1;

	// path where the log file will be stored
	private String m_path = null;

	// name of previous log file
	private String m_preFileName = null;

	// the time where this LogFile were born
	private Date m_birthTime = new Date();

	static private SimpleDateFormat dateFormat = new SimpleDateFormat(("yyyy-MM-dd"));

	// static private SimpleDateFormat dateFormat = new SimpleDateFormat(
	// ("yyyy-MM-dd_HH-mm-ss"));

	public String getConfigStr() {
		return "��־Ŀ¼��" + m_path + ", ��־���г���:" + m_queueLen + ", ÿ��д���¼��:"
				+ m_writeNum_eachTime + ", ÿ����־�ļ�����¼��:" + m_maxLogCount;
	}

	public long getFileCount() {
		return m_fileCount;
	}

	public long getWriteCount() {
		return m_writeCount;
	}

	public long getNeglectCount() {
		return m_neglectCount;
	}

	private synchronized void init(long time) throws IOException {
		if (m_writer == null) {
			Calendar calendar = Calendar.getInstance();
			current = time;
			calendar.setTimeInMillis(time);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			startTime = calendar.getTimeInMillis();
			String fileName = m_fileName + genNewDate(current);
			m_writer = new BufferedWriter(new FileWriter(fileName, true));
		}

	}

	public void close() throws IOException {
		m_close = true;
		if (m_writeThread == null) {
			return;
		}
		try {
			m_writeThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		m_writer.close();
		logger.info("total " + getFileCount() + " log files, write "
				+ getWriteCount() + " records, neglect " + getNeglectCount()
				+ " records");
	}



	private class WriteThread extends Thread {
		private List<String> logList = new ArrayList<String>(
				m_writeNum_eachTime);

		public void run() {
			while (!m_close) {
				logList.clear();
				m_writeQueue.drainTo(logList, m_writeNum_eachTime);
				if (logList.size() <= 0) {
					try {
						if (m_writer != null) {
							m_writer.flush();
						}
						sleep(SLEEPTIME);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				} else {
					// Collections.sort(logList);
					try {
						if (m_writer == null) {
							// init(logList.get(0).getTime());
							init(System.currentTimeMillis());
						}
						check();
					} catch (IOException e1) {
						logger.error("check error !", e1);
					}
					for (String log : logList) {
						try {
							realWrite(log);
						} catch (IOException e) {
							m_close = true;
							logger.error(e.getMessage()
									+ " :close logfile service");
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * ��ʱ�䳬��
	 * 
	 * @param value
	 * @return
	 */
	private boolean validateTime(long value) {
		long newStart = startTime + DAY;
		if (value >= newStart || value < startTime) {
			return false;
		}
		return true;
	}

	private boolean needCheckAll(long min, long max) {
		if (min >= startTime && max <= (startTime + DAY)) {
			return false;
		}
		return true;
	}

	private LogFile(String path, String fileName, int writeNum_eachTime,
			int maxLogCount) throws IOException {
		setMaxLogCount(maxLogCount);
		setWriteNumET(writeNum_eachTime);
		if (!new File(path).exists()) {
			new File(path).mkdir();
		}
		m_path = path;
		m_fileName = path + "/" + fileName;
		m_writeThread = new WriteThread();
		m_writeThread.start();
	}

	public void setWriteNumET(int writeNum_eachTime) {
		if (m_writeNum_eachTime <= 0) {
			throw new IllegalArgumentException("illegal m_writeNum_eachTime:"
					+ m_writeNum_eachTime);
		}
		m_writeNum_eachTime = writeNum_eachTime;
	}

	public int getWriteNumET() {
		return m_writeNum_eachTime;
	}

	public int getQueueLen() {
		return m_queueLen;
	}

	public void setMaxLogCount(int maxCount) {
		if (maxCount <= 0) {
			throw new IllegalArgumentException("illegal maxCount:" + maxCount);
		}
		m_maxLogCount = maxCount;
	}

	public int getMaxLogCount() {
		return m_maxLogCount;
	}

	static private String genNewDate(long time) {
		// return '-' + new Date().toLocaleString().replace(':', '-');
		return '-' + dateFormat.format(new Date(time));
	}

	private void realWrite(String log) throws IOException {
		m_writer.write(log.trim());
		m_writer.newLine();
		m_writeCount++;
		m_count++;

	}

	private void initStartTime(long time) {
		Calendar calendar = Calendar.getInstance();
		current = time;
		calendar.setTimeInMillis(time);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		startTime = calendar.getTimeInMillis();
	}

	private void checkDayTime(long time) throws IOException {
		if (!validateTime(time)) {
			initStartTime(time);
			m_writer.close();
			String fileName = m_fileName + genNewDate(current);
			if (fileName.equals(m_preFileName)) {
				fileName += "_" + m_fileCount;
			} else {
				m_preFileName = fileName;
			}
			m_writer = new BufferedWriter(new FileWriter(fileName, true));
			m_fileCount++;
			m_count = 0;
			m_birthTime = new Date();
		}
	}

	private void check() throws IOException {
		if (m_count >= m_maxLogCount) {
			current += (System.currentTimeMillis() - m_birthTime.getTime());
			if (current >= (startTime + DAY)) {
				startTime += DAY;
			}
			m_writer.close();
			String fileName = m_fileName + genNewDate(current);
			if (fileName.equals(m_preFileName)) {
				fileName += "_" + m_fileCount;
			} else {
				m_preFileName = fileName;
			}
			m_writer = new BufferedWriter(new FileWriter(fileName, true));
			m_fileCount++;
			m_count = 0;
			m_birthTime = new Date();
		}
	}

	static public LogFile getLogger(String filenamePrefix, String path,
			int writeNum_eachTime, int maxLogCount) throws IOException {
		return new LogFile(path, filenamePrefix, writeNum_eachTime, maxLogCount);
	}

	static int getRandomInt(int min, int max) {
		return min + new Random(System.currentTimeMillis()).nextInt(max - min);
	}

	/**
	 * add the content to the write queue
	 * 
	 * @param content
	 *            which will be added to write queue
	 * @return true if adding successfully, false if else
	 * **/

	public boolean write(String log) {
		if (log == null || StringUtils.isBlank(log)) {
			throw new NullPointerException("content can not be null");
		}
		if (m_writeQueue.size() >= m_maxLogCount) {
			try {
				m_writeThread.notifyAll();
			} catch (Exception e) {
			}
		}
		try {
			return m_writeQueue.add(log);
		} catch (Exception e) {
			logger.error("write to file exception !", e);
			return false;
		}

	}

}
