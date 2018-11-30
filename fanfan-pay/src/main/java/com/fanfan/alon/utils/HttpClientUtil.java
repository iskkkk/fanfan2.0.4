package com.fanfan.alon.utils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述:Http客户端工具类
 * @param:
 * @return:
 * @auther: zoujiulong
 * @date: 2018/9/3   18:19
 */
public class HttpClientUtil {
	
	public static final String SunX509 = "SunX509";
	public static final String JKS = "JKS";
	public static final String PKCS12 = "PKCS12";
	public static final String TLS = "TLS";
	
	/**
	 * 功能描述:get HttpsURLConnection
	 * @param: strUrl url地址
	 * @return: HttpsURLConnection
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:20
	 */
	public static HttpsURLConnection getHttpsURLConnection(String strUrl)
			throws IOException {
		URL url = new URL(strUrl);
		HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url
				.openConnection();
		return httpsURLConnection;
	}
	
	/**
	 * 功能描述:获取不带查询串的url
	 * @param: strUrl
	 * @return: String
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:20
	 */
	public static String getURL(String strUrl) {

		if(null != strUrl) {
			int indexOf = strUrl.indexOf("?");
			if(-1 != indexOf) {
				return strUrl.substring(0, indexOf);
			} 
			
			return strUrl;
		}
		
		return strUrl;
		
	}
	
	/**
	 * 功能描述:获取查询串
	 * @param:
	 * @return:
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:20
	 */
	public static String getQueryString(String strUrl) {
		
		if(null != strUrl) {
			int indexOf = strUrl.indexOf("?");
			if(-1 != indexOf) {
				return strUrl.substring(indexOf+1, strUrl.length());
			} 
			
			return "";
		}
		
		return strUrl;
	}
	
	/**
	 * 功能描述:查询字符串转换成Map name1=key1&name2=key2&...
	 * @param:
	 * @return:
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:21
	 */
	public static Map queryString2Map(String queryString) {
		if(null == queryString || "".equals(queryString)) {
			return null;
		}
		
		Map m = new HashMap();
		String[] strArray = queryString.split("&");
		for(int index = 0; index < strArray.length; index++) {
			String pair = strArray[index];
			HttpClientUtil.putMapByPair(pair, m);
		}
		
		return m;
		
	}
	
	/**
	 * 功能描述:把键值添加至Map pair:name=value
	 * @param: pair name=value
	 * @return:
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:22
	 */
	public static void putMapByPair(String pair, Map m) {
		
		if(null == pair || "".equals(pair)) {
			return;
		}
		
		int indexOf = pair.indexOf("=");
		if(-1 != indexOf) {
			String k = pair.substring(0, indexOf);
			String v = pair.substring(indexOf+1, pair.length());
			if(null != k && !"".equals(k)) {
				m.put(k, v);
			}
		} else {
			m.put(pair, "");
		}
	}
	
	/**
	 * 功能描述:BufferedReader转换成String 注意:流关闭需要自行处理
	 * @param:
	 * @return:
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:22
	 */
	public static String bufferedReader2String(BufferedReader reader) throws IOException {
		StringBuffer buf = new StringBuffer();
		String line = null;
		while( (line = reader.readLine()) != null) {
			buf.append(line);
			buf.append("\r\n");
		}
				
		return buf.toString();
	}
	
	/**
	 * 功能描述:处理输出 注意:流关闭需要自行处理
	 * @param:
	 * @return:
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:23
	 */
	public static void doOutput(OutputStream out, byte[] data, int len) 
			throws IOException {
		int dataLen = data.length;
		int off = 0;
		while(off < dataLen) {
			if(len >= dataLen) {
				out.write(data, off, dataLen);
			} else {
				out.write(data, off, len);
			}
			
			//刷新缓冲区
			out.flush();
			
			off += len;
			
			dataLen -= len;
		}
		
	}
	
	/**
	 * 功能描述:获取SSLContext
	 * @param:
	 * @return:
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:23
	 */
	public static SSLContext getSSLContext(
			FileInputStream trustFileInputStream, String trustPasswd,
			FileInputStream keyFileInputStream, String keyPasswd)
			throws NoSuchAlgorithmException, KeyStoreException,
			CertificateException, IOException, UnrecoverableKeyException,
			KeyManagementException {

		// ca
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(HttpClientUtil.SunX509);
		KeyStore trustKeyStore = KeyStore.getInstance(HttpClientUtil.JKS);
		trustKeyStore.load(trustFileInputStream, HttpClientUtil
				.str2CharArray(trustPasswd));
		tmf.init(trustKeyStore);

		final char[] kp = HttpClientUtil.str2CharArray(keyPasswd);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(HttpClientUtil.SunX509);
		KeyStore ks = KeyStore.getInstance(HttpClientUtil.PKCS12);
		ks.load(keyFileInputStream, kp);
		kmf.init(ks, kp);

		SecureRandom rand = new SecureRandom();
		SSLContext ctx = SSLContext.getInstance(HttpClientUtil.TLS);
		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), rand);

		return ctx;
	}
	
	/**
	 * 功能描述:获取CA证书信息
	 * @param: cafile CA证书文件
	 * @return:
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:23
	 */
	public static Certificate getCertificate(File cafile)
			throws CertificateException, IOException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		FileInputStream in = new FileInputStream(cafile);
		Certificate cert = cf.generateCertificate(in);
		in.close();
		return cert;
	}
	
	/**
	 * 功能描述:字符串转换成char数组
	 * @param:
	 * @return:
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:23
	 */
	public static char[] str2CharArray(String str) {
		if(null == str) return null;
		
		return str.toCharArray();
	}
	
	/**
	 * 功能描述:存储ca证书成JKS格式
	 * @param:
	 * @return:
	 * @auther: zoujiulong
	 * @date: 2018/9/3   18:24
	 */
	public static void storeCACert(Certificate cert, String alias,
			String password, OutputStream out) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore ks = KeyStore.getInstance("JKS");

		ks.load(null, null);

		ks.setCertificateEntry(alias, cert);

		// store keystore
		ks.store(out, HttpClientUtil.str2CharArray(password));

	}
	
	public static InputStream String2Inputstream(String str) {
		return new ByteArrayInputStream(str.getBytes());
	}

}

