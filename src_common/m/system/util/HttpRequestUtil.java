package m.system.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.UUID;

public class HttpRequestUtil {

	private String charset = "UTF-8";
	private Integer connectTimeout = null;
	private Integer socketTimeout = null;
	private String proxyHost = null;
	private Integer proxyPort = null;

	/**
	 * Do GET request
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public String doGet(String url) throws Exception {
		URL localURL = new URL(url);
		URLConnection connection = openConnection(localURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
		InputStream inputStream = null;
		StringBuffer resultBuffer = new StringBuffer();
		if (httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK) {
			try {
				inputStream = httpURLConnection.getInputStream();
				BufferedReader bf=new BufferedReader(new InputStreamReader(inputStream,charset)); 
				String line="";
				while ((line=bf.readLine())!=null){
					resultBuffer.append(line).append("\r\n");
				}
			} finally {
				if(inputStream!=null) inputStream.close();
			}
		}
		if(resultBuffer.indexOf("\r\n")!=-1){
			return resultBuffer.substring(0, resultBuffer.lastIndexOf("\r\n"));
		}else{
			return resultBuffer.toString();
		}
	}

	public String doPost(String url, String param) throws Exception {
		return doPost(url,param,null);
	}
	/**
	 * Do POST request
	 * 
	 * @param url
	 * @param parameterMap
	 * @return
	 * @throws Exception
	 */
	public String doPost(String url, String param,Map<String,String> header) throws Exception {
		URL localURL = new URL(url);
		URLConnection connection = openConnection(localURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
		if(null!=header) {
			for(String key : header.keySet()) {
				httpURLConnection.setRequestProperty(key, header.get(key));
			}
		}
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setDoOutput(true);
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		InputStream inputStream = null;
		StringBuffer resultBuffer = new StringBuffer();
		try{
			outputStream = httpURLConnection.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(param);
			outputStreamWriter.flush();
			if (httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK) {
				try {
					inputStream = httpURLConnection.getInputStream();
					BufferedReader bf=new BufferedReader(new InputStreamReader(inputStream,charset)); 
					String line="";
					while ((line=bf.readLine())!=null){
						resultBuffer.append(line).append("\r\n");
					}
				} finally {
					if(inputStream!=null) inputStream.close();
				}
			}
		} finally{
			if(outputStreamWriter!=null) outputStreamWriter.close();
			if(outputStream!=null) outputStream.close();
		}
		if(resultBuffer.indexOf("\r\n")!=-1){
			return resultBuffer.substring(0, resultBuffer.lastIndexOf("\r\n"));
		}else{
			return resultBuffer.toString();
		}
	}
	public String doUpload(String url,File[] files) throws Exception {
		String boundary = UUID.randomUUID().toString();
		URL localURL = new URL(url);
		URLConnection connection = openConnection(localURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
		// 设置是否从httpUrlConnection读入，默认情况下是true;
		httpURLConnection.setDoInput(true);
		// 设置是否向httpUrlConnection输出
		httpURLConnection.setDoOutput(true);
		// Post 请求不能使用缓存
		httpURLConnection.setUseCaches(false);
		// 设定请求的方法，默认是GET
		httpURLConnection.setRequestMethod("POST");
		// 设置字符编码连接参数
		httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
		// 设置字符编码
		httpURLConnection.setRequestProperty("Charset", charset);
		// 设置请求内容类型
		httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

		DataOutputStream outputStream = null;
		InputStream inputStream = null;
		StringBuffer resultBuffer = new StringBuffer();
		try{
			outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				outputStream.writeBytes(new StringBuffer("--").append(boundary).append("\r\n").toString());
				outputStream.writeBytes(new StringBuffer("Content-Disposition: form-data; name=\"").append(file.getName()).append("\";filename=\"").append(file.getName()).append("\"\r\n\r\n").toString());
				FileInputStream fStream = new FileInputStream(file);
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int length = -1;
				while ((length = fStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.writeBytes("\r\n");
				fStream.close();
			}
			outputStream.writeBytes(new StringBuffer("--").append(boundary).append("--\r\n").toString());
			outputStream.flush();
			if (httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK) {
				try {
					inputStream = httpURLConnection.getInputStream();
					BufferedReader bf=new BufferedReader(new InputStreamReader(inputStream,charset)); 
					String line="";
					while ((line=bf.readLine())!=null){
						resultBuffer.append(line).append("\r\n");
					}
				} finally {
					if(inputStream!=null) inputStream.close();
				}
			}
		} finally{
			if(outputStream!=null) outputStream.close();
		}
		if(resultBuffer.indexOf("\r\n")!=-1){
			return resultBuffer.substring(0, resultBuffer.lastIndexOf("\r\n"));
		}else{
			return resultBuffer.toString();
		}
	}
	public String doJson(String url,String json) throws Exception{
		byte[] data = json.getBytes();
		URL localURL = new URL(url);
		URLConnection connection = openConnection(localURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setRequestProperty("Connection", "keep-alive");
		httpURLConnection.setRequestProperty("Charset", charset);
		httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
		httpURLConnection.setRequestProperty("Content-Type", "application/json");
		OutputStream outputStream = null;
		InputStream inputStream = null;
		StringBuffer resultBuffer = new StringBuffer();
		try{
			outputStream = httpURLConnection.getOutputStream();
			outputStream.write(data);
			outputStream.flush();
			if (httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK) {
				try {
					inputStream = httpURLConnection.getInputStream();
					BufferedReader bf=new BufferedReader(new InputStreamReader(inputStream,charset)); 
					String line="";
					while ((line=bf.readLine())!=null){
						resultBuffer.append(line);
					}
				} finally {
					if(inputStream!=null) inputStream.close();
				}
			}
		} finally{
			if(outputStream!=null) outputStream.close();
		}
		return resultBuffer.toString().trim();
	}

	private URLConnection openConnection(URL localURL) throws IOException {
		URLConnection connection;
		if (proxyHost != null && proxyPort != null) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					proxyHost, proxyPort));
			connection = localURL.openConnection(proxy);
		} else {
			connection = localURL.openConnection();
		}
		return connection;
	}

	/*
	 * Getter & Setter
	 */
	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public Integer getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
