package com.liu.servlets;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class LogServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2594118187782198951L;
	private static final Logger log = Logger.getLogger(LogServlet.class);
	private static final Conf conf = Conf.getInstance();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(("yyyy-MM-dd"));
	private static LogFile logFile = null;
	private String date = null;
	private static final long LENGTH = 1024 * 1024;
	private static final int BUFFER_SIZE = 1024 * 1024;
	private static String path = null;
	private static String fileNamePrefix = null;
	private static AtomicLong count;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		path = config.getInitParameter("PATH");
		fileNamePrefix = config.getInitParameter("FILENAME");
		count = new AtomicLong(0);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
//		String normedLog = Parser.normLog(request.)
		StringBuffer returnStr = new StringBuffer();
		DataInputStream reader = null;
		String ip = "";
		count.incrementAndGet();
		if (count.get() % 1000 == 0) {
			log.info("Receive:" + count);
		}
		try {
			long time = System.currentTimeMillis();
			logFile = getLogFile(time);

			ip = getRemoteIp(request);
			response.setContentType("text/html;charset=GBK");
			returnStr.append(ip + "\t");
			reader = new DataInputStream(request.getInputStream());
			String os = conf.getDefaultAppOs();
			if (request.getHeader("APP_OS") != null) {
				os = request.getHeader("APP_OS");
			}

			byte[] buffer = new byte[BUFFER_SIZE];
			if (LENGTH < request.getContentLength()) {
				log.warn("The req's lenth:" + request.getContentLength() + "is out of range ");
				return;
			}
			ByteBuffer bb = ByteBuffer.allocate(request.getContentLength() * 2 + BUFFER_SIZE);
			int count = 0;
			int countSum = 0;
			while ((count = reader.read(buffer)) > 0) {
				bb.put(buffer);
				countSum = countSum + count;
			}
			byte[] contentsOnly = Arrays.copyOf(bb.array(), countSum);
			byte[] encoded = Base64.encodeBase64(contentsOnly);
			String result = new String(encoded, "US-ASCII");
			returnStr.append(result + "\t");
			returnStr.append(os);
			returnStr.append("\n");
			logFile.write(returnStr.toString());
		} catch (Throwable e) {
			log.error(ip + ":" + returnStr.toString() + " can not write to file ", e);
		} finally {
			if (reader != null) {
				reader.close();
			}

		}
	}
	
	private synchronized LogFile getLogFile(long time) throws IOException {
		String d = genNewDate(time);

		if (date == null) {
			date = d;
			logFile = LogFile.getLogger(fileNamePrefix, path, 10000,
					Integer.MAX_VALUE);
		} else {
			if (date.compareTo(d) < 0) {
				logFile.close();
				date = d;
				logFile = LogFile.getLogger(fileNamePrefix, path, 10000, Integer.MAX_VALUE);
			}
		}

		return logFile;
	}
	
	private static String genNewDate(long time) {
		return dateFormat.format(new Date(time));
	}

	public static String getRemoteIp(HttpServletRequest request) {
		String uip = request.getHeader("X-Forwarded-For");
		if (uip == null) {
			uip = request.getRemoteAddr();
		} else {
			String[] ips = uip.split(",");
			if (ips.length > 1) {
				uip = ips[ips.length - 1].trim();
			}
		}
		return uip;
	}
}
