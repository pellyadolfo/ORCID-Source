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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidSSOManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.SSOCredentials;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller("developerToolsController")
@RequestMapping(value = { "/developer-tools" })
@PreAuthorize("!@sourceManager.isInDelegationMode() OR @sourceManager.isDelegatedByAnAdmin()")
public class DeveloperToolsController extends BaseWorkspaceController {

    private static int CLIENT_NAME_LENGTH = 255;

    @Resource
    private OrcidSSOManager orcidSSOManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private ResearcherUrlDao researcherUrlDao;
    
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;
    
    @RequestMapping
    public ModelAndView manageDeveloperTools() {
        ModelAndView mav = new ModelAndView("developer_tools");
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_AND_INTERNAL_ONLY);
        mav.addObject("profile", profile);
        try {
            if (!profile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled().isValue()) {                
                if (OrcidType.USER.equals(profile.getType())) {
                    mav.addObject("error", getMessage("manage.developer_tools.user.error.enable_developer_tools"));
                } else {
                    mav.addObject("error", getMessage("manage.developer_tools.user.error.invalid_user_type"));
                }
            }
        } catch (NullPointerException npe) {

        }
        return mav;
    }

    @RequestMapping(value = "/get-empty-redirect-uri.json", method = RequestMethod.GET)
    public @ResponseBody
    RedirectUri getEmptyRedirectUri(HttpServletRequest request) {
        RedirectUri result = new RedirectUri();
        result.setValue(new Text());
        result.setActType(Text.valueOf(""));
        result.setGeoArea(Text.valueOf(""));
        result.setType(Text.valueOf(RedirectUriType.DEFAULT.name()));
        return result;
    }

    @RequestMapping(value = "/get-empty-sso-credential.json", method = RequestMethod.GET)
    public @ResponseBody
    SSOCredentials getEmptySSOCredentials(HttpServletRequest request) {
        SSOCredentials emptyObject = new SSOCredentials();
        emptyObject.setClientSecret(Text.valueOf(StringUtils.EMPTY));

        RedirectUri redirectUri = new RedirectUri();
        redirectUri.setValue(new Text());
        redirectUri.setType(Text.valueOf(RedirectUriType.DEFAULT.name()));

        Set<RedirectUri> set = new HashSet<RedirectUri>();
        set.add(redirectUri);
        emptyObject.setRedirectUris(set);
        return emptyObject;
    }

    @RequestMapping(value = "/generate-sso-credentials.json", method = RequestMethod.POST)
    public @ResponseBody
    SSOCredentials generateSSOCredentialsJson(@RequestBody SSOCredentials ssoCredentials) {
        boolean hasErrors = validateSSOCredentials(ssoCredentials);

        if (!hasErrors) {            
            OrcidProfile profile = getEffectiveProfile();
            String orcid = profile.getOrcidIdentifier().getPath();
            Set<String> redirectUriStrings = new HashSet<String>();
            for (RedirectUri redirectUri : ssoCredentials.getRedirectUris()) {
                redirectUriStrings.add(redirectUri.getValue().getValue());
            }
            String clientName = ssoCredentials.getClientName().getValue();
            String clientDescription = ssoCredentials.getClientDescription().getValue();
            String clientWebsite = ssoCredentials.getClientWebsite().getValue();
            ClientDetailsEntity clientDetails = orcidSSOManager.grantSSOAccess(orcid, clientName, clientDescription, clientWebsite, redirectUriStrings);
            ssoCredentials = SSOCredentials.toSSOCredentials(clientDetails);
        } else {
            List<String> errors = ssoCredentials.getErrors();
            if (errors == null)
                errors = new ArrayList<String>();

            if (ssoCredentials.getClientName().getErrors() != null && !ssoCredentials.getClientName().getErrors().isEmpty())
                errors.addAll(ssoCredentials.getClientName().getErrors());

            if (ssoCredentials.getClientDescription().getErrors() != null && !ssoCredentials.getClientDescription().getErrors().isEmpty())
                errors.addAll(ssoCredentials.getClientDescription().getErrors());

            if (ssoCredentials.getClientWebsite().getErrors() != null && !ssoCredentials.getClientWebsite().getErrors().isEmpty())
                errors.addAll(ssoCredentials.getClientWebsite().getErrors());

            if (ssoCredentials.getRedirectUris() != null) {
                for (RedirectUri redirectUri : ssoCredentials.getRedirectUris()) {
                    if (redirectUri.getErrors() != null && !redirectUri.getErrors().isEmpty())
                        errors.addAll(redirectUri.getErrors());
                }
            }
            ssoCredentials.setErrors(errors);
        }

        return ssoCredentials;
    }

    @RequestMapping(value = "/update-user-credentials.json", method = RequestMethod.POST)
    public @ResponseBody
    SSOCredentials updateUserCredentials(@RequestBody SSOCredentials ssoCredentials) {
        boolean hasErrors = validateSSOCredentials(ssoCredentials);

        if (!hasErrors) {            
            OrcidProfile profile = getEffectiveProfile();
            String orcid = profile.getOrcidIdentifier().getPath();
            Set<String> redirectUriStrings = new HashSet<String>();
            for (RedirectUri redirectUri : ssoCredentials.getRedirectUris()) {
                redirectUriStrings.add(redirectUri.getValue().getValue());
            }
            String clientName = ssoCredentials.getClientName().getValue();
            String clientDescription = ssoCredentials.getClientDescription().getValue();
            String clientWebsite = ssoCredentials.getClientWebsite().getValue();
            ClientDetailsEntity clientDetails = orcidSSOManager.updateUserCredentials(orcid, clientName, clientDescription, clientWebsite, redirectUriStrings);
            ssoCredentials = SSOCredentials.toSSOCredentials(clientDetails);
            ssoCredentials.setClientWebsite(Text.valueOf(clientWebsite));
        } else {
            List<String> errors = ssoCredentials.getErrors();
            if (errors == null)
                errors = new ArrayList<String>();

            if (ssoCredentials.getClientName().getErrors() != null && !ssoCredentials.getClientName().getErrors().isEmpty())
                errors.addAll(ssoCredentials.getClientName().getErrors());

            if (ssoCredentials.getClientDescription().getErrors() != null && !ssoCredentials.getClientDescription().getErrors().isEmpty())
                errors.addAll(ssoCredentials.getClientDescription().getErrors());

            if (ssoCredentials.getClientWebsite().getErrors() != null && !ssoCredentials.getClientWebsite().getErrors().isEmpty())
                errors.addAll(ssoCredentials.getClientWebsite().getErrors());

            for (RedirectUri redirectUri : ssoCredentials.getRedirectUris()) {
                if (redirectUri.getErrors() != null && !redirectUri.getErrors().isEmpty())
                    errors.addAll(redirectUri.getErrors());
            }
            ssoCredentials.setErrors(errors);
        }
        return ssoCredentials;
    }

    @RequestMapping(value = "/reset-client-secret", method = RequestMethod.POST)
    public @ResponseBody
    boolean resetClientSecret(@RequestBody String clientId) {
        //Verify this client belongs to the user
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        if(clientDetails == null)
            return false;
        ProfileEntity groupProfile = profileEntityCacheManager.retrieve(clientDetails.getGroupProfileId());
        if(groupProfile == null)
            return false;
        if(!groupProfile.getId().equals(getCurrentUserOrcid()))
            return false;               
        return orcidSSOManager.resetClientSecret(clientId);
    }

    @RequestMapping(value = "/get-sso-credentials.json", method = RequestMethod.GET)
    public @ResponseBody
    SSOCredentials getSSOCredentialsJson() {
        SSOCredentials credentials = new SSOCredentials();
        String userOrcid = getEffectiveUserOrcid();
        ClientDetailsEntity existingClientDetails = orcidSSOManager.getUserCredentials(userOrcid);
        if (existingClientDetails != null)
            credentials = SSOCredentials.toSSOCredentials(existingClientDetails);

        return credentials;
    }

    @RequestMapping(value = "/revoke-sso-credentials.json", method = RequestMethod.POST)
    public @ResponseBody
    SSOCredentials revokeSSOCredentials(HttpServletRequest request) {
        throw new NotImplementedException();
    }
    
    /**
     * Validates the ssoCredentials object
     * 
     * @param ssoCredentials
     * @return true if any error is found in the ssoCredentials object
     * */
    private boolean validateSSOCredentials(SSOCredentials ssoCredentials) {
        boolean hasErrors = false;
        Set<RedirectUri> redirectUris = ssoCredentials.getRedirectUris();
        if (PojoUtil.isEmpty(ssoCredentials.getClientName())) {
            if (ssoCredentials.getClientName() == null) {
                ssoCredentials.setClientName(new Text());
            }
            ssoCredentials.getClientName().setErrors(Arrays.asList(getMessage("manage.developer_tools.name_not_empty")));
            hasErrors = true;
        } else if (ssoCredentials.getClientName().getValue().length() > CLIENT_NAME_LENGTH) {
            ssoCredentials.getClientName().setErrors(Arrays.asList(getMessage("manage.developer_tools.name_too_long")));
            hasErrors = true;
        } else if(OrcidStringUtils.hasHtml(ssoCredentials.getClientName().getValue())){
            ssoCredentials.getClientName().setErrors(Arrays.asList(getMessage("manage.developer_tools.name.html")));
            hasErrors = true;
        } else {
            ssoCredentials.getClientName().setErrors(new ArrayList<String>());
        }

        if (PojoUtil.isEmpty(ssoCredentials.getClientDescription())) {
            if (ssoCredentials.getClientDescription() == null) {
                ssoCredentials.setClientDescription(new Text());
            }
            ssoCredentials.getClientDescription().setErrors(Arrays.asList(getMessage("manage.developer_tools.description_not_empty")));
            hasErrors = true;
        } else if(OrcidStringUtils.hasHtml(ssoCredentials.getClientDescription().getValue())) {
            ssoCredentials.getClientDescription().setErrors(Arrays.asList(getMessage("manage.developer_tools.description.html")));
            hasErrors = true;
        } else {
            ssoCredentials.getClientDescription().setErrors(new ArrayList<String>());
        }

        if (PojoUtil.isEmpty(ssoCredentials.getClientWebsite())) {
            if (ssoCredentials.getClientWebsite() == null) {
                ssoCredentials.setClientWebsite(new Text());
            }
            ssoCredentials.getClientWebsite().setErrors(Arrays.asList(getMessage("manage.developer_tools.website_not_empty")));
            hasErrors = true;
        } else {
            List<String> errors = new ArrayList<String>();
            String[] schemes = { "http", "https", "ftp" };
            UrlValidator urlValidator = new UrlValidator(schemes);
            String websiteString = ssoCredentials.getClientWebsite().getValue();
            if (!urlValidator.isValid(websiteString))
                websiteString = "http://" + websiteString;

            // test validity again
            if (!urlValidator.isValid(websiteString)) {
                errors.add(getMessage("manage.developer_tools.invalid_website"));
            }
            ssoCredentials.getClientWebsite().setErrors(errors);
        }

        if (redirectUris == null || redirectUris.isEmpty()) {
            List<String> errors = new ArrayList<String>();
            errors.add(getMessage("manage.developer_tools.at_least_one"));
            ssoCredentials.setErrors(errors);
            hasErrors = true;
        } else {
            for (RedirectUri redirectUri : redirectUris) {
                List<String> errors = validateRedirectUri(redirectUri);
                if (errors != null) {
                    redirectUri.setErrors(errors);
                    hasErrors = true;
                }
            }
        }
        return hasErrors;
    }

    /**
     * Checks if a redirect uri contains a valid URI associated to it
     * 
     * @param redirectUri
     * @return null if there are no errors, an List of strings containing error
     *         messages if any error happens
     * */
    private List<String> validateRedirectUri(RedirectUri redirectUri) {
        List<String> errors = null;
        String[] schemes = { "http", "https" };
        UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
        if (!PojoUtil.isEmpty(redirectUri.getValue())) {
            try {
                String redirectUriString = redirectUri.getValue().getValue();
                if (!urlValidator.isValid(redirectUriString)) {
                    errors = new ArrayList<String>();
                    errors.add(getMessage("manage.developer_tools.invalid_redirect_uri"));
                }
            } catch (NullPointerException npe) {
                errors = new ArrayList<String>();
                errors.add(getMessage("manage.developer_tools.empty_redirect_uri"));
            }
        } else {
            errors = new ArrayList<String>();
            errors.add(getMessage("manage.developer_tools.empty_redirect_uri"));
        }

        return errors;
    }

    /**
     * Enable developer tools on the current profile
     * 
     * @return true if the developer tools where enabled on the profile
     * */
    @RequestMapping(value = "/enable-developer-tools.json", method = RequestMethod.POST)
    public @ResponseBody
    boolean enableDeveloperTools(HttpServletRequest request) {
        OrcidProfile profile = getEffectiveProfile();
        boolean updated = true;
        if (profile.getOrcidInternal() != null && profile.getOrcidInternal().getPreferences() != null
                && profile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled() != null
                && !profile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled().isValue()) {
            updated = profileEntityManager.enableDeveloperTools(profile);
        }
        return updated;
    }

    /**
     * Disable developer tools on the current profile
     * 
     * @return true if the developer tools were disabled on the profile
     * */
    @RequestMapping(value = "/disable-developer-tools.json", method = RequestMethod.POST)
    public @ResponseBody
    boolean disableDeveloperTools(HttpServletRequest request) {
        OrcidProfile profile = getEffectiveProfile();
        boolean updated = true;
        if (profile.getOrcidInternal() != null && profile.getOrcidInternal().getPreferences() != null
                && profile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled() != null
                && profile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled().isValue()) {
            // Disable the developer tools
            updated = profileEntityManager.disableDeveloperTools(profile);
            // Disable the sso access
            orcidSSOManager.revokeSSOAccess(profile.getOrcidIdentifier().getPath());
        }
        return updated;
    }
    
    @ModelAttribute("hasVerifiedEmail")
    public boolean hasVerifiedEmail() {
        OrcidProfile profile = getEffectiveProfile();
        if (profile == null  || profile.getOrcidBio() == null || profile.getOrcidBio().getContactDetails() == null) return false;
        return profile.getOrcidBio().getContactDetails().anyEmailVerified();
    } 
}
