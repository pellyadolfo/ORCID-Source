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
package org.orcid.frontend.web.controllers;

import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Angel Montenegro
 */

@Controller
@RequestMapping(value = { "/admin-search" })
public class AdminSearchController extends BaseController {

    @Resource(name = "orcidSearchManagerReadOnly")
    private OrcidSearchManager orcidSearchManagerReadOnly;
        
    @Resource 
    private LocaleManager localeManager;
    
    @RequestMapping
    public ModelAndView getAdminSearchPage() {
        ModelAndView mav = new ModelAndView("includes/admin_search/admin_search");
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_ONLY);
        mav.addObject("profile", profile);
        return mav;
    }
    
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @RequestMapping(value = { "/search.json" }, method = RequestMethod.GET)
    public @ResponseBody Object search(@RequestParam("term") String term, @RequestParam("maxResults") String maxResults) {
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        setParam(queryParams, "start", "0");
        setParam(queryParams, "q", "{!edismax qf=\"given-and-family-names^50.0 family-name^10.0 given-names^5.0 credit-name^10.0 other-names^5.0 text^1.0\" pf=\"given-and-family-names^50.0\" mm=1}" + term);
        setParam(queryParams, "rows", maxResults);
        Response jsonQueryResults = searchByQuery(term, queryParams);
        return jsonQueryResults;
    }
    
    private void setParam(Map<String, List<String>> queryParams, String paramName, String ... values) {
        ArrayList<String> list = new ArrayList<String>();
        for(String value : values) {
            list.add(value);
        }
        queryParams.put(paramName, list);
    }
    
    public Response searchByQuery(String term, Map<String, List<String>> queryMap) {
        OrcidMessage orcidMessage = orcidSearchManagerReadOnly.findOrcidsByQuery(queryMap);
        List<OrcidSearchResult> searchResults = orcidMessage.getOrcidSearchResults() != null ? orcidMessage.getOrcidSearchResults().getOrcidSearchResult() : null;
        List<OrcidSearchResult> filteredResults = new ArrayList<OrcidSearchResult>();
        OrcidSearchResults orcidSearchResults = new OrcidSearchResults();
        if (searchResults != null) {
            orcidSearchResults.setNumFound(orcidMessage.getOrcidSearchResults().getNumFound());
            if (searchResults.size() > 0) {
                for (OrcidSearchResult searchResult : searchResults) {
                    OrcidSearchResult filteredSearchResult = new OrcidSearchResult();
                    OrcidProfile filteredProfile = new OrcidProfile();
                    filteredSearchResult.setRelevancyScore(searchResult.getRelevancyScore());
                    filteredProfile.setOrcid(searchResult.getOrcidProfile().getOrcid());
                    filteredProfile.setOrcidId(searchResult.getOrcidProfile().getOrcidId());
                    filteredProfile.setOrcidIdentifier(searchResult.getOrcidProfile().getOrcidIdentifier());
                    filteredProfile.setOrcidBio(searchResult.getOrcidProfile().getOrcidBio());
                    filteredSearchResult.setOrcidProfile(filteredProfile);
                    filteredResults.add(filteredSearchResult);
                }
            }
        }
        orcidSearchResults.getOrcidSearchResult().addAll(filteredResults);
        
        OrcidMessage result = new OrcidMessage();
        result.setMessageVersion("1.2");
        result.setOrcidSearchResults(orcidSearchResults);
        return Response.ok(result).build();        
    }
}
