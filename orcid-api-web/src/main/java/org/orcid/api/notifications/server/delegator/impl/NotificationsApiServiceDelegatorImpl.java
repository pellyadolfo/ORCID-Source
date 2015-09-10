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
package org.orcid.api.notifications.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.notifications.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.exception.OrcidNotificationNotFoundException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.NotificationValidationManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.notification.Notification;
import org.orcid.jaxb.model.notification.permission.NotificationPermission;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Will Simpson
 *
 */
@Component
public class NotificationsApiServiceDelegatorImpl implements NotificationsApiServiceDelegator {

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private NotificationValidationManager notificationValidationManager;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private LocaleManager localeManager;

    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findPermissionNotifications(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findPermissionNotification(String orcid, Long id) {
        Notification notification = notificationManager.findByOrcidAndId(orcid, id);
        if (notification != null) {
            checkSource(notification);
            return Response.ok(notification).build();
        } else {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", orcid);
            params.put("id", String.valueOf(id));
            throw new OrcidNotificationNotFoundException(params);
        }
    }

    private void checkSource(Notification notification) {
        String notificationSourceId = notification.getSource().retrieveSourcePath();
        String currentSourceId = sourceManager.retrieveSourceOrcid();
        if (!notificationSourceId.equals(currentSourceId)) {
            Object params[] = { currentSourceId };
            throw new AccessControlException(localeManager.resolveMessage("apiError.notification_accesscontrol.exception", params));
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response flagNotificationAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException {
        Notification notification = notificationManager.flagAsArchived(orcid, id);
        if (notification == null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", orcid);
            params.put("id", String.valueOf(id));
            throw new OrcidNotificationNotFoundException(params);
        }
        return Response.ok(notification).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response addPermissionNotification(UriInfo uriInfo, String orcid, NotificationPermission notification) {
        notificationValidationManager.validateNotificationPermission(notification);
        Notification createdNotification = notificationManager.createNotification(orcid, notification);
        try {
            return Response.created(new URI(uriInfo.getAbsolutePath() + "/" + createdNotification.getPutCode())).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.notification_uri.exception"), e);
        }
    }

}
