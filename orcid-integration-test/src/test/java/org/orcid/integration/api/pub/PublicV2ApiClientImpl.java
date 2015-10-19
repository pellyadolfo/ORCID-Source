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
package org.orcid.integration.api.pub;

import static org.orcid.core.api.OrcidApiConstants.ACTIVITIES;
import static org.orcid.core.api.OrcidApiConstants.PUTCODE;
import static org.orcid.core.api.OrcidApiConstants.RESEARCHER_URLS;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.WORK;
import static org.orcid.core.api.OrcidApiConstants.WORK_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW_SUMMARY;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.pojo.ajaxForm.PojoUtil;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class PublicV2ApiClientImpl {

    private OrcidClientHelper orcidClientHelper;

    public PublicV2ApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }  

    public ClientResponse viewActivities(String orcid) {
        return viewActivities(orcid, null);
    }
    
    public ClientResponse viewActivities(String orcid, String token) {
        URI activitiesUri = UriBuilder.fromPath(ACTIVITIES).build(orcid);
        return getClientReponse(activitiesUri, token);
    }
    
    public ClientResponse viewWorkXml(String orcid, String putCode) {
        return viewWorkXml(orcid, putCode, null);
    }
    
    public ClientResponse viewWorkXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(WORK + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }        
 
    public ClientResponse viewWorkSummaryXml(String orcid, String putCode) {
        return viewWorkSummaryXml(orcid, putCode, null);
    }
    
    public ClientResponse viewWorkSummaryXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(WORK_SUMMARY + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewFundingXml(String orcid, String putCode) {        
        return viewFundingXml(orcid, putCode, null);
    }
    
    public ClientResponse viewFundingXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(FUNDING + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewFundingSummaryXml(String orcid, String putCode) {
        return viewFundingSummaryXml(orcid, putCode, null);
    }
    
    public ClientResponse viewFundingSummaryXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(FUNDING_SUMMARY + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewEducationXml(String orcid, String putCode) {
        return viewEducationXml(orcid, putCode, null);
    }
    
    public ClientResponse viewEducationXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(EDUCATION + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewEducationSummaryXml(String orcid, String putCode) {
        return viewEducationSummaryXml(orcid, putCode, null);
    }
    
    public ClientResponse viewEducationSummaryXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(EDUCATION_SUMMARY + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewEmploymentXml(String orcid, String putCode) {        
        return viewEmploymentXml(orcid, putCode, null);
    }
 
    public ClientResponse viewEmploymentXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(EMPLOYMENT + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewEmploymentSummaryXml(String orcid, String putCode) {
        return viewEmploymentSummaryXml(orcid, putCode, null);
    }
    
    public ClientResponse viewEmploymentSummaryXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(EMPLOYMENT_SUMMARY + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewPeerReviewXml(String orcid, String putCode) {
        return viewPeerReviewXml(orcid, putCode, null);
    }
    
    public ClientResponse viewPeerReviewXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(PEER_REVIEW + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewPeerReviewSummaryXml(String orcid, String putCode) {
        return viewPeerReviewSummaryXml(orcid, putCode, null);
    }
    
    public ClientResponse viewPeerReviewSummaryXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(PEER_REVIEW_SUMMARY + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }    
    
    public ClientResponse viewResearcherUrlsXML(String orcid) {
        URI getURI = UriBuilder.fromPath(RESEARCHER_URLS).build(orcid);
        return getClientReponse(getURI, null);        
    }
    
    public ClientResponse viewResearcherUrlXML(String orcid, String putCode) {
        URI getURI = UriBuilder.fromPath(RESEARCHER_URLS + PUTCODE).build(orcid, putCode);
        return getClientReponse(getURI, null);        
    }        
    
    private ClientResponse getClientReponse(URI uri, String token) {
        ClientResponse result = null;
        if(PojoUtil.isEmpty(token)) {
            result = orcidClientHelper.getClientResponse(uri, VND_ORCID_XML);
        } else {
            result = orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, token);
        }
        return result;
    }
}
