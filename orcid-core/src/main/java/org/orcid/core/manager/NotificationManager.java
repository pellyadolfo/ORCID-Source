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
package org.orcid.core.manager;

import java.util.List;

import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.notification.Notification;
import org.orcid.jaxb.model.notification.amended.AmendedSection;

public interface NotificationManager {

    // void sendRegistrationEmail(RegistrationEntity registration, URI baseUri);

    void sendWelcomeEmail(OrcidProfile orcidProfile, String email);

    void sendVerificationEmail(OrcidProfile orcidProfile, String email);

    public void sendVerificationReminderEmail(OrcidProfile orcidProfile, String email);

    void sendPasswordResetEmail(String toEmail, OrcidProfile orcidProfile);

    public String createVerificationUrl(String email, String baseUri);

    public String deriveEmailFriendlyName(OrcidProfile orcidProfile);

    void sendNotificationToAddedDelegate(OrcidProfile grantingUser, List<DelegationDetails> delegatesGrantedByUser);

    void sendAmendEmail(OrcidProfile amendedProfile, AmendedSection amendedSection);

    void sendOrcidDeactivateEmail(OrcidProfile orcidToDeactivate);

    void sendApiRecordCreationEmail(String toEmail, OrcidProfile createdProfile);

    void sendEmailAddressChangedNotification(OrcidProfile updatedProfile, Email oldEmail);

    void sendClaimReminderEmail(OrcidProfile orcidProfile, int daysUntilActivation);

    public boolean sendPrivPolicyEmail2014_03(OrcidProfile orcidProfile);

    void sendDelegationRequestEmail(OrcidProfile managed, OrcidProfile trusted, String link);

    public List<Notification> findUnsentByOrcid(String orcid);

    public List<Notification> findByOrcid(String orcid, boolean includeArchived, int firstResult, int maxResults);

    public Notification findById(Long id);

    public Notification findByOrcidAndId(String orcid, Long id);

    public Notification createNotification(String orcid, Notification notification);

    public Notification flagAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException;
    
    public Notification setActionedDate(String orcid, Long id);

}
