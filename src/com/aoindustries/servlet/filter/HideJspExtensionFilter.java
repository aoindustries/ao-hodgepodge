/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2015  AO Industries, Inc.
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
package com.aoindustries.servlet.filter;

import com.aoindustries.net.UrlUtils;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.servlet.http.ServletUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * <p>
 * A servlet filter that hides the .jsp extension from JSP-based sites.
 * It accomplishes this with the following steps:
 * </p>
 * <ol>
 * <li>Rewrite any URLs ending in "/path/index.jsp" to "/path/", maintaining any query string</li>
 * <li>Rewrite any URLs ending in "/path/file.jsp" to "/path/file", maintaining any query string</li>
 * <li>301 redirect any incoming request ending in "/path/index.jsp" to "/path/" (to not lose traffic after enabling the filter)</li>
 * <li>Forward incoming request of "/path/" to "/path/index.jsp", if the resource exists.
 *     This is done by container with a welcome file list of index.jsp in web.xml.</li>
 * <li>Forward incoming request of "/path/file" to "/path/file.jsp", if the resource exists</li>
 * <li>Send any other request down the filter chain</li>
 * </ol>
 * <p>
 * This should be used for the REQUEST dispatcher only.
 * </p>
 * <p>
 * In the filter chain, it is important to consider the forwarding performed by this filter.  Subsequent filters
 * may need FILTER dispatcher in addition to REQUEST to see the forwarded requests.
 * </p>
 * <p>
 * Note: When testing in Tomcat 7, /WEB-INF/ protection was not violated by the forwarding.
 * Requests to /WEB-INF/ never hit the filter.
 * </p>
 */
public class HideJspExtensionFilter implements Filter {

    private static final String FILTER_APPLIED_KEY = HideJspExtensionFilter.class.getName()+".filterApplied";

	private static final String JSP_EXTENSION = ".jsp";
	private static final String INDEX_JSP = "index" + JSP_EXTENSION;
	private static final String SLASH_INDEX_JSP = "/" + INDEX_JSP;

	private ServletContext servletContext;

	@Override
    public void init(FilterConfig config) {
        ServletContext configContext = config.getServletContext();
		this.servletContext = configContext;
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        if(request.getAttribute(FILTER_APPLIED_KEY)==null) {
            try {
                request.setAttribute(FILTER_APPLIED_KEY, Boolean.TRUE);
                if(
                    (request instanceof HttpServletRequest)
                    && (response instanceof HttpServletResponse)
                ) {
                    final HttpServletRequest httpRequest = (HttpServletRequest)request;
                    final HttpServletResponse httpResponse = (HttpServletResponse)response;

					// 301 redirect any incoming request ending in "/path/index.jsp" to "/path/" (to not lose traffic after enabling the filter)
					String servletPath = httpRequest.getServletPath();
					if(servletPath.endsWith(SLASH_INDEX_JSP)) {
						// "index.jsp" is added to the servlet path for requests ending in /, this
						// uses the un-decoded requestUri to distinguish between the two
						if(httpRequest.getRequestURI().endsWith(SLASH_INDEX_JSP)) {
							String queryString = httpRequest.getQueryString();
							String path = servletPath.substring(0, servletPath.length() - INDEX_JSP.length());
							// Encode URL path elements (like Japanese filenames)
							path = UrlUtils.encodeUrlPath(path);
							// Add any query string
							if(queryString != null) {
								path = path + '?' + queryString;
							}
							// Perform URL rewriting
							path = httpResponse.encodeRedirectURL(path);
							// Convert to absolute URL
							String location = ServletUtil.getAbsoluteURL(httpRequest, path);
							ServletUtil.sendRedirect(httpResponse, location, HttpServletResponse.SC_MOVED_PERMANENTLY);
							return;
						}
					}

					HttpServletResponse rewritingResponse = new HttpServletResponseWrapper(httpResponse) {
						private String encode(String url) {
							int questionPos = url.indexOf('?');
							// Strip the parameters
							String noParamsUrl = questionPos==-1 ? url : url.substring(0, questionPos);
							// Strip any remaining anchor (one still after parameters will remain with parameters)
							int anchorPos = noParamsUrl.lastIndexOf('#');
							String noAnchorUrl = anchorPos==-1 ? noParamsUrl : noParamsUrl.substring(0, anchorPos);
							// Rewrite any URLs ending in "/path/index.jsp" to "/path/", maintaining any query string
							if(noAnchorUrl.endsWith(SLASH_INDEX_JSP)) {
								String shortenedUrl = noAnchorUrl.substring(0, noAnchorUrl.length() - INDEX_JSP.length());
								if(questionPos == -1) {
									if(anchorPos == -1) {
										return shortenedUrl;
									} else {
										return shortenedUrl + noParamsUrl.substring(anchorPos);
									}
								} else {
									if(anchorPos == -1) {
										return shortenedUrl + url.substring(questionPos);
									} else {
										throw new AssertionError("Since anchors come after parameters, this should never happen because the anchor is left on the parameters");
									}
								}
							}
							// Rewrite any URLs ending in "/path/file.jsp" to "/path/file", maintaining any query string
							if(noAnchorUrl.endsWith(JSP_EXTENSION)) {
								String shortenedUrl = noAnchorUrl.substring(0, noAnchorUrl.length() - JSP_EXTENSION.length());
								if(!shortenedUrl.endsWith("/")) {
									if(questionPos == -1) {
										if(anchorPos == -1) {
											return shortenedUrl;
										} else {
											return shortenedUrl + noParamsUrl.substring(anchorPos);
										}
									} else {
										if(anchorPos == -1) {
											return shortenedUrl + url.substring(questionPos);
										} else {
											throw new AssertionError("Since anchors come after parameters, this should never happen because the anchor is left on the parameters");
										}
									}
								}
							}
							return url;
						}

						@Deprecated
						@Override
						public String encodeUrl(String url) {
							return encode(url);
						}
						@Override
						public String encodeURL(String url) {
							return encode(url);
						}

						@Deprecated
						@Override
						public String encodeRedirectUrl(String url) {
							return encode(url);
						}
						@Override
						public String encodeRedirectURL(String url) {
							return encode(url);
						}
					};

					// Forward incoming request of "/path/" to "/path/index.jsp", if the resource exists
					// This is done by container with a welcome file list of index.jsp in web.xml.

					// Forward incoming request of "/path/file" to "/path/file.jsp", if the resource exists
					if(!servletPath.endsWith("/")) {
						String resourcePath = servletPath + JSP_EXTENSION;
						URL resourceUrl;
						try {
							resourceUrl = servletContext.getResource(resourcePath);
						} catch(MalformedURLException e) {
							// Assume does not exist
							resourceUrl = null;
						}
						if(resourceUrl != null) {
							// Forward to JSP file
							Dispatcher.forward(
								servletContext,
								resourcePath,
								httpRequest,
								rewritingResponse
							);
							return;
						}
					}
					// Send any other request down the filter chain</li>
					chain.doFilter(httpRequest, rewritingResponse);
                } else {
					// Not HTTP protocol
                    chain.doFilter(request, response);
                }
            } finally {
                request.removeAttribute(FILTER_APPLIED_KEY);
            }
        } else {
            // Filter already applied
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
		servletContext = null;
    }
}
