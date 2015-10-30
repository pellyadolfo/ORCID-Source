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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.spring.web.social.config.SocialType;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.plus.Person;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Shobhit Tyagi
 */
@Controller
@RequestMapping("/social")
public class SocialController extends BaseController {

	@Autowired
	private SocialContext socialContext;

	@Resource
	private EmailDao emailDao;

	@Resource
	private AuthenticationManager authenticationManager;

	@Resource
	private UserConnectionDao userConnectionDao;

	@Resource
	private OrcidSecurityManager securityMgr;

	private String providerUserId;

	@RequestMapping(value = { "/access" }, method = RequestMethod.GET)
	public ModelAndView signinHandler(HttpServletRequest request, HttpServletResponse response) {

		String emailId = null;
		SocialType connectionType = socialContext.isSignedIn(request, response);
		if (connectionType != null) {
			emailId = retrieveEmail(connectionType);
		}

		if (emailId == null) {
			throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
		} else {
			String providerId = connectionType.value();
			String userId = socialContext.getUserId();
				
			UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserId(providerUserId, providerId);
			if(userConnectionEntity != null) {
				if(userConnectionEntity.isLinked()) {
					UserconnectionPK pk = new UserconnectionPK(userId, providerId, providerUserId);
					userConnectionDao.updateLoginInformation(emailId, userConnectionEntity.getOrcid(), pk);
					String aCredentials = new StringBuffer(providerId).append(":").append(providerUserId).toString();
					PreAuthenticatedAuthenticationToken token = 
							new PreAuthenticatedAuthenticationToken(userConnectionEntity.getOrcid(), aCredentials);
					token.setDetails(new WebAuthenticationDetails(request));
					Authentication authentication = authenticationManager.authenticate(token);
					SecurityContextHolder.getContext().setAuthentication(authentication);
					return new ModelAndView("redirect:/my-orcid");
				} else {
					ModelAndView mav = new ModelAndView();
					logoutCurrentUser(request, response);
					mav.setViewName("social_link_signin");
					mav.addObject("providerId", providerId);
					mav.addObject("emailId", emailId);
					return mav;
				}
			} else {
				throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
			}
		}
	}

	@RequestMapping(value = { "/link" }, method = RequestMethod.GET)
	public ModelAndView link(HttpServletRequest request, HttpServletResponse response) {
		SocialType connectionType = socialContext.isSignedIn(request, response);
		String email = null;
		if (connectionType != null) {
			email = retrieveEmail(connectionType);
		}
		if(email != null) {
			String providerId = connectionType.value();
			UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserId(providerUserId, providerId);
			if(userConnectionEntity != null ) {
				if (!userConnectionEntity.isLinked()) {
					OrcidProfile profile = getRealProfile();
					userConnectionEntity.setLinked(true);
					userConnectionEntity.setEmail(email);
			        userConnectionEntity.setOrcid(profile.getOrcidIdentifier().getPath());
					userConnectionDao.merge(userConnectionEntity);
				}
				return new ModelAndView("redirect:/my-orcid");
			} else {
				throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
			}

		} else {
			throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
		}
	}

	private String retrieveEmail(SocialType connectionType) {
		String email = null;
		if (SocialType.FACEBOOK.equals(connectionType)) {
			Facebook facebook = socialContext.getFacebook();
			User user = facebook.fetchObject("me", User.class, "id", "email");
			providerUserId = user.getId();
			email = user.getEmail();
		} else if (SocialType.GOOGLE.equals(connectionType)) {
			Google google = socialContext.getGoogle();
			Person person = google.plusOperations().getGoogleProfile();
			providerUserId = person.getId();
			email = person.getAccountEmail();
		}

		return email;
	}
}
