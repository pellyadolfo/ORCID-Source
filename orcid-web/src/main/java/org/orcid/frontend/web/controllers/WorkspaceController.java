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

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.ThirdPartyLinkManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.frontend.web.util.NumberList;
import org.orcid.frontend.web.util.YearsList;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FundingContributorRole;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.record.CitationType;
import org.orcid.jaxb.model.record.PeerReviewType;
import org.orcid.jaxb.model.record.Role;
import org.orcid.jaxb.model.record.WorkCategory;
import org.orcid.jaxb.model.record.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.WorkType;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.pojo.ThirdPartyRedirect;
import org.orcid.pojo.ajaxForm.KeywordsForm;
import org.orcid.pojo.ajaxForm.OtherNamesForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Website;
import org.orcid.pojo.ajaxForm.WebsitesForm;
import org.orcid.utils.FunctionsOverCollections;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 * @author Will Simpson
 */
@Controller("workspaceController")
public class WorkspaceController extends BaseWorkspaceController {

    @Resource
    private ThirdPartyLinkManager thirdPartyLinkManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;    
    
    @Resource
    private ProfileKeywordManager profileKeywordManager;
    
    @Resource
    private OtherNameManager otherNameManager;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    @Resource
    private WorkManager workManager;
    
    @Resource
    private ResearcherUrlManager researcherUrlManager;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;
    
    @RequestMapping(value = { "/workspace/retrieve-work-impor-wizards.json" }, method = RequestMethod.GET)
    public @ResponseBody List<OrcidClient> retrieveWorkImportWizards() {
        return thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeWorksImport();
    }

    @ModelAttribute("fundingImportWizards")
    public List<OrcidClient> retrieveFundingImportWizards() {
        return thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeFundingImport();
    }
    
    @RequestMapping(value = { "/workspace/retrieve-peer-review-import-wizards.json" }, method = RequestMethod.GET)
    public @ResponseBody List<OrcidClient> retrievePeerReviewImportWizards() {
        return thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopePeerReviewImport();
    }

    @ModelAttribute("affiliationTypes")
    public Map<String, String> retrieveAffiliationTypesAsMap() {
        Map<String, String> affiliationTypes = new LinkedHashMap<String, String>();
        for (AffiliationType affiliationType : AffiliationType.values()) {
            affiliationTypes.put(affiliationType.value(), getMessage(buildInternationalizationKey(AffiliationType.class, affiliationType.value())));
        }
        return FunctionsOverCollections.sortMapsByValues(affiliationTypes);
    }

    @ModelAttribute("fundingTypes")
    public Map<String, String> retrieveFundingTypesAsMap() {
        Map<String, String> grantTypes = new LinkedHashMap<String, String>();
        for (FundingType fundingType : FundingType.values()) {
            grantTypes.put(fundingType.value(), getMessage(buildInternationalizationKey(FundingType.class, fundingType.value())));
        }
        return FunctionsOverCollections.sortMapsByValues(grantTypes);
    }

    @ModelAttribute("currencyCodeTypes")
    public Map<String, String> retrieveCurrencyCodesTypesAsMap() {
        Map<String, String> currencyCodeTypes = new LinkedHashMap<String, String>();
        //Add an empty one
        currencyCodeTypes.put("", "");
        for (Currency currency : Currency.getAvailableCurrencies()) {
            currencyCodeTypes.put(currency.getCurrencyCode(), currency.getCurrencyCode());
        }
        return FunctionsOverCollections.sortMapsByValues(currencyCodeTypes);
    }

    @ModelAttribute("affiliationLongDescriptionTypes")
    public Map<String, String> retrieveAffiliationLongDescriptionTypesAsMap() {
        Map<String, String> organizationTypes = new LinkedHashMap<String, String>();
        for (AffiliationType organizationType : AffiliationType.values()) {
            organizationTypes.put(organizationType.value(), getMessage(AffiliationType.class.getName() + '.' + "longDescription" + '.' + organizationType.value()));
        }
        return FunctionsOverCollections.sortMapsByValues(organizationTypes);
    }

    @ModelAttribute("workCategories")
    public Map<String, String> retrieveWorkCategoriesAsMap() {
        Map<String, String> workCategories = new LinkedHashMap<String, String>();

        for (WorkCategory workCategory : WorkCategory.values()) {
            workCategories.put(workCategory.value(), getMessage(buildInternationalizationKey(WorkCategory.class, workCategory.value())));
        }

        return workCategories;
    }

    @ModelAttribute("citationTypes")
    public Map<String, String> retrieveTypesAsMap() {
        Map<String, String> citationTypes = new LinkedHashMap<String, String>();

        for (CitationType citationType : CitationType.values()) {
            citationTypes.put(citationType.value(), getMessage(buildInternationalizationKey(CitationType.class, citationType.value())));
        }

        return FunctionsOverCollections.sortMapsByValues(citationTypes);
    }

    @ModelAttribute("years")
    public Map<String, String> retrieveYearsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList();
        map.put("", getMessage("select.item.year"));
        for (String year : list) {
            map.put(year, year);
        }
        return map;
    }

    @ModelAttribute("fundingYears")
    public Map<String, String> retrieveFundingYearsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList(10);
        map.put("", getMessage("select.item.year"));
        for (String year : list) {
            map.put(year, year);
        }
        return map;
    }

    @ModelAttribute("months")
    public Map<String, String> retrieveMonthsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = NumberList.createList(12);
        map.put("", getMessage("select.item.month"));
        for (String month : list) {
            map.put(month, month);
        }
        return map;
    }

    @ModelAttribute("days")
    public Map<String, String> retrieveDaysAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = NumberList.createList(31);
        map.put("", getMessage("select.item.day"));
        for (String day : list) {
            map.put(day, day);
        }
        return map;
    }

    /**
     * Generate a map with ID types. The map is different from the rest, because
     * it will be ordered in the form: value -> key, to keep the map alpha
     * ordered in UI.
     * */
    @ModelAttribute("idTypes")
    public Map<String, String> retrieveIdTypesAsMap() {
        Map<String, String> map = new TreeMap<String, String>();

        for (WorkExternalIdentifierType type : WorkExternalIdentifierType.values()) {
            map.put(getMessage(buildInternationalizationKey(WorkExternalIdentifierType.class, type.value())), type.value());
        }

        return FunctionsOverCollections.sortMapsByValues(map);
    }

    @ModelAttribute("roles")
    public Map<String, String> retrieveRolesAsMap() {
        Map<String, String> map = new TreeMap<String, String>();

        for (ContributorRole contributorRole : ContributorRole.values()) {
            map.put(contributorRole.value(), getMessage(buildInternationalizationKey(ContributorRole.class, contributorRole.value())));
        }
        return FunctionsOverCollections.sortMapsByValues(map);
    }

    @ModelAttribute("fundingRoles")
    public Map<String, String> retrieveFundingRolesAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();

        for (FundingContributorRole contributorRole : FundingContributorRole.values()) {
            map.put(contributorRole.value(), getMessage(buildInternationalizationKey(FundingContributorRole.class, contributorRole.value())));
        }
        return map;
    }

    @ModelAttribute("sequences")
    public Map<String, String> retrieveSequencesAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();

        for (SequenceType sequenceType : SequenceType.values()) {
            map.put(sequenceType.value(), getMessage(buildInternationalizationKey(SequenceType.class, sequenceType.value())));
        }

        return FunctionsOverCollections.sortMapsByValues(map);
    }

    @ModelAttribute("languages")
    public Map<String, String> retrieveLocalesAsMap() {
        return lm.getLanguagesMap(localeManager.getLocale());
    }

    @ModelAttribute("peerReviewRoles")
    public Map<String, String> retrievePeerReviewRolesAsMap() {
        Map<String, String> peerReviewRoles = new LinkedHashMap<String, String>();
        for (Role role : Role.values()) {
            peerReviewRoles.put(role.value(), getMessage(buildInternationalizationKey(Role.class, role.name())));
        }
        return FunctionsOverCollections.sortMapsByValues(peerReviewRoles);
    }
    
    @ModelAttribute("peerReviewTypes")
    public Map<String, String> retrievePeerReviewTypesAsMap() {
        Map<String, String> peerReviewTypes = new LinkedHashMap<String, String>();
        for (PeerReviewType type : PeerReviewType.values()) {
            peerReviewTypes.put(type.value(), getMessage(buildInternationalizationKey(PeerReviewType.class, type.name())));
        }
        return FunctionsOverCollections.sortMapsByValues(peerReviewTypes);
    }
    
    @ModelAttribute("workTypes")
    public Map<String, String> retrieveWorkTypesAsMap() {
        Map<String, String> types = new LinkedHashMap<String, String>();
        for (WorkType type : WorkType.values()) {
            types.put(type.value(), getMessage(buildInternationalizationKey(WorkType.class, type.value())));
        }
        return FunctionsOverCollections.sortMapsByValues(types);
    }
    
    @RequestMapping(value = {"/my-orcid3","/my-orcid", "/workspace"}, method = RequestMethod.GET)
    public ModelAndView viewWorkspace3(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "200") int maxResults) {
        ModelAndView mav = new ModelAndView("workspace_v3");
        mav.addObject("showPrivacy", true);

        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_AND_INTERNAL_ONLY);
        mav.addObject("profile", profile);
        String countryName = getCountryName(profile);
        if(!StringUtil.isBlank(countryName))
            mav.addObject("countryName", countryName);
        mav.addObject("currentLocaleKey", localeManager.getLocale().toString());
        mav.addObject("currentLocaleValue", lm.buildLanguageValue(localeManager.getLocale(), localeManager.getLocale()));
        return mav;
    }

    
    @RequestMapping(value = "/my-orcid/keywordsForms.json", method = RequestMethod.GET)
    public @ResponseBody
    KeywordsForm getKeywordsFormJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        return KeywordsForm.valueOf(currentProfile.getOrcidBio().getKeywords());
    }
    
    @RequestMapping(value = "/my-orcid/keywordsForms.json", method = RequestMethod.POST)
    public @ResponseBody
    KeywordsForm setKeywordsFormJson(HttpServletRequest request, @RequestBody KeywordsForm kf) throws NoSuchRequestHandlingMethodException {
        kf.setErrors(new ArrayList<String>());
    	for (int i = kf.getKeywords().size() - 1; i >= 0; i--) {
            Text t = kf.getKeywords().get(i);
            if (PojoUtil.isEmpty(t))
                kf.getKeywords().remove(i);
            else if (t.getValue().length() > 100)
                t.setValue(t.getValue().substring(0,100));
        }
        if (kf.getErrors().size()>0) return kf;        
        OrcidProfile currentProfile = getEffectiveProfile();
        profileKeywordManager.updateProfileKeyword(currentProfile.getOrcidIdentifier().getPath(), kf.toKeywords());
        return kf;
    }

    
    @RequestMapping(value = "/my-orcid/otherNamesForms.json", method = RequestMethod.GET)
    public @ResponseBody
    OtherNamesForm getOtherNamesFormJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        return OtherNamesForm.valueOf(currentProfile.getOrcidBio().getPersonalDetails().getOtherNames());
    }
    
    @RequestMapping(value = "/my-orcid/otherNamesForms.json", method = RequestMethod.POST)
    public @ResponseBody
    OtherNamesForm setOtherNamesFormJson(HttpServletRequest request, @RequestBody OtherNamesForm onf) throws NoSuchRequestHandlingMethodException {
        onf.setErrors(new ArrayList<String>());
        for (int i = onf.getOtherNames().size() - 1; i > 0; i--) {
            Text t = onf.getOtherNames().get(i);
            if (PojoUtil.isEmpty(t))
                onf.getOtherNames().remove(i);
            else if (t.getValue().length() > 255)
                t.setValue(t.getValue().substring(0,255));
        }
        if (onf.getErrors().size()>0) return onf;        
        OrcidProfile currentProfile = getEffectiveProfile();
        
        otherNameManager.updateOtherNames(currentProfile.getOrcidIdentifier().getPath(), onf.toOtherNames());
        return onf;
    }
    
    /**
     * Retrieve all external identifiers as a json string
     * */
    @RequestMapping(value = "/my-orcid/websitesForms.json", method = RequestMethod.GET)
    public @ResponseBody
    WebsitesForm getWebsitesFormJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        WebsitesForm wf = WebsitesForm.valueOf(currentProfile.getOrcidBio().getResearcherUrls());
        return wf;
    }
    
    /**
     * Retrieve all external identifiers as a json string
     * */
    @RequestMapping(value = "/my-orcid/websitesForms.json", method = RequestMethod.POST)
    public @ResponseBody
    WebsitesForm setWebsitesFormJson(HttpServletRequest request, @RequestBody WebsitesForm ws) throws NoSuchRequestHandlingMethodException {
        ws.setErrors(new ArrayList<String>());
        HashMap<String, Website> websitesHm = new HashMap<String, Website>(); 
        for (Website w:ws.getWebsites()) {
            //Clean old errors
            w.setErrors(new ArrayList<String>());
            w.getUrl().setErrors(new ArrayList<String>());
            w.getName().setErrors(new ArrayList<String>());
            //Validate
            validateUrl(w.getUrl(), SiteConstants.URL_MAX_LENGTH);
            validateNoLongerThan(SiteConstants.URL_MAX_LENGTH, w.getName());
            if (websitesHm.containsKey(w.getUrl().getValue()))
                setError(w.getUrl(), "common.duplicate_url");
            else
                websitesHm.put(w.getUrl().getValue(), w);
            copyErrors(w.getUrl(), ws);
            copyErrors(w.getName(), ws);
        }   
        if (ws.getErrors().size()>0) return ws;        
        OrcidProfile currentProfile = getEffectiveProfile();
        researcherUrlManager.updateResearcherUrls(currentProfile.getOrcidIdentifier().getPath(), ws.toResearcherUrls());
        return ws;
    }

    /**
     * Retrieve all external identifiers as a json string
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/my-orcid/externalIdentifiers.json", method = RequestMethod.GET)
    public @ResponseBody
    org.orcid.pojo.ExternalIdentifiers getExternalIdentifiersJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        org.orcid.pojo.ExternalIdentifiers externalIdentifiers = new org.orcid.pojo.ExternalIdentifiers();
        externalIdentifiers.setExternalIdentifiers((List<org.orcid.pojo.ExternalIdentifier>) (Object) currentProfile.getOrcidBio().getExternalIdentifiers()
                .getExternalIdentifier());
        return externalIdentifiers;
    }

    @RequestMapping(value = "/my-orcid/sourceGrantReadWizard.json", method = RequestMethod.GET)
    public @ResponseBody
    ThirdPartyRedirect getSourceGrantReadWizard() {
        ThirdPartyRedirect tpr = new ThirdPartyRedirect();

        OrcidProfile currentProfile = getEffectiveProfile();
        if (currentProfile.getOrcidHistory().getSource() == null)
            return tpr;
        Source source = currentProfile.getOrcidHistory().getSource();
        String sourcStr = source.retrieveSourcePath();        
        // Check that the cache is up to date
        evictThirdPartyLinkManagerCacheIfNeeded();
        // Get list of clients
        List<OrcidClient> orcidClients = thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeReadAccess();
        for (OrcidClient orcidClient : orcidClients) {
            if (sourcStr.equals(orcidClient.getClientId())) {
                RedirectUri ru = orcidClient.getRedirectUris().getRedirectUri().get(0);
                String redirect = getBaseUri() + "/oauth/authorize?client_id=" + orcidClient.getClientId() + "&response_type=code&scope=" + ru.getScopeAsSingleString()
                        + "&redirect_uri=" + ru.getValue();
                tpr.setUrl(redirect);
                tpr.setDisplayName(orcidClient.getDisplayName());
                tpr.setShortDescription(orcidClient.getShortDescription());
                return tpr;
            }
        }
        return tpr;
    }        
    
    /**
     * Reads the latest cache version from database, compare it against the
     * local version; if they are different, evicts all caches.
     * 
     * @return true if the local cache version is different than the one on
     *         database
     * */
    private boolean evictThirdPartyLinkManagerCacheIfNeeded() {
        long currentCachedVersion = thirdPartyLinkManager.getLocalCacheVersion();
        long dbCacheVersion = thirdPartyLinkManager.getDatabaseCacheVersion();
        if (currentCachedVersion < dbCacheVersion) {
            // If version changed, evict the cache
            thirdPartyLinkManager.evictAll();
            return true;
        }
        return false;
    }

    /**
     * Updates the list of external identifiers assigned to a user
     * */
    @RequestMapping(value = "/my-orcid/externalIdentifiers.json", method = RequestMethod.DELETE)
    public @ResponseBody
    org.orcid.pojo.ExternalIdentifier removeExternalIdentifierJson(HttpServletRequest request, @RequestBody org.orcid.pojo.ExternalIdentifier externalIdentifier) {
        List<String> errors = new ArrayList<String>();

        // If the external identifier is blank, add an error
        if (externalIdentifier.getExternalIdReference() == null || StringUtils.isBlank(externalIdentifier.getExternalIdReference().getContent())) {
            errors.add(getMessage("ExternalIdentifier.externalIdReference"));
        }
        // Set errors to the external
        externalIdentifier.setErrors(errors);

        if (errors.isEmpty()) {
            // Get cached profile
            OrcidProfile currentProfile = getEffectiveProfile();
            ExternalIdentifiers externalIdentifiers = currentProfile.getOrcidBio().getExternalIdentifiers();
            List<ExternalIdentifier> externalIdentifiersList = externalIdentifiers.getExternalIdentifier();
            Iterator<ExternalIdentifier> externalIdentifierIterator = externalIdentifiersList.iterator();
            // Remove external identifier from the cached profile
            while (externalIdentifierIterator.hasNext()) {
                ExternalIdentifier existingExternalIdentifier = externalIdentifierIterator.next();
                if (existingExternalIdentifier.equals(externalIdentifier)) {
                    externalIdentifierIterator.remove();
                }
            }
            // Update cached profile
            currentProfile.getOrcidBio().setExternalIdentifiers(externalIdentifiers);
            // Remove external identifier
            externalIdentifierManager.removeExternalIdentifier(currentProfile.getOrcidIdentifier().getPath(), externalIdentifier.getExternalIdReference().getContent());
        }

        return externalIdentifier;
    }

}