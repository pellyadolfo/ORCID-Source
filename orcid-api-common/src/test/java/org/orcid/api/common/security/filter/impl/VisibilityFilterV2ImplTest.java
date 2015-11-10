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
package org.orcid.api.common.security.filter.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.xml.sax.SAXException;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class VisibilityFilterV2ImplTest {

    private Unmarshaller unmarshaller;

    @Resource(name = "visibilityFilterV2")
    private VisibilityFilterV2 visibilityFilter;

    @Before
    public void before() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ActivitiesSummary.class);
        unmarshaller = context.createUnmarshaller();
    }

    @Test
    public void testUnmarshall() throws JAXBException, IOException, SAXException {
        ActivitiesSummary activitiesSummary = getActivitiesSummary("/activities-protected-full-latest.xml");
        String expected = IOUtils.toString(getClass().getResourceAsStream("/activities-protected-full-latest.xml"), "UTF-8").replaceAll("(?s)<!--.*?-->\n*", "");
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expected, activitiesSummary.toString());               
        assertTrue(diff.identical());
    }

    @Test
    public void testFilterActivities() throws JAXBException {
        ActivitiesSummary activitiesSummary = getActivitiesSummary("/activities-protected-full-latest.xml");
        ActivitiesSummary expectedActivitiesSummary = getActivitiesSummary("/activities-stripped-latest.xml");
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_PUBLIC);
        visibilityFilter.filter(activitiesSummary);
        assertEquals(expectedActivitiesSummary.toString(), activitiesSummary.toString());
    }

    @Test
    public void testFilterActivitiesOnPublicAPI() throws JAXBException {
        ActivitiesSummary activitiesSummary = getActivitiesSummary("/activities-protected-full-latest.xml");
        ActivitiesSummary expectedActivitiesSummary = getActivitiesSummary("/activities-stripped-latest.xml");
        visibilityFilter.filter(activitiesSummary);
        assertEquals(expectedActivitiesSummary.toString(), activitiesSummary.toString());
    }

    private ActivitiesSummary getActivitiesSummary(String path) throws JAXBException {
        return (ActivitiesSummary) unmarshaller.unmarshal(getClass().getResourceAsStream(path));
    }

}
