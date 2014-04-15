package com.jexbox.connector.http;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jexbox.connector.JexboxConnectorImpl;
import com.jexbox.connector.TransportException;

public class JexboxConnectorHttpImpl extends JexboxConnectorImpl implements JexboxConnectorHttp{
    private static Logger _logger = Logger.getLogger(JexboxConnectorHttpImpl.class.getName());
    
	public JexboxConnectorHttpImpl(Properties props) {
		super(props);
	}
	
	/*
	 * Entry point for sending errors to Jexbox.
	 * Depending from configuration, can send error instantly in current thread or use background notifier to put errors in queue
	 */
	public void send(Throwable e, HttpServletRequest request){
		sendWithMeta(e, request, null);
	}
	
	public void sendWithMeta(Throwable e, HttpServletRequest request, Map<String, Map<String, String>> metaD){
		try {
			JsonObject json = json(e, metaD);
			addRequestMetaData(request, json);
			
			HttpSession session = request.getSession(false);
			addSessionMetaData(session, json);
			_notifier.send(json);
		} catch (UnsupportedEncodingException e1) {
			_logger.log(Level.SEVERE, "Could not able to send error to Jexbox", e1);
		} catch (TransportException e1) {
			_logger.log(Level.SEVERE, "Could not able to send error to Jexbox", e1);
		}
	}
	
	protected void addSessionMetaData(HttpSession session, JsonObject json){
		if(session != null){
			JsonObject meta = json.getAsJsonObject("meta");
			if(meta == null){
				meta = new JsonObject();
				json.add("meta", meta);
			}
			
			JsonObject sessionD = new JsonObject();
			meta.add("Session", sessionD);
			
			@SuppressWarnings("unchecked")
			Enumeration<String> names = session.getAttributeNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				Object attr = session.getAttribute(name);
				sessionD.add(name, new JsonPrimitive(String.valueOf(attr)));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void addRequestMetaData(HttpServletRequest reqHTTP, JsonObject json){
			JsonObject meta = json.getAsJsonObject("meta");
			if(meta == null){
				meta = new JsonObject();
				json.add("meta", meta);
			}
			
			json.add("uri",  new JsonPrimitive(reqHTTP.getRequestURL().toString()));
			
			
			JsonObject reqAttr = new JsonObject();
			meta.add("Request Attr", reqAttr);
			Enumeration<String> names = reqHTTP.getAttributeNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				Object attr = reqHTTP.getAttribute(name);
				if(attr != null){
					reqAttr.add(name, new JsonPrimitive(String.valueOf(attr)));
				}else{
					reqAttr.add(name, new JsonPrimitive("null"));
				}
			}
			
			JsonObject reqPara = new JsonObject();
			meta.add("Request Params", reqPara);
			names = reqHTTP.getParameterNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				String attr = reqHTTP.getParameter(name);
				if(attr != null){
					reqPara.add(name, new JsonPrimitive(String.valueOf(attr)));
				}else{
					reqPara.add(name, new JsonPrimitive("null"));
				}
			}
			
			JsonObject reqHead = new JsonObject();
			meta.add("Request Headers", reqHead);
			names = reqHTTP.getHeaderNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				String attr = reqHTTP.getHeader(name);
				if(attr != null){
					reqHead.add(name, new JsonPrimitive(String.valueOf(attr)));
				}else{
					reqHead.add(name, new JsonPrimitive("null"));
				}
			}
			
			
			JsonObject req = new JsonObject();
			meta.add("Request", req);
			
			req.add("Auth Type", new JsonPrimitive(String.valueOf(reqHTTP.getAuthType())));
			req.add("Character Encoding", new JsonPrimitive(String.valueOf(reqHTTP.getCharacterEncoding())));
			req.add("Content Type", new JsonPrimitive(String.valueOf(reqHTTP.getContentType())));
			req.add("Context Path", new JsonPrimitive(String.valueOf(reqHTTP.getContextPath())));
			req.add("Local Addr", new JsonPrimitive(String.valueOf(reqHTTP.getLocalAddr())));
			req.add("Local Name", new JsonPrimitive(String.valueOf(reqHTTP.getLocalName())));
			req.add("Method", new JsonPrimitive(String.valueOf(reqHTTP.getMethod())));
			req.add("Path Info", new JsonPrimitive(String.valueOf(reqHTTP.getPathInfo())));
			req.add("Path Translated", new JsonPrimitive(String.valueOf(reqHTTP.getPathTranslated())));
			req.add("Protocol", new JsonPrimitive(String.valueOf(reqHTTP.getProtocol())));
			req.add("Query String", new JsonPrimitive(String.valueOf(reqHTTP.getQueryString())));
			req.add("Remote Addr", new JsonPrimitive(String.valueOf(reqHTTP.getRemoteAddr())));
			req.add("Remote Host", new JsonPrimitive(String.valueOf(reqHTTP.getRemoteHost())));
			req.add("Remote User", new JsonPrimitive(String.valueOf(reqHTTP.getRemoteUser())));
			req.add("Requested Session Id", new JsonPrimitive(String.valueOf(reqHTTP.getRequestedSessionId())));
			req.add("Request URI", new JsonPrimitive(String.valueOf(reqHTTP.getRequestURI())));
			req.add("Scheme", new JsonPrimitive(String.valueOf(reqHTTP.getScheme())));
			req.add("Server Name", new JsonPrimitive(String.valueOf(reqHTTP.getServerName())));
			req.add("Servlet Path", new JsonPrimitive(String.valueOf(reqHTTP.getServletPath())));
			req.add("Content Length", new JsonPrimitive(String.valueOf(reqHTTP.getContentLength())));
			
	}
	
}
