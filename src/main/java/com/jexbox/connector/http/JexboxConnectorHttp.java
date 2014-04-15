package com.jexbox.connector.http;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jexbox.connector.JexboxConnector;

public interface JexboxConnectorHttp extends JexboxConnector{
	public void send(Throwable e, HttpServletRequest request);
	public void sendWithMeta(Throwable e, HttpServletRequest request, Map<String, Map<String, String>> metaD);
}
