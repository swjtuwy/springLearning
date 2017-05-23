/**
 *******************************************************************************
 *                       Continental Confidential
 *                  Copyright (c) Continental AG. 2017
 *
 *      This software is furnished under license and may be used or
 *      copied only in accordance with the terms of such license.
 *******************************************************************************
 * @file  HttpUtil.java
 * @brief HttpUtil interface
 *******************************************************************************
 */

package com.map.util;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.map.constant.Const.MessageConstants;
import com.map.constant.Const.ResponseCode;

@Component
public class HttpUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
//	private static final Marker tag = MarkerFactory.getMarker("common.httputil.convertMsg2Response");

	public Response doGet(String url, Map<String, String> header) {
		logger.debug("url = " + url);

		CloseableHttpResponse httpResponse = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);

		addHeader(httpGet, header);

		try {
			httpResponse = httpClient.execute(httpGet);
			return convertMsg2Response(httpResponse);
		} catch (Exception e) {
			// logger.error("HttpUtil doGet Exception " + e.toString());
			Response response = new Response();
			response.setCode(ResponseCode.INTERNAL_SERVER_ERROR);
			response.setData(MessageConstants.SERVER_UNSERVICE);
			return response;
		} finally {
			close(httpClient, httpResponse);
		}
	}

	public Response doPut(String url, String body, Map<String, String> header) {
//		logger.debug("doPut url = " + url + ", body = " + body);

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut(url);

		addHeader(httpPut, header);

		if (body != null) {
			StringEntity entity = new StringEntity(body, "UTF-8");
			httpPut.setEntity(entity);
		}

		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPut);
			return convertMsg2Response(httpResponse);
		} catch (Exception e) {
			System.err.println("HttpUtil doPut Exception " + e.toString());
			Response response = new Response();
			response.setCode(ResponseCode.INTERNAL_SERVER_ERROR);
			response.setData(MessageConstants.SERVER_UNSERVICE);
			return response;
		} finally {
			close(httpClient, httpResponse);
		}

	}

	public Response doPost(String url, String body, Map<String, String> header) {
		System.out.println("doPost url = " + url + ", body = " + body);

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = new StringEntity(body, "UTF-8");
		httpPost.setEntity(entity);

		addHeader(httpPost, header);

		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
			return convertMsg2Response(httpResponse);
		} catch (Exception e) {
			System.err.println("HttpUtil doPost Exception " + e.toString());
			Response response = new Response();
			response.setCode(ResponseCode.INTERNAL_SERVER_ERROR);
			response.setData(MessageConstants.SERVER_UNSERVICE);
			return response;
		} finally {
			close(httpClient, httpResponse);
		}
	}

	public Response doDelete(String url, Map<String, String> header) {
//		sys.debug("doDelete path = " + url);

		CloseableHttpResponse httpResponse = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpDelete httpDelete = new HttpDelete(url);
		addHeader(httpDelete, header);
		try {
			httpResponse = httpClient.execute(httpDelete);
			return convertMsg2Response(httpResponse);
		} catch (Exception e) {
			System.err.println("HttpUtil doDelete Exception " + e.toString());
			Response response = new Response();
			response.setCode(ResponseCode.INTERNAL_SERVER_ERROR);
			response.setData(MessageConstants.SERVER_UNSERVICE);
			return response;
		} finally {
			close(httpClient, httpResponse);
		}
	}


	public Response doGet(String url) {
		return doGet(url, null);
	}

	public Response doPut(String url, String body) {
		return doPut(url, body, null);
	}

	public Response doPost(String url, String body) {
		return doPost(url, body, null);
	}

	public Response doDelete(String url) {
		return doDelete(url, null);
	}

	private void addHeader(HttpMessage httpMessage, Map<String, String> headers) {
		if (headers == null)
			return;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			httpMessage.addHeader(entry.getKey(), entry.getValue());
//			logger.debug("Key = " + entry.getKey() + ", Value = "
//					+ entry.getValue());
		}
	}

	private void close(CloseableHttpClient httpClient,
			CloseableHttpResponse httpResponse) {
		if (null != httpResponse) {
			try {
				httpResponse.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (null != httpClient) {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Response convertMsg2Response(HttpResponse httpResponse) {
//		logger.debug("convertMsg2Response");
		Response response = new Response();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
//		logger.debug("statusCode = " + statusCode);
		response.setCode(HttpStatus.valueOf(statusCode));

		String result = "";
		try {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				result = EntityUtils.toString(httpEntity, "UTF-8");
			}
		} catch (ParseException e) {
			System.err.println(e.toString());
		} catch (IOException e) {
			System.err.println(e.toString());
		}

		response.setData(result);
		return response;
	}
}
