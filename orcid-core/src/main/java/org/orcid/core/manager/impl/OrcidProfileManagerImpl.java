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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.constants.DefaultPreferences;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.OrcidIndexManager;
import org.orcid.core.manager.OrcidJaxbCopyManager;
import org.orcid.core.manager.OrcidProfileCleaner;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorOrcid;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DeactivationDate;
import org.orcid.jaxb.model.message.DeveloperToolsEnabled;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.EncryptedPassword;
import org.orcid.jaxb.model.message.EncryptedSecurityAnswer;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidPreferences;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.jaxb.model.message.SendAdministrativeChangeNotifications;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceClientId;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.VisibilityType;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.notification.amended_rc2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_rc2.Item;
import org.orcid.jaxb.model.notification.permission_rc2.ItemType;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.EmailEventEntity;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import net.sf.ehcache.Element;

/**
 * The profile manager is responsible for passing onto the persistence layer
 * after performing any data manipulation necessary before doing so.
 * <p/>
 * One of its functions is to persist visibilty options set by the user and/or
 * client. As this is VERY sensitive it is implied that any security has been
 * performed already, thus if a method is called within this class it is will
 * first check for visibility within the updated object, and persist them. If
 * none are found in the updated object, the existing visibility options are
 * used. If neither are present, the defaults are used.
 * 
 * @author Declan Newman and Will Simpson
 */

public class OrcidProfileManagerImpl extends OrcidProfileManagerReadOnlyImpl implements OrcidProfileManager {

    @Resource
    private OrcidGenerationManager orcidGenerationManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private GenericDao<OrgAffiliationRelationEntity, Long> orgAffilationRelationDao;

    @Resource
    ProfileFundingDao profileFundingDao;

    @Resource
    private EmailDao emailDao;

    @Resource
    private GivenPermissionToDao givenPermissionToDao;

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    @Resource
    private OrcidIndexManager orcidIndexManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private OrcidProfileCleaner orcidProfileCleaner;

    @Resource
    private GenericDao<EmailEventEntity, Long> emailEventDao;

    @Resource
    private OrcidJaxbCopyManager orcidJaxbCopyManager;

    @Resource
    private WorkDao workDao;

    @Resource
    private OrgManager orgManager;

    @Value("${org.orcid.core.works.compare.useScopusWay:false}")
    private boolean compareWorksUsingScopusWay;

    private int claimReminderAfterDays = 8;

    private int verifyReminderAfterDays = 7;

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    private int numberOfIndexingThreads;

    private static final int INDEXING_BATCH_SIZE = 100;

    public void setOrcidIndexManager(OrcidIndexManager orcidIndexManager) {
        this.orcidIndexManager = orcidIndexManager;
    }

    public void setNumberOfIndexingThreads(int numberOfIndexingThreads) {
        this.numberOfIndexingThreads = numberOfIndexingThreads;
    }

    public void setClaimReminderAfterDays(int claimReminderAfterDays) {
        this.claimReminderAfterDays = claimReminderAfterDays;
    }

    @Override
    public void setCompareWorksUsingScopusWay(boolean compareWorksUsingScopusWay) {
        this.compareWorksUsingScopusWay = compareWorksUsingScopusWay;
    }

    @Override
    @Transactional
    public OrcidProfile createOrcidProfile(OrcidProfile orcidProfile, boolean createdByMember, boolean usedCaptcha) {
        if (orcidProfile.getOrcidIdentifier() == null) {
            orcidProfile.setOrcidIdentifier(orcidGenerationManager.createNewOrcid());
        }

        // Add source to works and affiliations
        String amenderOrcid = sourceManager.retrieveSourceOrcid();

        addSourceToEmails(orcidProfile, amenderOrcid);
        addSourceToWorks(orcidProfile, amenderOrcid);
        addSourceToAffiliations(orcidProfile, amenderOrcid);
        addSourceToFundings(orcidProfile, amenderOrcid);

        ProfileEntity profileEntity = adapter.toProfileEntity(orcidProfile);
        profileEntity.setUsedRecaptchaOnRegistration(usedCaptcha);
        encryptAndMapFieldsForProfileEntityPersistence(orcidProfile, profileEntity);
        profileEntity.setAuthorities(getGrantedAuthorities(profileEntity));
        setDefaultVisibility(profileEntity, createdByMember);

        profileDao.persist(profileEntity);
        profileDao.flush();
        OrcidProfile updatedTranslatedOrcid = adapter.toOrcidProfile(profileEntity);
        return updatedTranslatedOrcid;
    }

    @Override
    public OrcidProfile createOrcidProfileAndNotify(OrcidProfile orcidProfile) {
        OrcidProfile createdOrcidProfile = createOrcidProfile(orcidProfile, true, false);
        notificationManager.sendApiRecordCreationEmail(orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(), orcidProfile);
        return createdOrcidProfile;
    }

    @Override
    @Transactional
    public OrcidProfile updateOrcidProfile(OrcidProfile orcidProfile) {
        String amenderOrcid = sourceManager.retrieveSourceOrcid();
        ProfileEntity existingProfileEntity = profileDao.find(orcidProfile.getOrcidIdentifier().getPath());

        if (existingProfileEntity != null) {
            profileDao.removeChildrenWithGeneratedIds(existingProfileEntity);
            setWorkPrivacy(orcidProfile, existingProfileEntity.getActivitiesVisibilityDefault());
            setAffiliationPrivacy(orcidProfile, existingProfileEntity.getActivitiesVisibilityDefault());
            setFundingPrivacy(orcidProfile, existingProfileEntity.getActivitiesVisibilityDefault());
        }
        dedupeWorks(orcidProfile);
        dedupeAffiliations(orcidProfile);
        dedupeFundings(orcidProfile);
        addSourceToEmails(orcidProfile, existingProfileEntity, amenderOrcid);
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidProfile, existingProfileEntity);
        profileEntity.setLastModified(new Date());
        profileEntity.setIndexingStatus(IndexingStatus.PENDING);
        ProfileEntity updatedProfileEntity = profileDao.merge(profileEntity);

        profileDao.flush();
        profileDao.refresh(updatedProfileEntity);
        OrcidProfile updatedOrcidProfile = convertToOrcidProfile(updatedProfileEntity, LoadOptions.ALL);

        orcidProfileCacheManager.put(updatedOrcidProfile);
        return updatedOrcidProfile;
    }

    /**
     * Preserves existing source for existing emails, and adds specified source
     * for new emails
     * 
     * @param orcidProfile
     *            The incoming profile
     * @param existingProfileEntity
     *            The existing profile entity from the DB
     * @param amenderOrcid
     *            The source of new emails (from the security context)
     */
    private void addSourceToEmails(OrcidProfile orcidProfile, ProfileEntity existingProfileEntity, String amenderOrcid) {
        Map<String, EmailEntity> existingMap = new HashMap<>();
        Set<EmailEntity> existingEmails = existingProfileEntity.getEmails();
        if (existingEmails != null) {
            existingMap = EmailEntity.mapByLowerCaseEmail(existingEmails);
        }
        OrcidBio orcidBio = orcidProfile.getOrcidBio();
        if (orcidBio != null) {
            ContactDetails contactDetails = orcidBio.getContactDetails();
            if (contactDetails != null) {
                for (Email email : contactDetails.getEmail()) {
                    EmailEntity existingEmail = existingMap.get(email.getValue().toLowerCase());
                    if (existingEmail == null) {
                        if (OrcidStringUtils.isValidOrcid(amenderOrcid)) {
                            email.setSourceClientId(amenderOrcid);
                        } else {
                            email.setSource(amenderOrcid);
                        }
                    } else {
                        SourceEntity existingSource = existingEmail.getSource();
                        if (existingSource != null) {
                            email.setSource(existingSource.getSourceId());
                        }
                    }
                }
            }
        }
    }

    /**
     * Add source to the profile emails
     * 
     * @param orcidProfile
     *            The profile
     * @param amenderOrcid
     *            The orcid of the user or client that add the email to the
     *            profile user
     * */
    private void addSourceToEmails(OrcidProfile orcidProfile, String amenderOrcid) {
        if (orcidProfile != null && orcidProfile.getOrcidBio() != null && orcidProfile.getOrcidBio().getContactDetails() != null
                && orcidProfile.getOrcidBio().getContactDetails().getEmail() != null) {
            for (Email email : orcidProfile.getOrcidBio().getContactDetails().getEmail()) {
                if (OrcidStringUtils.isValidOrcid(amenderOrcid)) {
                    email.setSource(amenderOrcid);
                } else {
                    email.setSourceClientId(amenderOrcid);
                }
            }
        }
    }

    /**
     * Add source to the profile works
     * 
     * @param orcidProfile
     *            The profile
     * @param amenderOrcid
     *            The orcid of the user or client that add the work to the
     *            profile user
     * */
    private void addSourceToWorks(OrcidProfile orcidProfile, String amenderOrcid) {
        OrcidWorks orcidWorks = orcidProfile.getOrcidActivities() == null ? null : orcidProfile.getOrcidActivities().getOrcidWorks();
        addSourceToWorks(orcidWorks, amenderOrcid);
    }

    private void addSourceToWorks(OrcidWorks orcidWorks, String amenderOrcid) {
        if (orcidWorks != null && !orcidWorks.getOrcidWork().isEmpty() && amenderOrcid != null) {
            for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                Source source = createSource(amenderOrcid);
                orcidWork.setSource(source);
            }
        }
    }

    private Source createSource(String amenderOrcid) {
        Source source = new Source();
        if (OrcidStringUtils.isValidOrcid(amenderOrcid)) {
            source.setSourceOrcid(new SourceOrcid(amenderOrcid));
            source.setSourceClientId(null);
        } else {
            source.setSourceClientId(new SourceClientId(amenderOrcid));
            source.setSourceOrcid(null);
        }
        return source;
    }

    /**
     * Add source to the affiliations
     * 
     * @param orcidProfile
     *            The profile
     * @param amenderOrcid
     *            The orcid of the user or client that is adding the affiliation
     *            to the profile user
     * */
    private void addSourceToAffiliations(OrcidProfile orcidProfile, String amenderOrcid) {
        Affiliations affiliations = orcidProfile.getOrcidActivities() == null ? null : orcidProfile.getOrcidActivities().getAffiliations();

        if (affiliations != null && !affiliations.getAffiliation().isEmpty()) {
            for (Affiliation affiliation : affiliations.getAffiliation()) {
                if (affiliation.getSource() == null || StringUtils.isEmpty(affiliation.retrieveSourcePath()))
                    affiliation.setSource(new Source(amenderOrcid));
            }
        }

    }

    public boolean exists(String orcid) {
        return profileDao.exists(orcid);
    }

    /**
     * Add source to the fundings
     * 
     * @param orcidProfile
     *            The profile
     * @param amenderOrcid
     *            The orcid of the user or client that is adding the fundings to
     *            the profile user
     * */
    private void addSourceToFundings(OrcidProfile orcidProfile, String amenderOrcid) {
        FundingList fundings = orcidProfile.getOrcidActivities() == null ? null : orcidProfile.getOrcidActivities().getFundings();

        if (fundings != null && !fundings.getFundings().isEmpty()) {
            for (Funding funding : fundings.getFundings()) {
                if (funding.getSource() == null || StringUtils.isEmpty(funding.retrieveSourcePath()))
                    funding.setSource(new Source(amenderOrcid));
            }
        }

    }

    private void setWorkPrivacy(OrcidProfile updatedOrcidProfile, Visibility defaultWorkVisibility) {
        OrcidHistory orcidHistory = updatedOrcidProfile.getOrcidHistory();
        boolean isClaimed = orcidHistory != null ? orcidHistory.getClaimed().isValue() : false;
        OrcidActivities incomingActivities = updatedOrcidProfile.getOrcidActivities();
        if (incomingActivities != null) {
            OrcidWorks incomingWorks = incomingActivities.getOrcidWorks();
            if (incomingWorks != null) {
                setWorkPrivacy(incomingWorks, defaultWorkVisibility, isClaimed);
            }
        }
    }

    private void setWorkPrivacy(OrcidWorks incomingWorks, Visibility defaultWorkVisibility, boolean isClaimed) {
        for (OrcidWork incomingWork : incomingWorks.getOrcidWork()) {
            if (StringUtils.isBlank(incomingWork.getPutCode())) {
                Visibility incomingWorkVisibility = incomingWork.getVisibility();
                if (isClaimed) {
                    if (defaultWorkVisibility.isMoreRestrictiveThan(incomingWorkVisibility)) {
                        incomingWork.setVisibility(defaultWorkVisibility);
                    }
                } else if (incomingWorkVisibility == null) {
                    incomingWork.setVisibility(Visibility.PRIVATE);
                }
            }
        }
    }

    @Override
    @Transactional
    public OrcidProfile retrieveOrcidProfileByEmail(String email) {
        return retrieveOrcidProfileByEmail(email, LoadOptions.ALL);
    }

    @Override
    @Transactional
    public OrcidProfile retrieveOrcidProfileByEmail(String email, LoadOptions loadOptions) {
        EmailEntity emailEntity = emailDao.findCaseInsensitive(email);
        if (emailEntity != null) {
            ProfileEntity profileEntity = emailEntity.getProfile();
            OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity, loadOptions);
            String verificationCode = profileEntity.getEncryptedVerificationCode();
            String securityAnswer = profileEntity.getEncryptedSecurityAnswer();
            orcidProfile.setVerificationCode(decrypt(verificationCode));
            orcidProfile.setSecurityQuestionAnswer(decrypt(securityAnswer));
            return orcidProfile;
        } else {
            return null;
        }
    }

    /**
     * Retrieves the orcid profile given an identifier, without any personal
     * internal data.
     * <p/>
     * Specifically, this is for use by tier 1
     * 
     * @param orcid
     *            the identifier
     * @return the full orcid profile
     */
    @Override
    @Transactional
    public OrcidProfile retrieveOrcidProfileWithNoInternal(String orcid) {
        ProfileEntity profileEntity = profileDao.find(orcid);
        if (profileEntity != null) {
            return adapter.toOrcidProfile(profileEntity);
        } else {
            return null;
        }
    }

    /**
     * Updates the ORCID works only
     * 
     * @param updatedOrcidProfile
     * @return
     */
    @Override
    @Transactional
    public OrcidProfile updateOrcidWorks(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcidIdentifier().getPath());
        if (existingProfile == null) {
            return null;
        }
        OrcidActivities updatedActivities = updatedOrcidProfile.getOrcidActivities();
        if (updatedActivities == null) {
            return null;
        }
        OrcidWorks updatedOrcidWorks = updatedActivities.getOrcidWorks();
        if (updatedOrcidWorks == null) {
            return null;
        }
        OrcidActivities existingActivities = existingProfile.getOrcidActivities();
        if (existingActivities == null) {
            existingActivities = new OrcidActivities();
            existingProfile.setOrcidActivities(existingActivities);
        }
        OrcidWorks existingOrcidWorks = existingActivities.getOrcidWorks();
        if (existingOrcidWorks == null) {
            existingOrcidWorks = new OrcidWorks();
            existingActivities.setOrcidWorks(existingOrcidWorks);
        }
        orcidJaxbCopyManager.copyUpdatedWorksPreservingVisbility(existingProfile.retrieveOrcidWorks(), updatedOrcidProfile.retrieveOrcidWorks());
        OrcidProfile profileToReturn = updateOrcidProfile(existingProfile);
        notificationManager.sendAmendEmail(profileToReturn, AmendedSection.WORK);
        return profileToReturn;
    }

    /**
     * Add new external identifiers to an existing profile
     * 
     * @param updatedOrcidProfile
     * @return
     */
    @Override
    @Transactional
    public OrcidProfile addExternalIdentifiers(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcidIdentifier().getPath());

        if (existingProfile != null && existingProfile.getOrcidBio() != null) {
            OrcidBio orcidBio = existingProfile.getOrcidBio();
            ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();

            if (externalIdentifiers == null) {
                orcidBio.setExternalIdentifiers(new ExternalIdentifiers());
            }
            ExternalIdentifiers externalIdentifier = updatedOrcidProfile.getOrcidBio().getExternalIdentifiers();
            List<ExternalIdentifier> updatedExternalIdentifiers = externalIdentifier.getExternalIdentifier();
            List<ExternalIdentifier> existingExternalIdentifiers = orcidBio.getExternalIdentifiers().getExternalIdentifier();

            // Copy all the existing external identifiers to the updated profile
            for (ExternalIdentifier ei : existingExternalIdentifiers) {
                updatedExternalIdentifiers.add(ei);
            }

            orcidJaxbCopyManager.copyUpdatedExternalIdentifiersToExistingPreservingVisibility(orcidBio, updatedOrcidProfile.getOrcidBio());

            OrcidProfile profileToReturn = updateOrcidProfile(existingProfile);
            notificationManager.sendAmendEmail(profileToReturn, AmendedSection.EXTERNAL_IDENTIFIERS);
            return profileToReturn;
        } else {
            return null;
        }
    }

    /**
     * Updates the ORCID bio data
     * 
     * @param updatedOrcidProfile
     * @return
     */
    @Override
    @Transactional
    public OrcidProfile updateOrcidBio(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcidIdentifier().getPath());
        if (existingProfile == null) {
            return null;
        }
        // preserve the visibility settings
        orcidJaxbCopyManager.copyUpdatedBioToExistingWithVisibility(existingProfile.getOrcidBio(), updatedOrcidProfile.getOrcidBio());
        OrcidProfile profileToReturn = updateOrcidProfile(existingProfile);
        notificationManager.sendAmendEmail(profileToReturn, AmendedSection.BIO);
        return profileToReturn;
    }

    @Override
    @Transactional
    public OrcidProfile updateAffiliations(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcidIdentifier().getPath());
        if (existingProfile == null) {
            return null;
        }
        OrcidActivities updatedActivities = updatedOrcidProfile.getOrcidActivities();
        if (updatedActivities == null) {
            return null;
        }
        Affiliations updatedAffiliations = updatedActivities.getAffiliations();
        if (updatedAffiliations == null) {
            return null;
        }
        OrcidActivities existingActivities = existingProfile.getOrcidActivities();
        if (existingActivities == null) {
            existingActivities = new OrcidActivities();
            existingProfile.setOrcidActivities(existingActivities);
        }
        Affiliations existingAffiliations = existingActivities.getAffiliations();
        if (existingAffiliations == null) {
            existingAffiliations = new Affiliations();
            existingActivities.setAffiliations(existingAffiliations);
        }

        orcidJaxbCopyManager.copyAffiliationsToExistingPreservingVisibility(existingAffiliations, updatedAffiliations);
        OrcidProfile profileToReturn = updateOrcidProfile(existingProfile);
        notificationManager.sendAmendEmail(profileToReturn, AmendedSection.AFFILIATION);
        return profileToReturn;
    }

    @Override
    @Transactional
    public OrcidProfile updateFundings(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcidIdentifier().getPath());
        if (existingProfile == null) {
            return null;
        }
        FundingList updatedFundingList = updatedOrcidProfile.retrieveFundings();
        if (updatedFundingList == null) {
            return null;
        } else {
            // Parse the amount in the new funding
            setFundingAmountsWithTheCorrectFormat(updatedOrcidProfile);
            // Update the funding list with the new values
            updatedFundingList = updatedOrcidProfile.retrieveFundings();
        }
        OrcidActivities existingActivities = existingProfile.getOrcidActivities();
        if (existingActivities == null) {
            existingActivities = new OrcidActivities();
            existingProfile.setOrcidActivities(existingActivities);
        }
        FundingList existingFundingList = existingActivities.getFundings();
        if (existingFundingList == null) {
            existingFundingList = new FundingList();
            existingActivities.setFundings(existingFundingList);
        }

        orcidJaxbCopyManager.copyFundingListToExistingPreservingVisibility(existingFundingList, updatedFundingList);
        OrcidProfile profileToReturn = updateOrcidProfile(existingProfile);
        notificationManager.sendAmendEmail(profileToReturn, AmendedSection.FUNDING);
        return profileToReturn;
    }

    @Override
    @Transactional
    public void updatePasswordInformation(OrcidProfile updatedOrcidProfile) {
        String orcid = updatedOrcidProfile.getOrcidIdentifier().getPath();
        String hashedPassword = hash(updatedOrcidProfile.getPassword());
        profileDao.updateEncryptedPassword(orcid, hashedPassword);
        OrcidProfile cachedProfile = orcidProfileCacheManager.retrieve(orcid);
        if (cachedProfile != null) {
            profileDao.flush();
            SecurityDetails securityDetails = initSecurityDetails(cachedProfile);
            securityDetails.setEncryptedPassword(new EncryptedPassword(hashedPassword));
            cachedProfile.setPassword(hashedPassword);
            orcidProfileCacheManager.put(cachedProfile);
        }
        updateSecurityQuestionInformation(updatedOrcidProfile);
    }

    private SecurityDetails initSecurityDetails(OrcidProfile cachedProfile) {
        OrcidInternal internal = cachedProfile.getOrcidInternal();
        if (internal == null) {
            internal = new OrcidInternal();
            cachedProfile.setOrcidInternal(internal);
        }
        SecurityDetails securityDetails = internal.getSecurityDetails();
        if (securityDetails == null) {
            securityDetails = new SecurityDetails();
            internal.setSecurityDetails(securityDetails);
        }
        return securityDetails;
    }

    @Override
    public void updateSecurityQuestionInformation(OrcidProfile updatedOrcidProfile) {
        String orcid = updatedOrcidProfile.getOrcidIdentifier().getPath();
        SecurityQuestionId securityQuestionId = updatedOrcidProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId();
        Integer questionId = null;
        if (securityQuestionId != null) {
            questionId = new Long(securityQuestionId.getValue()).intValue();
        }
        String unencryptedAnswer = updatedOrcidProfile.getSecurityQuestionAnswer();
        String encryptedAnswer = encrypt(unencryptedAnswer);
        profileDao.updateSecurityQuestion(orcid, questionId, questionId != null ? encryptedAnswer : null);
        OrcidProfile cachedProfile = orcidProfileCacheManager.retrieve(orcid);
        if (cachedProfile != null) {
            profileDao.flush();
            SecurityDetails securityDetails = initSecurityDetails(cachedProfile);
            securityDetails.setSecurityQuestionId(questionId != null ? new SecurityQuestionId(questionId) : null);
            securityDetails.setEncryptedSecurityAnswer(encryptedAnswer != null ? new EncryptedSecurityAnswer(encryptedAnswer) : null);
            cachedProfile.setSecurityQuestionAnswer(encryptedAnswer != null ? unencryptedAnswer : null);
            orcidProfileCacheManager.put(cachedProfile);
        }
    }

    @Override
    @Transactional
    public void updateCountry(OrcidProfile orcidProfile) {
        profileDao.updateCountry(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().getValue(),
                orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().getVisibility());
    }

    @Override
    @Transactional
    public void updateBiography(OrcidProfile orcidProfile) {
        profileDao.updateBiography(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getBiography().getContent(), orcidProfile.getOrcidBio()
                .getBiography().getVisibility());
    }

    @Override
    @Transactional    
    public void updateNames(String orcid, org.orcid.jaxb.model.record_rc2.PersonalDetails personalDetails) {
        String givenNames = personalDetails.getName().getGivenNames() != null ? personalDetails.getName().getGivenNames().getContent() : null;
        String familyName = personalDetails.getName().getFamilyName() != null ? personalDetails.getName().getFamilyName().getContent() : null;
        String creditName = personalDetails.getName().getCreditName() != null ? personalDetails.getName().getCreditName().getContent() : null;
        Visibility namesVisibility = personalDetails.getName().getVisibility() != null ? Visibility.fromValue(personalDetails.getName().getVisibility().value()) : null;
        profileDao.updateNames(orcid, givenNames, familyName, creditName, namesVisibility);
    }            

    @Override
    @Transactional
    public void updatePreferences(String orcid, Preferences preferences) {
        boolean sendChangeNotifications = preferences.getSendChangeNotifications() == null ? DefaultPreferences.SEND_CHANGE_NOTIFICATIONS_DEFAULT : preferences
                .getSendChangeNotifications().isValue();
        boolean sendAdministrativeChangeNotifications = preferences.getSendAdministrativeChangeNotifications() == null ? sendChangeNotifications : preferences
                .getSendAdministrativeChangeNotifications().isValue();
        boolean sendOrcidNews = preferences.getSendOrcidNews() == null ? DefaultPreferences.SEND_ORCID_NEWS_DEFAULT : preferences.getSendOrcidNews().isValue();
        boolean sendMemberUpdateRequests = preferences.getSendMemberUpdateRequests() == null ? DefaultPreferences.SEND_MEMBER_UPDATE_REQUESTS : preferences
                .getSendMemberUpdateRequests();
        Visibility activitiesVisibilityDefault = preferences.getActivitiesVisibilityDefault().getValue();
        boolean developerToolsEnabled = preferences.getDeveloperToolsEnabled() == null ? DefaultPreferences.DEVELOPER_TOOLS_ENABLED_DEFAULT : preferences
                .getDeveloperToolsEnabled().isValue();
        float sendEmailFrequencyDays = Float.valueOf(preferences.getSendEmailFrequencyDays() == null ? DefaultPreferences.SEND_EMAIL_FREQUENCY_DAYS : preferences
                .getSendEmailFrequencyDays());
        profileDao.updatePreferences(orcid, sendChangeNotifications, sendAdministrativeChangeNotifications, sendOrcidNews, sendMemberUpdateRequests,
                activitiesVisibilityDefault, developerToolsEnabled, sendEmailFrequencyDays);
        OrcidProfile cachedProfile = orcidProfileCacheManager.retrieve(orcid);
        if (cachedProfile != null) {
            profileDao.flush();
            Preferences cachedPreferences = cachedProfile.getOrcidInternal().getPreferences();
            cachedPreferences.setSendChangeNotifications(new SendChangeNotifications(sendChangeNotifications));
            cachedPreferences.setSendAdministrativeChangeNotifications(new SendAdministrativeChangeNotifications(sendAdministrativeChangeNotifications));
            cachedPreferences.setSendOrcidNews(new SendOrcidNews(sendOrcidNews));
            cachedPreferences.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(activitiesVisibilityDefault));
            cachedPreferences.setDeveloperToolsEnabled(new DeveloperToolsEnabled(developerToolsEnabled));
            cachedPreferences.setSendEmailFrequencyDays(preferences.getSendEmailFrequencyDays());
            orcidProfileCacheManager.put(cachedProfile);
        }
    }

    @Override
    @Transactional
    public OrcidProfile updateOrcidPreferences(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcidIdentifier().getPath());
        if (existingProfile == null) {
            return null;
        }

        existingProfile.setOrcidPreferences(updatedOrcidProfile.getOrcidPreferences());
        OrcidProfile profileToReturn = updateOrcidProfile(existingProfile);
        notificationManager.sendAmendEmail(profileToReturn, AmendedSection.PREFERENCES);
        return profileToReturn;
    }

    @Override
    @Transactional
    public void addOrcidWorks(OrcidProfile updatedOrcidProfile) {
        String orcid = updatedOrcidProfile.getOrcidIdentifier().getPath();
        OrcidProfile existingProfile = retrieveOrcidProfile(orcid);
        if (existingProfile == null) {
            throw new IllegalArgumentException("No record found for " + orcid);
        }
        OrcidWorks existingOrcidWorks = existingProfile.retrieveOrcidWorks();
        OrcidWorks updatedOrcidWorks = updatedOrcidProfile.retrieveOrcidWorks();
        Visibility workVisibilityDefault = existingProfile.getOrcidInternal().getPreferences().getActivitiesVisibilityDefault().getValue();
        Boolean claimed = existingProfile.getOrcidHistory().isClaimed();
        setWorkPrivacy(updatedOrcidWorks, workVisibilityDefault, claimed == null ? false : claimed);
        String amenderOrcid = sourceManager.retrieveSourceOrcid();
        addSourceToWorks(updatedOrcidWorks, amenderOrcid);
        updatedOrcidWorks = dedupeWorks(updatedOrcidWorks);
        List<OrcidWork> updatedOrcidWorksList = updatedOrcidWorks.getOrcidWork();

        if (compareWorksUsingScopusWay) {
            checkForAlreadyExistingWorks(existingOrcidWorks, updatedOrcidWorksList);
            if (existingOrcidWorks != null)
                checkWorkExternalIdentifiersAreNotDuplicated(updatedOrcidWorksList, existingOrcidWorks.getOrcidWork());
            else
                checkWorkExternalIdentifiersAreNotDuplicated(updatedOrcidWorksList, null);
        } else {
            checkForAlreadyExistingWorksLegacyMode(existingOrcidWorks, updatedOrcidWorksList);
        }

        persistAddedWorks(orcid, updatedOrcidWorksList);
        profileDao.flush();

        boolean notificationsEnabled = existingProfile.getOrcidInternal().getPreferences().getNotificationsEnabled();
        if (notificationsEnabled) {
            List<Item> activities = new ArrayList<>();
            for (OrcidWork updatedWork : updatedOrcidWorksList) {
                Item activity = new Item();
                activity.setItemName(updatedWork.getWorkTitle().getTitle().getContent());
                activity.setItemType(ItemType.WORK);
                activity.setPutCode(updatedWork.getPutCode());
                activities.add(activity);
            }
            notificationManager.sendAmendEmail(existingProfile, AmendedSection.WORK, activities);
        }
    }

    /**
     * Legacy mode to check if works are duplicated TODO: This must be removed
     * in a near future
     * */
    private void checkForAlreadyExistingWorksLegacyMode(OrcidWorks existingOrcidWorks, List<OrcidWork> updatedOrcidWorksList) {
        if (existingOrcidWorks != null) {
            Set<OrcidWork> existingOrcidWorksSet = new HashSet<>();
            for (OrcidWork existingWork : existingOrcidWorks.getOrcidWork()) {
                existingOrcidWorksSet.add(existingWork);
            }
            for (Iterator<OrcidWork> updatedWorkIterator = updatedOrcidWorksList.iterator(); updatedWorkIterator.hasNext();) {
                OrcidWork updatedWork = updatedWorkIterator.next();
                for (OrcidWork orcidWork : existingOrcidWorksSet) {
                    if (orcidWork.isDuplicatedLegacyMode(updatedWork)) {
                        updatedWorkIterator.remove();
                        break;
                    }
                }
            }
        }
    }

    private void checkForAlreadyExistingWorks(OrcidWorks existingOrcidWorks, List<OrcidWork> updatedOrcidWorksList) {
        if (existingOrcidWorks != null) {
            Set<OrcidWork> existingOrcidWorksSet = new HashSet<>();
            for (OrcidWork existingWork : existingOrcidWorks.getOrcidWork()) {
                existingOrcidWorksSet.add(existingWork);
            }
            for (Iterator<OrcidWork> updatedWorkIterator = updatedOrcidWorksList.iterator(); updatedWorkIterator.hasNext();) {
                OrcidWork updatedWork = updatedWorkIterator.next();
                for (OrcidWork orcidWork : existingOrcidWorksSet) {
                    if (orcidWork.isDuplicated(updatedWork)) {
                        // Update the existing work
                        long workId = Long.valueOf(orcidWork.getPutCode());
                        WorkEntity workEntity = workDao.find(workId);
                        workEntity.clean();
                        workEntity = jaxb2JpaAdapter.getWorkEntity(updatedWork, workEntity);
                        workDao.persist(workEntity);
                        // Since it was already updated, remove it from the list
                        // of updated works
                        updatedWorkIterator.remove();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Checks if the list of updated works contains any duplicated external
     * identifier, if so, it will throw an exception The newOrcidWorksList MUST
     * be deduped before getting into this method
     * 
     * @param updatedOrcidWorksList
     *            the deduped list of works
     * @throws IllegalArgumentException
     *             if there is a duplicated external identifier
     * */
    public void checkWorkExternalIdentifiersAreNotDuplicated(List<OrcidWork> newOrcidWorksList, List<OrcidWork> existingWorkList) {
        // Rules to define if two works have the same id:
        // 1) If the source is the same and
        // 2) any of the ext id matches
        // 3) but the tile is different
        // Then, if both works are already existing: Log an error message
        // If any of the works is new, or both are new, throw an exception

        // First compare new works, to verify they don't share any ext id
        // Since the new works comes from a single request, we know they are
        // from the same source, so, we can skip the work source comparison
        if (newOrcidWorksList != null) {
            for (int i = 0; i < newOrcidWorksList.size(); i++) {
                OrcidWork newWork = newOrcidWorksList.get(i);
                for (int j = 0; j < newOrcidWorksList.size(); j++) {
                    // If they are not the same work
                    if (i != j) {
                        OrcidWork newWorkToCompare = newOrcidWorksList.get(j);
                        // If newWork have external identifiers
                        if (newWork.getWorkExternalIdentifiers() != null && newWork.getWorkExternalIdentifiers().getWorkExternalIdentifier() != null
                                && !newWork.getWorkExternalIdentifiers().getWorkExternalIdentifier().isEmpty()) {
                            // For each external id on the outer work
                            for (WorkExternalIdentifier workExtId : newWork.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
                                if (newWorkToCompare.getWorkExternalIdentifiers() != null
                                        && newWorkToCompare.getWorkExternalIdentifiers().getWorkExternalIdentifier() != null
                                        && !newWorkToCompare.getWorkExternalIdentifiers().getWorkExternalIdentifier().isEmpty()) {
                                    // Compare it against each external id in
                                    // the inner work
                                    for (WorkExternalIdentifier workExtIdToCompare : newWorkToCompare.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
                                        // If the ext ids are the same
                                        if (workExtId.equals(workExtIdToCompare)) {
                                            Title title = (newWork.getWorkTitle() == null || newWork.getWorkTitle().getTitle() == null) ? null : newWork.getWorkTitle()
                                                    .getTitle();
                                            Title titleToCompare = (newWorkToCompare.getWorkTitle() == null || newWorkToCompare.getWorkTitle().getTitle() == null) ? null
                                                    : newWorkToCompare.getWorkTitle().getTitle();
                                            if (!isTheSameTitle(title, titleToCompare)) {
                                                String extIdContent = (workExtId.getWorkExternalIdentifierId() == null || PojoUtil.isEmpty(workExtId
                                                        .getWorkExternalIdentifierId().getContent())) ? "" : workExtId.getWorkExternalIdentifierId().getContent();
                                                String title1 = (title == null) ? "" : title.getContent();
                                                String title2 = (titleToCompare == null) ? "" : titleToCompare.getContent();
                                                String errorMessage = String.format("Works \"%s\" and \"%s\" have the same external id \"%s\"", title1, title2,
                                                        extIdContent);
                                                throw new IllegalArgumentException(errorMessage);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Then, if it already have works
        if (existingWorkList != null && existingWorkList.size() > 0) {
            // Check for duplicates in existing works, if any is found, log it
            for (int i = 0; i < existingWorkList.size(); i++) {
                OrcidWork existingWork = existingWorkList.get(i);
                Source workSource = existingWork.getSource();
                for (int j = 0; j < existingWorkList.size(); j++) {
                    // If it is not the same index
                    if (i != j) {
                        OrcidWork existingWorkToCompare = existingWorkList.get(j);
                        Source workSourceToCompare = existingWorkToCompare.getSource();
                        // If both works have the same source
                        if (isTheSameSource(workSource, workSourceToCompare)) {
                            // If the work have external identifiers
                            if (existingWork.getWorkExternalIdentifiers() != null && existingWork.getWorkExternalIdentifiers().getWorkExternalIdentifier() != null
                                    && !existingWork.getWorkExternalIdentifiers().getWorkExternalIdentifier().isEmpty()) {
                                // Compare each external identifier
                                for (WorkExternalIdentifier workExtId : existingWork.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
                                    // If the workToCompare have ext ids
                                    if (existingWorkToCompare.getWorkExternalIdentifiers() != null
                                            && existingWorkToCompare.getWorkExternalIdentifiers().getWorkExternalIdentifier() != null
                                            && !existingWorkToCompare.getWorkExternalIdentifiers().getWorkExternalIdentifier().isEmpty()) {
                                        // Compare each ext ids, following the
                                        // rules:
                                        for (WorkExternalIdentifier workToCompareExtId : existingWorkToCompare.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
                                            // If the ext ids are the same
                                            if (workExtId.equals(workToCompareExtId)) {
                                                // Compare the titles, if they
                                                // are different, set it as
                                                // duplicated
                                                Title title = (existingWork.getWorkTitle() == null || existingWork.getWorkTitle().getTitle() == null) ? null
                                                        : existingWork.getWorkTitle().getTitle();
                                                Title titleToCompare = (existingWorkToCompare.getWorkTitle() == null || existingWorkToCompare.getWorkTitle().getTitle() == null) ? null
                                                        : existingWorkToCompare.getWorkTitle().getTitle();
                                                if (!isTheSameTitle(title, titleToCompare)) {
                                                    String extIdContent = (workExtId.getWorkExternalIdentifierId() == null || PojoUtil.isEmpty(workExtId
                                                            .getWorkExternalIdentifierId().getContent())) ? "" : workExtId.getWorkExternalIdentifierId().getContent();
                                                    LOG.error("Works {} and {} have the same external identifier {}", new Object[] { existingWork.getPutCode(),
                                                            existingWorkToCompare.getPutCode(), extIdContent });
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Check for duplicates between the existing works and the new works
            if (newOrcidWorksList != null) {
                for (OrcidWork orcidWork : newOrcidWorksList) {
                    Source workSource = orcidWork.getSource();
                    for (OrcidWork existingWork : existingWorkList) {
                        Source existingWorkSource = existingWork.getSource();
                        // If both works have the same source
                        if (isTheSameSource(workSource, existingWorkSource)) {
                            // If the new work have external identifiers
                            if (orcidWork.getWorkExternalIdentifiers() != null && orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier() != null
                                    && !orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier().isEmpty()) {
                                // For each external identifier in the new work
                                for (WorkExternalIdentifier newExternalIdentifier : orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
                                    if (existingWork.getWorkExternalIdentifiers() != null
                                            && existingWork.getWorkExternalIdentifiers().getWorkExternalIdentifier() != null
                                            && !existingWork.getWorkExternalIdentifiers().getWorkExternalIdentifier().isEmpty()) {
                                        // Compare them against the existing
                                        // identifiers
                                        for (WorkExternalIdentifier existingExternalIdentifier : existingWork.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
                                            // If the ext ids are the same
                                            if (newExternalIdentifier.equals(existingExternalIdentifier)) {
                                                // Compare the titles, if they
                                                // are different, set it as
                                                // duplicated
                                                Title title = (orcidWork.getWorkTitle() == null || orcidWork.getWorkTitle().getTitle() == null) ? null : orcidWork
                                                        .getWorkTitle().getTitle();
                                                Title titleToCompare = (existingWork.getWorkTitle() == null || existingWork.getWorkTitle().getTitle() == null) ? null
                                                        : existingWork.getWorkTitle().getTitle();
                                                if (!isTheSameTitle(title, titleToCompare)) {
                                                    String extIdContent = (existingExternalIdentifier.getWorkExternalIdentifierId() == null || PojoUtil
                                                            .isEmpty(existingExternalIdentifier.getWorkExternalIdentifierId().getContent())) ? ""
                                                            : existingExternalIdentifier.getWorkExternalIdentifierId().getContent();
                                                    String title1 = (title == null) ? "" : title.getContent();
                                                    String title2 = (titleToCompare == null) ? "" : titleToCompare.getContent();
                                                    String errorMessage = String.format("Works \"%s\" and \"%s\"(put-code '%s') have the same external id \"%s\"",
                                                            title1, title2, existingWork.getPutCode(), extIdContent);
                                                    throw new IllegalArgumentException(errorMessage);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if source1 and source2 are equals
     * 
     * @param source1
     * @param source2
     * @return true if source1 is equals to source2
     * */
    private boolean isTheSameSource(Source source1, Source source2) {
        if (source1 == null) {
            if (source2 == null)
                return true;
            else
                return false;
        } else {
            if (source2 == null)
                return false;
            else
                return source1.equals(source2);
        }
    }

    /**
     * Check if title1 and title2 are equals
     * 
     * @param title1
     * @param title2
     * @return true if title1 is equals to title2
     * */
    private boolean isTheSameTitle(Title title1, Title title2) {
        if (title1 == null) {
            if (title2 == null)
                return true;
            else
                return false;
        } else {
            if (title2 == null)
                return false;
            else
                return title1.equals(title2);
        }
    }

    private void persistAddedWorks(String orcid, List<OrcidWork> updatedOrcidWorksList) {
        ProfileEntity profileEntity = profileDao.find(orcid);
        Set<String> titles = new HashSet<String>();
        for (OrcidWork updatedOrcidWork : updatedOrcidWorksList) {
            populateContributorInfo(updatedOrcidWork);
            // Create the work entity
            WorkEntity workEntity = jaxb2JpaAdapter.getWorkEntity(updatedOrcidWork, null);
            workEntity.setProfile(profileEntity);
            workDao.persist(workEntity);
            updatedOrcidWork.setPutCode(String.valueOf(workEntity.getId()));
            if (updatedOrcidWork.getWorkTitle() != null && updatedOrcidWork.getWorkTitle().getTitle() != null) {
                String title = updatedOrcidWork.getWorkTitle().getTitle().getContent();
                if (titles.contains(title)) {
                    LOG.warn("Request from {} contains dupplicated works on title '{}' and put-code '{}' \n {}", new Object[] { sourceManager.retrieveSourceOrcid(),
                            title, workEntity.getId(), updatedOrcidWork });
                } else {
                    titles.add(title);
                }
            }
        }
        orcidProfileCacheManager.remove(orcid);
    }

    /**
     * Get each of the work and check the orcid and email parameters against
     * existing profile information.
     */
    private void populateContributorInfo(OrcidWork work) {
        WorkContributors contributors = work.getWorkContributors();
        if (contributors != null) {
            for (Contributor contributor : contributors.getContributor()) {
                // If contributor orcid is available, look for the profile
                // associated with that orcid
                if (contributor.getContributorOrcid() != null) {
                    ProfileEntity profile = profileDao.find(contributor.getContributorOrcid().getPath());
                    if (profile != null) {
                        if (Visibility.PUBLIC.equals(profile.getNamesVisibility())) {
                            contributor.setCreditName(new CreditName(profile.getCreditName()));
                        }
                    }
                } else if (contributor.getContributorEmail() != null) {
                    // Else, if email is available, get the profile
                    // associated with that email
                    String email = contributor.getContributorEmail().getValue();

                    EmailEntity emailEntity = emailDao.findCaseInsensitive(email);
                    if (emailEntity != null) {
                        ProfileEntity profileEntity = emailEntity.getProfile();
                        contributor.setContributorOrcid(new ContributorOrcid(profileEntity.getId()));
                        if (Visibility.PUBLIC.equals(profileEntity.getNamesVisibility())) {
                            contributor.setCreditName(new CreditName(profileEntity.getCreditName()));
                        } else {
                            contributor.setCreditName(null);
                        }
                    }
                }
            }
        }
    }

    private void dedupeWorks(OrcidProfile orcidProfile) {
        OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
        if (orcidActivities != null) {
            OrcidWorks orcidWorks = orcidActivities.getOrcidWorks();
            if (orcidWorks != null) {
                OrcidWorks dedupedOrcidWorks = dedupeWorks(orcidWorks);
                orcidActivities.setOrcidWorks(dedupedOrcidWorks);
            }
        }
    }

    @Override
    public OrcidWorks dedupeWorks(OrcidWorks orcidWorks) {
        Set<OrcidWork> workSet = new LinkedHashSet<OrcidWork>();
        for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
            orcidProfileCleaner.clean(orcidWork);
            workSet.add(orcidWork);
        }
        OrcidWorks dedupedOrcidWorks = new OrcidWorks();
        dedupedOrcidWorks.getOrcidWork().addAll(workSet);
        return dedupedOrcidWorks;
    }

    @Override
    @Transactional
    public OrcidProfile deactivateOrcidProfile(OrcidProfile existingOrcidProfile) {
        OrcidProfile blankedOrcidProfile = new OrcidProfile();

        OrcidBio existingBio = existingOrcidProfile.getOrcidBio();

        OrcidBio minimalBio = new OrcidBio();

        ContactDetails minimalContactDetails = new ContactDetails();
        minimalContactDetails.getEmail().addAll(existingBio.getContactDetails().getEmail());

        OrcidInternal minimalOrcidInternal = new OrcidInternal();
        minimalOrcidInternal.setSecurityDetails(existingOrcidProfile.getOrcidInternal().getSecurityDetails());

        OrcidHistory deactivatedOrcidHistory = existingOrcidProfile.getOrcidHistory();
        deactivatedOrcidHistory.setDeactivationDate(new DeactivationDate(DateUtils.convertToXMLGregorianCalendar(new Date())));

        blankedOrcidProfile.setOrcidHistory(deactivatedOrcidHistory);

        // only names names from bio with a visibility setting
        PersonalDetails minimalPersonalDetails = new PersonalDetails();
        minimalPersonalDetails.setOtherNames(null);
        minimalPersonalDetails.setCreditName(new CreditName());
        minimalPersonalDetails.setGivenNames(new GivenNames("Given Names Deactivated"));
        minimalPersonalDetails.setFamilyName(new FamilyName("Family Name Deactivated"));

        for (Email email : minimalContactDetails.getEmail()) {
            setVisibilityToPrivate(email);
        }
        setVisibilityToPrivate(minimalPersonalDetails.getCreditName());
        setVisibilityToPrivate(minimalPersonalDetails.getOtherNames());

        minimalBio.setPersonalDetails(minimalPersonalDetails);
        minimalBio.setContactDetails(minimalContactDetails);
        minimalBio.setBiography(new Biography());
        minimalBio.setExternalIdentifiers(new ExternalIdentifiers());
        blankedOrcidProfile.setOrcidBio(minimalBio);
        blankedOrcidProfile.setOrcidIdentifier(existingOrcidProfile.getOrcidIdentifier().getPath());

        OrcidProfile profileToReturn = updateOrcidProfile(blankedOrcidProfile);
        notificationManager.sendAmendEmail(profileToReturn, AmendedSection.UNKNOWN);
        return profileToReturn;
    }

    /**
     * Reactivate an inactive profile
     * */
    public OrcidProfile reactivateOrcidProfile(OrcidProfile deactivatedOrcidProfile) {
        OrcidHistory deactivatedOrcidHistory = deactivatedOrcidProfile.getOrcidHistory();
        deactivatedOrcidHistory.setDeactivationDate(null);
        OrcidProfile profileToReturn = updateOrcidProfile(deactivatedOrcidProfile);
        notificationManager.sendAmendEmail(profileToReturn, AmendedSection.UNKNOWN);
        return profileToReturn;
    }

    private void setVisibilityToPrivate(VisibilityType visibilityType) {
        if (visibilityType != null) {
            visibilityType.setVisibility(Visibility.PRIVATE);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    @Transactional
    public OrcidProfile revokeApplication(String userOrcid, String applicationClientId, Collection<ScopePathType> scopes) {
        ProfileEntity existingProfile = profileDao.find(userOrcid);
        if (existingProfile == null) {
            return null;
        }
        Set<OrcidOauth2TokenDetail> tokenDetails = existingProfile.getTokenDetails();
        if (tokenDetails != null) {
            Iterator<OrcidOauth2TokenDetail> tokenDetailIterator = tokenDetails.iterator();
            while (tokenDetailIterator.hasNext()) {
                OrcidOauth2TokenDetail tokenDetail = tokenDetailIterator.next();
                if (tokenDetail.getClientDetailsId().equals(applicationClientId)) {
                    String tokenScope = tokenDetail.getScope();
                    if (tokenScope != null) {
                        Set<ScopePathType> tokenScopes = ScopePathType.getScopesFromSpaceSeparatedString(tokenScope);
                        if (new HashSet<ScopePathType>(scopes).equals(tokenScopes)) {
                            // Remove from DB
                            orcidOauth2TokenDetailDao.remove(tokenDetail.getId());
                            // Remove from in memory object cached by Hibernate
                            tokenDetailIterator.remove();
                        }
                    }
                }
            }
        }
        orcidOauth2TokenDetailDao.flush();
        OrcidProfile updatedOrcidProfile = convertToOrcidProfile(existingProfile, LoadOptions.ALL);
        orcidProfileCacheManager.put(userOrcid, updatedOrcidProfile);
        return updatedOrcidProfile;
    }

    /**
     * Checks that the email is not already being used
     * 
     * @param email
     *            the value to be used to check for an existing record
     */
    @Override
    public boolean emailExists(String email) {
        return emailDao.emailExists(email);
    }

    /**
     * Adds a new {@link List&lt;org.orcid.jaxb.model.message.Affiliation&lt;}
     * to the {@link} OrcidProfile} and returns the updated values
     * 
     * @param updatedOrcidProfile
     * @return
     */
    @Override
    @Transactional
    public void addAffiliations(OrcidProfile updatedOrcidProfile) {
        String orcid = updatedOrcidProfile.getOrcidIdentifier().getPath();
        OrcidProfile existingProfile = retrieveOrcidProfile(orcid);
        if (existingProfile == null) {
            throw new IllegalArgumentException("No record found for " + orcid);
        }
        Affiliations existingAffiliations = existingProfile.retrieveAffiliations();
        Affiliations updatedAffiliations = updatedOrcidProfile.retrieveAffiliations();
        Visibility workVisibilityDefault = existingProfile.getOrcidInternal().getPreferences().getActivitiesVisibilityDefault().getValue();
        Boolean claimed = existingProfile.getOrcidHistory().isClaimed();
        setAffiliationPrivacy(updatedAffiliations, workVisibilityDefault, claimed == null ? false : claimed);
        updatedAffiliations = dedupeAffiliations(updatedAffiliations);
        String amenderOrcid = sourceManager.retrieveSourceOrcid();
        addSourceToAffiliations(updatedAffiliations, amenderOrcid);
        List<Affiliation> updatedAffiliationsList = updatedAffiliations.getAffiliation();
        checkAndUpdateDisambiguatedOrganization(updatedAffiliationsList);
        checkForAlreadyExistingAffiliations(existingAffiliations, updatedAffiliationsList);
        persistAddedAffiliations(orcid, updatedAffiliationsList);
        profileDao.flush();
        boolean notificationsEnabled = existingProfile.getOrcidInternal().getPreferences().getNotificationsEnabled();
        if (notificationsEnabled) {
            notificationManager.sendAmendEmail(existingProfile, AmendedSection.AFFILIATION);
        }
    }

    private void checkAndUpdateDisambiguatedOrganization(List<Affiliation> affiliations) {
        if (affiliations != null && !affiliations.isEmpty()) {
            for (Affiliation affiliation : affiliations) {
                Organization org = affiliation.getOrganization();
                OrgEntity orgEntity = orgManager.getOrgEntity(org);
                // If the org exists
                if (orgEntity != null) {
                    // And it have a disambiguated org
                    if (orgEntity.getOrgDisambiguated() != null) {
                        // Update the desambiguated org
                        org.setDisambiguatedOrganization(adapter.getDisambiguatedOrganization(orgEntity.getOrgDisambiguated()));
                    } else {
                        // Null the disambiguated organization
                        org.setDisambiguatedOrganization(null);
                    }
                }
            }
        }
    }

    /**
     * Adds a new {@link List&lt;org.orcid.jaxb.model.message.FundingList&lt;}
     * to the {@link} OrcidProfile} and returns the updated values
     * 
     * @param updatedOrcidProfile
     * @return
     */
    @Override
    @Transactional
    public void addFundings(OrcidProfile updatedOrcidProfile) {
        String orcid = updatedOrcidProfile.getOrcidIdentifier().getPath();
        OrcidProfile existingProfile = retrieveOrcidProfile(orcid);
        if (existingProfile == null) {
            throw new IllegalArgumentException("No record found for " + orcid);
        }

        String amenderOrcid = sourceManager.retrieveSourceOrcid();
        FundingList existingFundingList = existingProfile.retrieveFundings();
        // updates the amount format to the right format according to the
        // current locale
        setFundingAmountsWithTheCorrectFormat(updatedOrcidProfile);
        FundingList updatedFundingList = updatedOrcidProfile.retrieveFundings();
        Visibility workVisibilityDefault = existingProfile.getOrcidInternal().getPreferences().getActivitiesVisibilityDefault().getValue();
        Boolean claimed = existingProfile.getOrcidHistory().isClaimed();
        setFundingPrivacy(updatedFundingList, workVisibilityDefault, claimed == null ? false : claimed);
        updatedFundingList = dedupeFundings(updatedFundingList);
        addSourceToFundings(updatedFundingList, amenderOrcid);
        List<Funding> updatedList = updatedFundingList.getFundings();
        checkForAlreadyExistingFundings(existingFundingList, updatedList);
        persistAddedFundings(orcid, updatedList);
        profileDao.flush();
        boolean notificationsEnabled = existingProfile.getOrcidInternal().getPreferences().getNotificationsEnabled();
        if (notificationsEnabled) {
            notificationManager.sendAmendEmail(existingProfile, AmendedSection.FUNDING);
        }
    }

    /**
     * Replace the funding amount string into the desired format
     * 
     * @param updatedOrcidProfile
     *            The profile containing the new funding
     * */
    private void setFundingAmountsWithTheCorrectFormat(OrcidProfile updatedOrcidProfile) throws IllegalArgumentException {
        FundingList fundings = updatedOrcidProfile.retrieveFundings();

        for (Funding funding : fundings.getFundings()) {
            // If the amount is not empty, update it
            if (funding.getAmount() != null && StringUtils.isNotBlank(funding.getAmount().getContent())) {
                String amount = funding.getAmount().getContent();
                Locale locale = localeManager.getLocale();
                ParsePosition parsePosition = new ParsePosition(0);
                DecimalFormat numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
                DecimalFormatSymbols symbols = numberFormat.getDecimalFormatSymbols();
                /**
                 * When spaces are allowed, the grouping separator is the
                 * character 160, which is a non-breaking space So, lets change
                 * it so it uses the default space as a separator
                 * */
                if (symbols.getGroupingSeparator() == 160) {
                    symbols.setGroupingSeparator(' ');
                }
                numberFormat.setDecimalFormatSymbols(symbols);
                Number number = numberFormat.parse(amount, parsePosition);
                String formattedAmount = number.toString();
                if (parsePosition.getIndex() != amount.length()) {
                    double example = 1234567.89;
                    NumberFormat numberFormatExample = NumberFormat.getNumberInstance(localeManager.getLocale());
                    throw new IllegalArgumentException("The amount: " + amount + " doesn'n have the right format, it should use the format: "
                            + numberFormatExample.format(example));
                }
                funding.getAmount().setContent(formattedAmount);
            }
        }
    }

    private void setAffiliationPrivacy(OrcidProfile updatedOrcidProfile, Visibility defaultAffiliationVisibility) {
        OrcidHistory orcidHistory = updatedOrcidProfile.getOrcidHistory();
        boolean isClaimed = orcidHistory != null ? orcidHistory.getClaimed().isValue() : false;
        OrcidActivities incomingActivities = updatedOrcidProfile.getOrcidActivities();
        if (incomingActivities != null) {
            Affiliations incomingWorks = incomingActivities.getAffiliations();
            if (incomingWorks != null) {
                setAffiliationPrivacy(incomingWorks, defaultAffiliationVisibility, isClaimed);
            }
        }
    }

    private void setFundingPrivacy(OrcidProfile updatedOrcidProfile, Visibility defaultFundingVisibility) {
        OrcidHistory orcidHistory = updatedOrcidProfile.getOrcidHistory();
        boolean isClaimed = orcidHistory != null ? orcidHistory.getClaimed().isValue() : false;
        OrcidActivities incomingActivities = updatedOrcidProfile.getOrcidActivities();
        if (incomingActivities != null) {
            FundingList incomingFundingList = incomingActivities.getFundings();
            if (incomingFundingList != null) {
                setFundingPrivacy(incomingFundingList, defaultFundingVisibility, isClaimed);
            }
        }
    }

    private void setAffiliationPrivacy(Affiliations incomingAffiliations, Visibility defaultAffiliationVisibility, boolean isClaimed) {
        for (Affiliation incomingAffiliation : incomingAffiliations.getAffiliation()) {
            if (StringUtils.isBlank(incomingAffiliation.getPutCode())) {
                Visibility incomingAffiliationVisibility = incomingAffiliation.getVisibility();
                if (isClaimed) {
                    if (defaultAffiliationVisibility.isMoreRestrictiveThan(incomingAffiliationVisibility)) {
                        incomingAffiliation.setVisibility(defaultAffiliationVisibility);
                    }
                } else if (incomingAffiliationVisibility == null) {
                    incomingAffiliation.setVisibility(Visibility.PRIVATE);
                }
            }
        }
    }

    private void setFundingPrivacy(FundingList incomingFundings, Visibility defaultFundingVisibility, boolean isClaimed) {
        for (Funding incomingFunding : incomingFundings.getFundings()) {
            if (StringUtils.isBlank(incomingFunding.getPutCode())) {
                Visibility incomingFundingVisibility = incomingFunding.getVisibility();
                if (isClaimed) {
                    if (defaultFundingVisibility.isMoreRestrictiveThan(incomingFundingVisibility)) {
                        incomingFunding.setVisibility(defaultFundingVisibility);
                    }
                } else if (incomingFundingVisibility == null) {
                    incomingFunding.setVisibility(Visibility.PRIVATE);
                }
            }
        }
    }

    private void addSourceToAffiliations(Affiliations affiliations, String amenderOrcid) {
        if (affiliations != null && !affiliations.getAffiliation().isEmpty()) {
            for (Affiliation affiliation : affiliations.getAffiliation()) {
                affiliation.setSource(createSource(amenderOrcid));
            }
        }
    }

    private void addSourceToFundings(FundingList fundings, String amenderOrcid) {
        if (fundings != null && !fundings.getFundings().isEmpty()) {
            for (Funding funding : fundings.getFundings()) {
                funding.setSource(createSource(amenderOrcid));
            }
        }
    }

    private void dedupeAffiliations(OrcidProfile orcidProfile) {
        OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
        if (orcidActivities != null) {
            Affiliations affiliations = orcidActivities.getAffiliations();
            if (affiliations != null) {
                Affiliations dedupedAffiliations = dedupeAffiliations(affiliations);
                orcidActivities.setAffiliations(dedupedAffiliations);
            }
        }
    }

    private Affiliations dedupeAffiliations(Affiliations affiliations) {
        Set<Affiliation> affiliationSet = new LinkedHashSet<Affiliation>();
        for (Affiliation affiliation : affiliations.getAffiliation()) {
            orcidProfileCleaner.clean(affiliation);
            affiliationSet.add(affiliation);
        }
        Affiliations dedupedAffiliations = new Affiliations();
        dedupedAffiliations.getAffiliation().addAll(affiliationSet);
        return dedupedAffiliations;
    }

    private void checkForAlreadyExistingAffiliations(Affiliations existingAffiliations, List<Affiliation> updatedAffiliationsList) {
        if (existingAffiliations != null) {
            Set<Affiliation> existingAffiliationsSet = new HashSet<>();
            for (Affiliation existingAffiliation : existingAffiliations.getAffiliation()) {
                existingAffiliationsSet.add(existingAffiliation);
            }
            for (Iterator<Affiliation> updatedAffiliationIterator = updatedAffiliationsList.iterator(); updatedAffiliationIterator.hasNext();) {
                Affiliation updatedAffiliation = updatedAffiliationIterator.next();
                if (existingAffiliationsSet.contains(updatedAffiliation)) {
                    updatedAffiliationIterator.remove();
                }
            }
        }
    }

    private void persistAddedAffiliations(String orcid, List<Affiliation> updatedAffiliationsList) {
        ProfileEntity profileEntity = profileDao.find(orcid);
        for (Affiliation updatedAffiliation : updatedAffiliationsList) {
            OrgAffiliationRelationEntity orgAffiliationRelationEntity = jaxb2JpaAdapter.getNewOrgAffiliationRelationEntity(updatedAffiliation, profileEntity);
            orgAffilationRelationDao.persist(orgAffiliationRelationEntity);
        }
        orcidProfileCacheManager.remove(orcid);
    }

    private void dedupeFundings(OrcidProfile orcidProfile) {
        OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
        if (orcidActivities != null) {
            FundingList fungins = orcidActivities.getFundings();
            if (fungins != null) {
                FundingList dedupedFundings = dedupeFundings(fungins);
                orcidActivities.setFundings(dedupedFundings);
            }
        }
    }

    private FundingList dedupeFundings(FundingList fundings) {
        Set<Funding> fundingsSet = new LinkedHashSet<Funding>();
        for (Funding funding : fundings.getFundings()) {
            orcidProfileCleaner.clean(funding);
            fundingsSet.add(funding);
        }
        FundingList dedupedFundings = new FundingList();
        dedupedFundings.getFundings().addAll(fundingsSet);
        return dedupedFundings;
    }

    private void checkForAlreadyExistingFundings(FundingList existingFundings, List<Funding> updatedFundingsList) {
        if (existingFundings != null) {
            Set<Funding> existingFundingsSet = new HashSet<>();
            for (Funding existingFunding : existingFundings.getFundings()) {
                existingFundingsSet.add(existingFunding);
            }
            for (Iterator<Funding> updatedFundingIterator = updatedFundingsList.iterator(); updatedFundingIterator.hasNext();) {
                Funding updatedFunding = updatedFundingIterator.next();
                for (Funding funding : existingFundingsSet) {
                    if (funding.isDuplicated(updatedFunding)) {
                        updatedFundingIterator.remove();
                        break;
                    }
                }
            }
        }
    }

    private void persistAddedFundings(String orcid, List<Funding> updatedFundingList) {
        ProfileEntity profileEntity = profileDao.find(orcid);
        for (Funding updatedFunding : updatedFundingList) {
            ProfileFundingEntity profileFundingEntity = jaxb2JpaAdapter.getNewProfileFundingEntity(updatedFunding, profileEntity);
            // Save the profile grant
            profileFundingDao.addProfileFunding(profileFundingEntity);
        }
        orcidProfileCacheManager.remove(orcid);
    }

    @Override
    @Transactional
    public OrcidProfile revokeDelegate(String giverOrcid, String receiverOrcid) {
        profileDao.remove(giverOrcid, receiverOrcid);
        givenPermissionToDao.remove(giverOrcid, receiverOrcid);
        return retrieveOrcidProfile(giverOrcid);
    }

    @Override
    @Transactional
    public OrcidProfile deleteProfile(String orcid) {
        ProfileEntity profileEntity = profileDao.find(orcid);
        if (profileEntity == null) {
            LOG.debug("Asked to delete profile {}, but not found in DB", orcid);
            return null;
        }
        profileDao.remove(profileEntity);
        profileDao.flush();
        orcidIndexManager.deleteOrcidProfile(orcid);
        orcidProfileCacheManager.remove(orcid);
        // There seems to be a Hibernate problem relating
        // OrcidOauth2TokenDetail, when getting and deleting in same
        // transaction. So not possible to return deleted profile, and probably
        // not really necessary either.
        // OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity);
        return null;
    }

    static ExecutorService executorService = null;
    static Object executorServiceLock = new Object();
    static ConcurrentHashMap<String, FutureTask<String>> futureHM = new ConcurrentHashMap<String, FutureTask<String>>();

    @Override
    public void processProfilesPendingIndexing() {
        // XXX There are some concurrency related edge cases to fix here.
        LOG.info("About to process profiles pending indexing");
        if (executorService == null || executorService.isShutdown()) {
            synchronized (executorServiceLock) {
                if (executorService == null || executorService.isShutdown()) {
                    executorService = createThreadPoolForIndexing();
                } else {
                    // already running
                    return;
                }
            }
        } else {
            // already running
            return;
        }

        List<String> orcidsForIndexing = new ArrayList<>();
        List<String> orcidFailures = new ArrayList<>();
        List<IndexingStatus> indexingStatuses = new ArrayList<IndexingStatus>(2);
        indexingStatuses.add(IndexingStatus.PENDING);
        indexingStatuses.add(IndexingStatus.REINDEX);
        do {
            orcidsForIndexing = profileDao.findOrcidsByIndexingStatus(indexingStatuses, INDEXING_BATCH_SIZE, orcidFailures);
            LOG.info("Got batch of {} profiles for indexing", orcidsForIndexing.size());
            for (final String orcid : orcidsForIndexing) {
                FutureTask<String> task = new FutureTask<String>(new GetPendingOrcid(orcid));
                executorService.execute(task);
                futureHM.put(orcid, task);
            }
            for (final String orcid : orcidsForIndexing) {
                try {
                    futureHM.remove(orcid).get(240, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    orcidFailures.add(orcid);
                    LOG.error(orcid + " InterruptedException ", e);
                    profileDao.updateIndexingStatus(orcid, IndexingStatus.FAILED);
                } catch (ExecutionException e) {
                    orcidFailures.add(orcid);
                    LOG.error(orcid + " ExecutionException ", e);
                    profileDao.updateIndexingStatus(orcid, IndexingStatus.FAILED);
                } catch (TimeoutException e) {
                    orcidFailures.add(orcid);
                    LOG.error(orcid + " TimeoutException ", e);
                    profileDao.updateIndexingStatus(orcid, IndexingStatus.FAILED);
                }
            }
        } while (!orcidsForIndexing.isEmpty());
        if (!executorService.isShutdown()) {
            synchronized (executorServiceLock) {
                if (!executorService.isShutdown()) {
                    executorService.shutdown();
                    try {
                        executorService.awaitTermination(120, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        LOG.warn("Received an interupt exception whilst waiting for the indexing to complete", e);
                    }
                    LOG.info("Finished processing profiles pending indexing");
                }
            }
        }
    }

    class GetPendingOrcid implements Callable<String> {
        String orcid = null;

        public GetPendingOrcid(String orcid) {
            this.orcid = orcid;
        }

        @Override
        public String call() throws Exception {
            processProfilePendingIndexingInTransaction(orcid);
            return "was successful " + orcid;
        }

    }

    private ExecutorService createThreadPoolForIndexing() {
        return new ThreadPoolExecutor(numberOfIndexingThreads, numberOfIndexingThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(INDEXING_BATCH_SIZE), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void processProfilePendingIndexingInTransaction(final String orcid) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            protected void doInTransactionWithoutResult(TransactionStatus status) {
                LOG.info("About to index profile: {}", orcid);
                OrcidProfile orcidProfile = retrievePublicOrcidProfile(orcid);
                if (orcidProfile == null) {
                    LOG.debug("Null profile found during indexing: {}", orcid);
                } else {
                    LOG.debug("Got profile to index: {}", orcid);
                    orcidIndexManager.persistProfileInformationForIndexingIfNecessary(orcidProfile);
                    profileDao.updateIndexingStatus(orcid, IndexingStatus.DONE);
                }
            }
        });
    }

    @Override
    public void processProfilesThatMissedIndexing() {
        LOG.info("About to process profiles that missed indexing");
        List<ProfileEntity> profileEntities = Collections.emptyList();
        do {
            profileEntities = profileDao.findProfilesThatMissedIndexing(INDEXING_BATCH_SIZE);
            for (ProfileEntity profileEntity : profileEntities) {
                LOG.info("Profile missed indexing: orcid={}, lastModified={}, lastIndexed={}, indexingStatus={}",
                        new Object[] { profileEntity.getId(), profileEntity.getLastModified(), profileEntity.getLastIndexedDate(), profileEntity.getIndexingStatus() });
                profileDao.updateIndexingStatus(profileEntity.getId(), IndexingStatus.PENDING);
            }
        } while (!profileEntities.isEmpty());
        LOG.info("Finished processing profiles that missed indexing");
    }

    @Override
    synchronized public void processUnclaimedProfilesToFlagForIndexing() {
        LOG.info("About to process unclaimed profiles to flag for indexing");
        List<String> orcidsToFlag = Collections.<String> emptyList();
        do {
            orcidsToFlag = profileDao.findUnclaimedNotIndexedAfterWaitPeriod(claimWaitPeriodDays, claimWaitPeriodDays * 2, INDEXING_BATCH_SIZE, orcidsToFlag);
            LOG.info("Got batch of {} unclaimed profiles to flag for indexing", orcidsToFlag.size());
            for (String orcid : orcidsToFlag) {
                LOG.info("About to flag unclaimed profile for indexing: {}", orcid);
                profileDao.updateLastModifiedDateAndIndexingStatus(orcid);
            }
        } while (!orcidsToFlag.isEmpty());
    }

    @Override
    synchronized public void processUnclaimedProfilesForReminder() {
        LOG.info("About to process unclaimed profiles for reminder");
        List<String> orcidsToRemind = Collections.<String> emptyList();
        do {
            orcidsToRemind = profileDao.findUnclaimedNeedingReminder(claimReminderAfterDays, INDEXING_BATCH_SIZE, orcidsToRemind);
            LOG.info("Got batch of {} unclaimed profiles for reminder", orcidsToRemind.size());
            for (final String orcid : orcidsToRemind) {
                processUnclaimedProfileForReminderInTransaction(orcid);
            }
        } while (!orcidsToRemind.isEmpty());
    }

    @Override
    synchronized public void processUnverifiedEmails7Days() {
        LOG.info("About to process unclaimed profiles for reminder");
        List<String> emails = Collections.<String> emptyList();
        do {
            emails = profileDao.findEmailsUnverfiedDays(verifyReminderAfterDays, INDEXING_BATCH_SIZE, EmailEventType.VERIFY_EMAIL_7_DAYS_SENT);
            LOG.info("Got batch of {} unclaimed profiles for reminder", emails.size());
            for (String email : emails) {
                processUnverifiedEmails7DaysInTransaction(email);
            }
        } while (!emails.isEmpty());
    }

    private void processUnverifiedEmails7DaysInTransaction(final String email) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            @Transactional
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                OrcidProfile orcidProfile = retrieveOrcidProfileByEmail(email);
                notificationManager.sendVerificationReminderEmail(orcidProfile, email);
                emailEventDao.persist(new EmailEventEntity(email, EmailEventType.VERIFY_EMAIL_7_DAYS_SENT));
                emailEventDao.flush();
            }
        });
    }

    private void processUnclaimedProfileForReminderInTransaction(final String orcid) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                processUnclaimedProfileForReminder(orcid);
            }
        });
    }

    private void processUnclaimedProfileForReminder(final String orcid) {
        LOG.info("About to process unclaimed profile for reminder: {}", orcid);
        OrcidProfile orcidProfile = retrieveOrcidProfile(orcid);
        notificationManager.sendClaimReminderEmail(orcidProfile, claimWaitPeriodDays - claimReminderAfterDays);
    }

    private Set<OrcidGrantedAuthority> getGrantedAuthorities(ProfileEntity profileEntity) {
        OrcidGrantedAuthority authority = new OrcidGrantedAuthority();
        authority.setProfileEntity(profileEntity);

        if (profileEntity.getOrcidType() == null || profileEntity.getOrcidType().equals(OrcidType.USER))
            authority.setAuthority(OrcidWebRole.ROLE_USER.getAuthority());
        else if (profileEntity.getOrcidType().equals(OrcidType.ADMIN))
            authority.setAuthority(OrcidWebRole.ROLE_ADMIN.getAuthority());
        else if (profileEntity.getOrcidType().equals(OrcidType.GROUP)) {
            switch (profileEntity.getGroupType()) {
            case BASIC:
                authority.setAuthority(OrcidWebRole.ROLE_BASIC.getAuthority());
                break;
            case PREMIUM:
                authority.setAuthority(OrcidWebRole.ROLE_PREMIUM.getAuthority());
                break;
            case BASIC_INSTITUTION:
                authority.setAuthority(OrcidWebRole.ROLE_BASIC_INSTITUTION.getAuthority());
                break;
            case PREMIUM_INSTITUTION:
                authority.setAuthority(OrcidWebRole.ROLE_PREMIUM_INSTITUTION.getAuthority());
                break;
            }
        }
        Set<OrcidGrantedAuthority> authorities = new HashSet<OrcidGrantedAuthority>(1);
        authorities.add(authority);
        return authorities;
    }

    private String hash(String unencrypted) {
        if (StringUtils.isNotBlank(unencrypted)) {
            return encryptionManager.hashForInternalUse(unencrypted);
        } else {
            return null;
        }
    }

    private String encrypt(String unencrypted) {
        if (StringUtils.isNotBlank(unencrypted)) {
            return encryptionManager.encryptForInternalUse(unencrypted);
        } else {
            return null;
        }
    }

    private void encryptAndMapFieldsForProfileEntityPersistence(OrcidProfile orcidProfile, ProfileEntity profileEntity) {
        String password = orcidProfile.getPassword();
        profileEntity.setEncryptedPassword(password == null ? null : encryptionManager.hashForInternalUse(password));
        String verificationCode = orcidProfile.getVerificationCode();
        profileEntity.setEncryptedVerificationCode(verificationCode == null ? null : encryptionManager.encryptForInternalUse(verificationCode));
        String securityAnswer = orcidProfile.getSecurityQuestionAnswer();
        profileEntity.setEncryptedSecurityAnswer(securityAnswer == null ? null : encryptionManager.encryptForInternalUse(securityAnswer));
    }

    @Override
    public Date updateLastModifiedDate(String orcid) {
        return profileDao.updateLastModifiedDate(orcid);
    }

    static public OrcidProfile toOrcidProfile(Element element) {
        return (OrcidProfile) (element != null ? element.getObjectValue() : null);
    }

    @Override
    public void clearOrcidProfileCache() {
        orcidProfileCacheManager.removeAll();
    }

    public void addLocale(OrcidProfile orcidProfile, Locale locale) {
        if (orcidProfile.getOrcidPreferences() == null)
            orcidProfile.setOrcidPreferences(new OrcidPreferences());
        orcidProfile.getOrcidPreferences().setLocale(org.orcid.jaxb.model.message.Locale.fromValue(locale.toString()));
    }

    /**
     * Sets the default visibility of each bio element present in the
     * orcidProfile object
     * 
     * @param orcidProfile
     * */
    private void setDefaultVisibility(ProfileEntity profileEntity, boolean useMemberDefaults) {
        if (profileEntity != null) {

            if (profileEntity.getBiographyVisibility() == null) {
                profileEntity.setBiographyVisibility(useMemberDefaults ? OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility()
                        : OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility());
            }

            if (profileEntity.getExternalIdentifiersVisibility() == null) {
                profileEntity.setExternalIdentifiersVisibility(useMemberDefaults ? OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility()
                        : OrcidVisibilityDefaults.EXTERNAL_IDENTIFIER_DEFAULT.getVisibility());
            }

            if (profileEntity.getKeywordsVisibility() == null) {
                profileEntity.setKeywordsVisibility(useMemberDefaults ? OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility()
                        : OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility());
            }

            if (profileEntity.getResearcherUrlsVisibility() == null) {
                profileEntity.setResearcherUrlsVisibility(useMemberDefaults ? OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility()
                        : OrcidVisibilityDefaults.RESEARCHER_URLS_DEFAULT.getVisibility());
            }

            if (profileEntity.getNamesVisibility() == null) {
                profileEntity.setNamesVisibility(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility());
            }

            if (profileEntity.getOtherNamesVisibility() == null) {
                profileEntity.setOtherNamesVisibility(useMemberDefaults ? OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility()
                        : OrcidVisibilityDefaults.OTHER_NAMES_DEFAULT.getVisibility());
            }

            if (profileEntity.getProfileAddressVisibility() == null) {
                profileEntity.setProfileAddressVisibility(useMemberDefaults ? OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility()
                        : OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility());
            }

            if (profileEntity.getActivitiesVisibilityDefault() == null) {
                profileEntity.setActivitiesVisibilityDefault(useMemberDefaults ? OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility()
                        : OrcidVisibilityDefaults.ACTIVITIES_DEFAULT.getVisibility());
            }

        }
    }
}
