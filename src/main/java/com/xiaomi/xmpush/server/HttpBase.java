package com.xiaomi.xmpush.server;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

public class HttpBase {
	protected static final String UTF8 = "UTF-8";
	protected static final int BACKOFF_INITIAL_DELAY = 1000;
	protected static final int MAX_BACKOFF_DELAY = 1024000;
	protected final Random random = new Random();
	protected final String security;

	public HttpBase(String security) {
		this.security = security;
	}

	protected static final Logger logger = Logger.getLogger(HttpBase.class.getName());

	protected HttpURLConnection doPost(String url, String body) throws IOException {
		return doPost(url, "application/x-www-form-urlencoded;charset=UTF-8", body);
	}

	protected HttpURLConnection doGet(String url, String parameter) throws IOException {
		return doGet(url, "application/x-www-form-urlencoded;charset=UTF-8", parameter);
	}

	protected HttpURLConnection doPost(String url, String contentType, String body) throws IOException {
		if ((url == null) || (body == null)) {
			throw new IllegalArgumentException("arguments cannot be null");
		}
		if (!url.startsWith("https://")) {
			logger.warn("URL does not use https: " + url);
		}
		logger.info("Sending post to " + url);
		logger.debug("post body: " + body);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = getConnection(url);
		conn.setConnectTimeout(20000);
		conn.setReadTimeout(20000);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setFixedLengthStreamingMode(bytes.length);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", contentType);
		conn.setRequestProperty("Authorization", "key=" + this.security);
		conn.setRequestProperty("X-PUSH-SDK-VERSION", "2015.04.23");
		prepareConnection(conn);
		OutputStream out = conn.getOutputStream();
		try {
			out.write(bytes);
		} finally {
			close(out);
		}
		return conn;
	}

	protected HttpURLConnection doGet(String url, String contentType, String parameter) throws IOException {
		if ((url == null) || (parameter == null)) {
			throw new IllegalArgumentException("arguments cannot be null");
		}
		if (!url.startsWith("https://")) {
			logger.warn("URL does not use https: " + url);
		}
		logger.info("Sending get to " + url);
		logger.debug("get parameter: " + parameter);
		String fullUrl = url + "?" + parameter;
		HttpURLConnection conn = getConnection(fullUrl);
		conn.setConnectTimeout(20000);
		conn.setReadTimeout(20000);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", contentType);
		conn.setRequestProperty("Authorization", "key=" + this.security);
		prepareConnection(conn);
		conn.getInputStream();
		return conn;
	}

	protected void prepareConnection(HttpURLConnection conn) {
	}

	protected static StringBuilder newBody(String name, String value) {
		return new StringBuilder((String) nonNull(name)).append('=').append((String) nonNull(value));
	}

	private static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				logger.debug("IOException closing stream", e);
			}
		}
	}

	protected static StringBuilder newBodyWithArrayParameters(String name, List<String> parameters) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parameters.size(); i++) {
			if (i == 0) {
				sb.append((String) nonNull(name)).append("=").append(URLEncoder.encode((String) nonNull(parameters.get(i)), "UTF-8"));
			} else {
				((StringBuilder) nonNull(sb)).append('&').append((String) nonNull(name)).append('=').append(URLEncoder.encode((String) nonNull(parameters.get(i)), "UTF-8"));
			}
		}
		if (parameters.size() == 0) {
			sb.append(name).append("=").append("");
		}
		return sb;
	}

	protected static void addParameter(StringBuilder body, String name, String value) {
		((StringBuilder) nonNull(body)).append('&').append((String) nonNull(name)).append('=').append((String) nonNull(value));
	}

	protected HttpURLConnection getConnection(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		return conn;
	}

	protected static String getString(InputStream stream) throws IOException {
		if (stream == null) {
			return "";
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder content = new StringBuilder();
		String newLine;
		do {
			newLine = reader.readLine();
			if (newLine != null) {
				content.append(newLine).append('\n');
			}
		} while (newLine != null);
		if (content.length() > 0) {
			content.setLength(content.length() - 1);
		}
		return content.toString();
	}

	protected static String getAndClose(InputStream stream) throws IOException {
		try {
			return getString(stream);
		} finally {
			if (stream != null) {
				close(stream);
			}
		}
	}

	static <T> T nonNull(T argument) {
		if (argument == null) {
			throw new IllegalArgumentException("argument cannot be null");
		}
		return argument;
	}

	void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}