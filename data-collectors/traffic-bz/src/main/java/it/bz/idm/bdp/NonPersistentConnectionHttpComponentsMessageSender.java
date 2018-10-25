package it.bz.idm.bdp;

import java.io.IOException;
import java.net.URI;

import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.http.HttpComponentsConnection;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

public class NonPersistentConnectionHttpComponentsMessageSender extends HttpComponentsMessageSender{
	public WebServiceConnection createConnection(URI uri) throws IOException {
		HttpComponentsConnection httpComponentsConnection = (HttpComponentsConnection)super.createConnection(uri);
		httpComponentsConnection.getHttpPost().addHeader("Connection","close");
		return httpComponentsConnection;
		}
}
