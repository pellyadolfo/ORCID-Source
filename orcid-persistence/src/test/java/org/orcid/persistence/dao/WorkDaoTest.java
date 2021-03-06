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
package org.orcid.persistence.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class WorkDaoTest extends DBUnitTest {

    private static String USER_ORCID="4444-4444-4444-4441";
    
    @Resource
    private WorkDao workDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/WorksEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/WorksEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddWork() {
        String title = "New Work";
        String subtitle = "Subtitle";
        String citation = "Test citation";
        String description = "Description for new work";
        String url = "http://work.com";

        WorkEntity work = new WorkEntity();
        work.setCitation(citation);
        work.setCitationType(CitationType.FORMATTED_UNSPECIFIED);
        work.setDescription(description);
        work.setTitle(title);
        work.setSubtitle(subtitle);
        work.setWorkType(WorkType.BOOK);
        work.setWorkUrl(url);
        ProfileEntity profile = new ProfileEntity(USER_ORCID); 
        work.setProfile(profile);
        work.setSource(new SourceEntity(profile));
        work.setAddedToProfileDate(new Date());
        
        assertNull(work.getId());

        try {
            work = workDao.addWork(work);
        } catch (Exception e) {
            fail();
        }

        assertNotNull(work.getId());
    }
}
