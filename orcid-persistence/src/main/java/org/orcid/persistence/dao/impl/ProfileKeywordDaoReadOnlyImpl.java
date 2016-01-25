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
package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.ProfileKeywordDaoReadOnly;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.springframework.cache.annotation.Cacheable;

public class ProfileKeywordDaoReadOnlyImpl extends GenericDaoImpl<ProfileKeywordEntity, Long> implements ProfileKeywordDaoReadOnly {

    public ProfileKeywordDaoReadOnlyImpl() {
        super(ProfileKeywordEntity.class);
    }

    /**
     * Return the list of keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    @Override
    @SuppressWarnings("unchecked")
    @Cacheable(value = "dao-keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public List<ProfileKeywordEntity> getProfileKeywors(String orcid, long lastModified) {
        Query query = entityManager.createQuery("FROM ProfileKeywordEntity WHERE profile.id = :orcid");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }            

    @Override
    public ProfileKeywordEntity getProfileKeyword(String orcid, Long putCode) {
        Query query = entityManager.createQuery("FROM ProfileKeywordEntity WHERE profile.id=:orcid and id=:id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", putCode);
        return (ProfileKeywordEntity) query.getSingleResult();
    }
}
