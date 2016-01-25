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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbKeywordAdapter;
import org.orcid.core.manager.OrcidProfileManagerReadOnly;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.persistence.dao.ProfileKeywordDaoReadOnly;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.springframework.cache.annotation.Cacheable;

public class ProfileKeywordManagerReadOnlyImpl implements ProfileKeywordManagerReadOnly {

    @Resource
    private ProfileKeywordDaoReadOnly profileKeywordDaoReadOnly;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private JpaJaxbKeywordAdapter adapter;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource
    private OrcidProfileManagerReadOnly orcidProfileManagerReadOnly;

    private long getLastModified(String orcid) {
        Date lastModified = orcidProfileManagerReadOnly.retrieveLastModifiedDate(orcid);
        return (lastModified == null) ? 0 : lastModified.getTime();
    }
    
    @Override
    @Cacheable(value = "keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public Keywords getKeywords(String orcid, long lastModified) {
        List<ProfileKeywordEntity> entities = getProfileKeywordEntitys(orcid, null);
        Keywords result = adapter.toKeywords(entities);
        result.updateIndexingStatusOnChilds();
        return result;
    }

    @Override
    @Cacheable(value = "public-keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public Keywords getPublicKeywords(String orcid, long lastModified) {
        List<ProfileKeywordEntity> entities = getProfileKeywordEntitys(orcid, Visibility.PUBLIC);
        Keywords result = adapter.toKeywords(entities);
        result.updateIndexingStatusOnChilds();
        return result;
    }

    private List<ProfileKeywordEntity> getProfileKeywordEntitys(String orcid, Visibility visibility) {
        List<ProfileKeywordEntity> keywords = profileKeywordDaoReadOnly.getProfileKeywors(orcid, getLastModified(orcid));
        if (visibility != null) {
            Iterator<ProfileKeywordEntity> it = keywords.iterator();
            while (it.hasNext()) {
                ProfileKeywordEntity keywordEntity = it.next();
                if (!visibility.equals(keywordEntity.getVisibility())) {
                    it.remove();
                }
            }
        }

        return keywords;
    }
    
    @Override
    public Keyword getKeyword(String orcid, Long putCode) {
        ProfileKeywordEntity entity = profileKeywordDaoReadOnly.getProfileKeyword(orcid, putCode);
        return adapter.toKeyword(entity);
    }    
}
