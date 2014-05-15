/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2014  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.messaging.http;

import com.aoindustries.io.AoByteArrayOutputStream;
import com.aoindustries.messaging.AbstractSocketContext;
import com.aoindustries.security.Identifier;
import com.aoindustries.util.concurrent.Callback;
import com.aoindustries.util.concurrent.ExecutorService;
import com.aoindustries.xml.XmlUtils;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.w3c.dom.Element;

/**
 * Client component for bi-directional messaging over TCP.
 */
public class HttpSocketClient extends AbstractSocketContext<HttpSocket> {

	private static final boolean DEBUG = false;

	private static final int CONNECT_TIMEOUT = 15 * 1000;

	private final ExecutorService executor = ExecutorService.newInstance();

	public HttpSocketClient() {
	}

	@Override
	public void close() {
		try {
			super.close();
		} finally {
			executor.dispose();
		}
	}

	/**
	 * Asynchronously connects.
	 */
	public void connect(
		final String endpoint,
		final Callback<? super HttpSocket> onConnect,
		final Callback<? super Exception> onError
	) {
		executor.submitUnbounded(
			new Runnable() {
				@Override
				public void run() {
					try {
						// Build request bytes
						AoByteArrayOutputStream bout = new AoByteArrayOutputStream();
						try {
							DataOutputStream out = new DataOutputStream(bout);
							try {
								out.writeBytes("action=connect");
							} finally {
								out.close();
							}
						} finally {
							bout.close();
						}
						long connectTime = System.currentTimeMillis();
						URL endpointURL = new URL(endpoint);
						HttpURLConnection conn = (HttpURLConnection)endpointURL.openConnection();
						conn.setAllowUserInteraction(false);
						conn.setConnectTimeout(CONNECT_TIMEOUT);
						conn.setDoOutput(true);
						conn.setFixedLengthStreamingMode(bout.size());
						conn.setInstanceFollowRedirects(false);
						conn.setReadTimeout(CONNECT_TIMEOUT);
						conn.setRequestMethod("POST");
						conn.setUseCaches(false);
						// Write request
						OutputStream out = conn.getOutputStream();
						try {
							out.write(bout.getInternalByteArray(), 0, bout.size());
							out.flush();
						} finally {
							out.close();
						}
						// Get response
						int responseCode = conn.getResponseCode();
						if(responseCode != 200) throw new IOException("Unexpect response code: " + responseCode);
						if(DEBUG) System.out.println("DEBUG: HttpSocketClient: connect: got connection");
						Element document = XmlUtils.parseXml(conn.getInputStream()).getDocumentElement();
						if(!"connection".equals(document.getNodeName())) throw new IOException("Unexpected root node name: " + document.getNodeName());
						Identifier id = Identifier.valueOf(document.getAttribute("id"));
						if(DEBUG) System.out.println("DEBUG: HttpSocketClient: connect: got id=" + id);
						HttpSocket httpSocket = new HttpSocket(
							HttpSocketClient.this,
							id,
							connectTime,
							endpointURL
						);
						if(DEBUG) System.out.println("DEBUG: HttpSocketClient: connect: adding socket");
						addSocket(httpSocket);
						if(onConnect!=null) {
							if(DEBUG) System.out.println("DEBUG: HttpSocketClient: connect: calling onConnect");
							onConnect.call(httpSocket);
						}
					} catch(Exception exc) {
						if(onError!=null) {
							if(DEBUG) System.out.println("DEBUG: HttpSocketClient: connect: calling onError");
							onError.call(exc);
						}
					}
				}
			}
		);
	}
}
