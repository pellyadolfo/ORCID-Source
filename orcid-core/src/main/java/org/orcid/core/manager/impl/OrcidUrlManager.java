/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;

public class OrcidUrlManager {

    static Pattern fileNamePattern = Pattern.compile("https{0,1}:\\/\\/[^\\/]*(.*){0,1}");

    static String PROTOCALL_PATTREN = "http[s]{0,1}:\\/\\/";

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Value("${org.orcid.core.pubBaseUri}")
    private String pubBaseUrl;

    @Value("${org.orcid.core.apiBaseUri}")
    private String apiBaseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseDomainRmProtocall() {
        return getBaseUrl().replaceAll(PROTOCALL_PATTREN, "");
    }

    public String getBaseUriHttp() {
        return this.baseUrl.replace("https", "http");
    }

    @ModelAttribute("basePath")
    public String getBasePath() {
        Matcher fileNameMatcher = fileNamePattern.matcher(getBaseUrl());
        if (!fileNameMatcher.find())
            return "/";
        return fileNameMatcher.group(1) + "/";
    }
    
    /** 
     * 
     * @return the path, without additional trailing slash
     */
    @ModelAttribute("apiPath")
    public String getApiPath() {
        Matcher fileNameMatcher = fileNamePattern.matcher(getApiBaseUrl());
        if (!fileNameMatcher.find())
            return "/";
        return fileNameMatcher.group(1);
    }
    
    /** 
     * 
     * @return the path, without additional trailing slash
     */
    @ModelAttribute("pubPath")
    public String getPubPath() {
        Matcher fileNameMatcher = fileNamePattern.matcher(this.getPubBaseUrl());
        if (!fileNameMatcher.find())
            return "/";
        return fileNameMatcher.group(1);
    }

    
    public String getBaseHost() {
        try {
            return new URI(this.baseUrl).getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem parsing base URI: " + this.baseUrl, e);
        }
    }
    
    public String getApiHostWithPort() {
        try {
            URI uri = new URI(this.apiBaseUrl);
            if (uri.getPort() >= 0)
                return uri.getHost() + ":"+uri.getPort();
            return uri.getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem parsing base URI: " + this.apiBaseUrl, e);
        }
    }
    
    public String getPubHostWithPort() {
        try {
            URI uri = new URI(this.pubBaseUrl);
            if (uri.getPort() >= 0)
                return uri.getHost() + ":"+uri.getPort();
            return uri.getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem parsing base URI: " + this.pubBaseUrl, e);
        }
    }

    public String getPubBaseUrl() {
        return pubBaseUrl;
    }

    public void setPubBaseUrl(String pubBaseUrl) {
        this.pubBaseUrl = pubBaseUrl;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getServerStringWithContextPath(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        if (isSecure(request))
        	sb.append(getBaseUrl());
        else
        	sb.append(getBaseUriHttp());
        return sb.toString();
    }

	public boolean isSecure(HttpServletRequest request) {
		String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String scheme = forwardedProto != null ? forwardedProto : request.getScheme();
		return scheme.equals("https");
	}
    
    public static String getPathWithoutContextPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }
    
}
