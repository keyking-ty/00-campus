package com.telit.info.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	public static String doPost(String url, Map<String, String> map, String charset) {
		//定义一个可关闭的httpClient的对象
		CloseableHttpClient httpClient = null;
		//定义httpPost对象
		HttpPost post = null;
		//返回结果
		String result = null;
		try {
			//1.创建httpClient的默认实例
			httpClient = HttpClients.createDefault();
			//2.提交post
			post = new HttpPost(url);
			//3.设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			//4.迭代参数
			Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				//获得参数
				Entry<String, String> element = iterator.next();
				list.add(new BasicNameValuePair(element.getKey(), element.getValue()));
			}
			// 5.编码
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
				post.setEntity(entity);
			}
			// 执行
			CloseableHttpResponse response = httpClient.execute(post);
			try {
				if (response != null) {
					HttpEntity httpEntity = response.getEntity();
					// 如果返回的内容不为空
					if (httpEntity != null) {
						result = EntityUtils.toString(httpEntity);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭资源
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static String doGet(String url,String charset) {
		//定义一个可关闭的httpClient的对象
		CloseableHttpClient httpClient = null;
		HttpGet get = null;
		//返回结果
		String result = null;
		try {
			//1.创建httpClient的默认实例
			httpClient = HttpClients.createDefault();
			get = new HttpGet(url);
			get.setHeader("Content-Type", "text/html; charset=" + charset);
			//执行
			CloseableHttpResponse response = httpClient.execute(get);
			try {
				if (response != null) {
					HttpEntity httpEntity = response.getEntity();
					//如果返回的内容不为空
					if (httpEntity != null) {
						result = EntityUtils.toString(httpEntity);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				//关闭资源
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static String doJsonPost(String path, String postContent) {
		URL url = null;
		try {
			url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");// 提交模式
			httpURLConnection.setConnectTimeout(10000);//连接超时 单位毫秒
			httpURLConnection.setReadTimeout(10000);//读取超时 单位毫秒
			// 发送POST请求必须设置如下两行
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
//			PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
//			printWriter.write(postContent);
//			printWriter.flush();
			httpURLConnection.connect();
			OutputStream os=httpURLConnection.getOutputStream();
			os.write(postContent.getBytes("UTF-8"));
			os.flush();
			StringBuilder sb = new StringBuilder();
			int httpRspCode = httpURLConnection.getResponseCode();
			if (httpRspCode == HttpURLConnection.HTTP_OK) {
				// 开始获取数据
				BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				return sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
}
