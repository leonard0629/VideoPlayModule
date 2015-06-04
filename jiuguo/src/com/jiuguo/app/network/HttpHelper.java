package com.jiuguo.app.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpHelper {
	private static final String TAG = "HttpHelper";

	public static final int BUFFER_SIZE = 1024;

	/**
	 * post
	 * @param params
	 * @param url
	 * @param encode
	 * @return
	 */
	public static String post(Map<String, String> params, String url, String encode) {
		String responseBody = null;

		// handle params
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		try {
			// create entity
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,
					encode);
			// create httpPost
			HttpPost httpPost = new HttpPost(url);
			// set params
			httpPost.setEntity(entity);
//			Log.v(TAG, pairs.toString());
			// create httoClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			// get response
			HttpResponse response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				// response ok
				InputStream in = response.getEntity().getContent();
				responseBody = getResponseStr(in, encode);
				if (!(responseBody != null && !responseBody.equalsIgnoreCase(""))) {
					Log.e(TAG, "no response");
				}
			} else {
				Log.e(TAG, "server error, status code:" + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}

		return responseBody;
	}

	/**
	 * get
	 * @param url
	 * @param encode
	 * @return
	 */
	public static String get(String url, String encode) {
		String responseBody = null;
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		try {
			httpClient = new DefaultHttpClient();
			httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				// response ok
				InputStream in = response.getEntity().getContent();
				responseBody = getResponseStr(in, encode);
				if (!(responseBody != null && !responseBody.equalsIgnoreCase(""))) {
					Log.e(TAG, "no response");
				}
			} else {
				Log.e(TAG, "server error, status code:" + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}

		return responseBody;
	}

	/**
	 * head method to get the file size
	 * @param url
	 * @param encode
	 * @return
	 */
	public static long head(String url, String encode) {
		long fileSize = -1;

		HttpClient httpClient = new DefaultHttpClient();
		HttpHead httpHead = new HttpHead(url);
		try {
			HttpResponse response = httpClient.execute(httpHead);
			if (response.getStatusLine().getStatusCode() == 200) {
				Header header = response.getFirstHeader("Content-Length");
				fileSize = Long.parseLong(header.getValue());
			} else {
				Log.e(TAG, "server error, status code:" + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}

		return fileSize;
	}

	/**
	 * getImage
	 * @param url
	 * @param encode
	 * @return
	 */
	public static Bitmap getImage(String url, String encode) {
		Bitmap bitmap = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				// response ok
				InputStream in = response.getEntity().getContent();
				bitmap = BitmapFactory.decodeStream(in);
			} else {
				Log.e(TAG, "server error, status code:" + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * parse string from input stream
	 * 
	 * @param in
	 * @param encode
	 * @return
	 */
	public static String getResponseStr(InputStream in, String encode) {
		String result = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUFFER_SIZE];
		int len = 0;

		if (null != in) {
			try {
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				result = new String(out.toByteArray(), encode);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
		}

		return result;
	}
}
