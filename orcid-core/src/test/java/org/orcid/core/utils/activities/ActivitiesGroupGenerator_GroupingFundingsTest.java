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
package org.orcid.core.utils.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.orcid.jaxb.model.common.Title;
import org.orcid.jaxb.model.record.summary_rc1.FundingSummary;
import org.orcid.jaxb.model.record_rc1.FundingExternalIdentifier;
import org.orcid.jaxb.model.record_rc1.FundingExternalIdentifierType;
import org.orcid.jaxb.model.record_rc1.FundingExternalIdentifiers;
import org.orcid.jaxb.model.record_rc1.FundingTitle;

public class ActivitiesGroupGenerator_GroupingFundingsTest extends ActivitiesGroupGeneratorBaseTest {

    @Test
    public void groupFundings_4GroupsOf1Funding_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, FundingSummary> fundings = generateFundings();
        
        //Group the first group
        //funding-2 -> C, D, E
        FundingSummary funding2 = fundings.get("funding-2");
        generator.group(funding2);
        //There should be one group, and the ext ids should be A, B and C
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        ActivitiesGroup g1 = groups.get(0);
        assertNotNull(g1);
        assertNotNull(g1.getActivities());
        assertEquals(1, g1.getActivities().size());
        assertTrue(g1.getActivities().contains(funding2));
        assertNotNull(g1.getGroupKeys());
        assertEquals(3, g1.getGroupKeys().size());
        checkExternalIdentifiers(funding2, g1);
        
        //Add another funding to the groups
        //funding-5 -> M, N, O
        FundingSummary funding5 = fundings.get("funding-5");
        generator.group(funding5);
        //There should be two groups, one for each funding
        groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        //There should be one activity in each group         
        assertEquals(1, groups.get(0).getActivities().size());
        assertEquals(1, groups.get(1).getActivities().size());
        //There should be 3 ext ids in each group
        assertEquals(3, groups.get(0).getGroupKeys().size());
        assertEquals(3, groups.get(1).getGroupKeys().size());                
        //Check funding in groups
        checkActivityIsOnGroups(funding5, groups);
        
        //Add another funding to the groups
        //funding-7 -> 1, 2, B
        FundingSummary funding7 = fundings.get("funding-7");
        generator.group(funding7);
        groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(3, groups.size());
        //There should be one activity in each group         
        assertEquals(1, groups.get(0).getActivities().size());
        assertEquals(1, groups.get(1).getActivities().size());
        assertEquals(1, groups.get(2).getActivities().size());
        //There should be 3 ext ids in each group
        assertEquals(3, groups.get(0).getGroupKeys().size());
        assertEquals(3, groups.get(1).getGroupKeys().size());                
        assertEquals(3, groups.get(2).getGroupKeys().size());
        //Check funding in groups
        checkActivityIsOnGroups(funding7, groups);
        
        //Add another funding to the groups
        //funding-8 -> No external identifiers  
        FundingSummary funding8 = fundings.get("funding-8");
        generator.group(funding8);        
        groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(4, groups.size());
        //There should be one activity in each group         
        assertEquals(1, groups.get(0).getActivities().size());
        assertEquals(1, groups.get(1).getActivities().size());
        assertEquals(1, groups.get(2).getActivities().size());
        assertEquals(1, groups.get(3).getActivities().size());
        //There should be 3 ext ids in each group, except for one group that doesnt have any ext id
        boolean funding8found = false;
        for(int i = 0; i < 4; i++) {
            if(groups.get(i).getGroupKeys().size() == 0) {                                
                funding8found = true;
            } else {
                assertEquals(3, groups.get(i).getGroupKeys().size());
            }                                                        
        }
        assertTrue("FundingSummary without ext ids was not found", funding8found);
        //Check funding in groups
        checkActivityIsOnGroups(funding8, groups);        
    }
    
    /**
     * Test grouping funding-1 and funding-2 
     * */
    @Test
    public void groupFundings_1GroupsOf2Fundings_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, FundingSummary> fundings = generateFundings();
        
        FundingSummary funding1 = fundings.get("funding-1");        
        FundingSummary funding2 = fundings.get("funding-2");
        
        generator.group(funding1);
        generator.group(funding2);
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        ActivitiesGroup g1 = groups.get(0);
        assertNotNull(g1);
        assertNotNull(g1.getActivities());
        assertEquals(2, g1.getActivities().size());
        assertTrue(g1.getActivities().contains(funding1));
        assertTrue(g1.getActivities().contains(funding2));
        assertNotNull(g1.getGroupKeys());
        assertEquals(5, g1.getGroupKeys().size());
        checkExternalIdentifiers(funding1, g1);
        checkExternalIdentifiers(funding2, g1);
    }
    
    /**
     * Test grouping (funding-1 and funding-2) and (funding-5 and funding-6) 
     * */
    @Test
    public void groupFundings_2GroupsOf2Fundings_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, FundingSummary> fundings = generateFundings();
        
        FundingSummary funding1 = fundings.get("funding-1");        
        FundingSummary funding2 = fundings.get("funding-2");
        FundingSummary funding5 = fundings.get("funding-5");
        FundingSummary funding6 = fundings.get("funding-6");
        
        generator.group(funding1);
        generator.group(funding2);
        generator.group(funding5);
        generator.group(funding6);
        
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        //Check there are two activities in each group
        assertEquals(2, groups.get(0).getActivities().size());
        assertEquals(2, groups.get(1).getActivities().size());
        //Check there are five external ids in each group
        assertEquals(5, groups.get(0).getGroupKeys().size());
        assertEquals(5, groups.get(1).getGroupKeys().size());
        //Check each funding
        checkActivityIsOnGroups(funding1, groups);
        checkActivityIsOnGroups(funding2, groups);
        checkActivityIsOnGroups(funding5, groups);
        checkActivityIsOnGroups(funding6, groups);
        
        //Check funding1 and funding2 are in the same group
        checkActivitiesBelongsToTheSameGroup(groups, funding1, funding2);
        //Check funding6 and funding7 are in the same group
        checkActivitiesBelongsToTheSameGroup(groups, funding5, funding6);
        //Check fundings are not mixed
        checkActivitiesDontBelongsToTheSameGroup(groups, funding1, funding5);
        checkActivitiesDontBelongsToTheSameGroup(groups, funding1, funding6);
        checkActivitiesDontBelongsToTheSameGroup(groups, funding2, funding5);
        checkActivitiesDontBelongsToTheSameGroup(groups, funding2, funding6);
    }
    
    /**
     * Test that two groups without ext ids dont get grouped
     * */
    @Test
    public void groupFundings_DontGroupFundingsWithoutExtIds_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, FundingSummary> fundings = generateFundings();
        
        //Group the first group
        FundingSummary funding8 = fundings.get("funding-8");
        FundingSummary funding9 = fundings.get("funding-9");
        
        generator.group(funding8);
        generator.group(funding9);
        
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        //Check there are two activities in each group
        assertEquals(1, groups.get(0).getActivities().size());
        assertEquals(1, groups.get(1).getActivities().size());
        //Check there are five external ids in each group
        assertEquals(0, groups.get(0).getGroupKeys().size());
        assertEquals(0, groups.get(1).getGroupKeys().size());
        
        checkActivityIsOnGroups(funding8, groups);
        checkActivityIsOnGroups(funding9, groups);
        
        checkActivitiesDontBelongsToTheSameGroup(groups, funding8, funding9);
    }
    
    /**
     * funding-1 and funding-3 will be in different groups
     * then funding-2 will go to the same group as funding-1
     * then funding-4 contains Y and B so, the two groups should be merged
     * */
    @Test
    public void groupFundings_MergeTwoGroups_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, FundingSummary> fundings = generateFundings();
        
        //Group the first group
        FundingSummary funding1 = fundings.get("funding-1");
        FundingSummary funding2 = fundings.get("funding-2");
        FundingSummary funding3 = fundings.get("funding-3");
        FundingSummary funding4 = fundings.get("funding-4");
        
        generator.group(funding1);
        generator.group(funding2);
        generator.group(funding3);

        /**
         * At this point there are two groups
         * G1 with funding1 and funding2
         * G2 with funding3
         * */
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        checkActivitiesBelongsToTheSameGroup(groups, funding1, funding2);
        checkActivitiesDontBelongsToTheSameGroup(groups, funding1, funding3);
        checkActivitiesDontBelongsToTheSameGroup(groups, funding2, funding3);
        
        //group funding4, which should merge the two groups
        generator.group(funding4);
        groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        assertEquals(4, groups.get(0).getActivities().size());
        assertEquals(9, groups.get(0).getGroupKeys().size());
        
        checkActivityIsOnGroups(funding1, groups);
        checkActivityIsOnGroups(funding2, groups);
        checkActivityIsOnGroups(funding3, groups);
        checkActivityIsOnGroups(funding4, groups);
        checkActivitiesBelongsToTheSameGroup(groups, funding1, funding2, funding3, funding4);
    }
    
    /**
     * funding-1, funding-3, funding-5 and funding-8 will be in separate groups
     * then funding-4 will merge groups of funding-1 and funding-3
     * 
     * Check that after that, there are 3 groups, one with funding-1, funding-3 and funding-4, one with funding-5 and other with funding-8
     * */
    @Test
    public void groupFundings_MergeGroupsDontAffectNotMergedGroups_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, FundingSummary> fundings = generateFundings();
        
        //Group the first group
        FundingSummary funding1 = fundings.get("funding-1");
        FundingSummary funding3 = fundings.get("funding-3");
        FundingSummary funding4 = fundings.get("funding-4");
        FundingSummary funding5 = fundings.get("funding-5");
        FundingSummary funding8 = fundings.get("funding-8");
        
        //Respect order
        generator.group(funding1);
        generator.group(funding3);
        generator.group(funding5);
        generator.group(funding8);
        generator.group(funding4);
        
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(3, groups.size());
        //Check funding1, funding3 and funding4 belongs to the same group
        checkActivitiesBelongsToTheSameGroup(groups, funding1, funding3, funding4);
        //Check funding1, funding5 and funding8 are all in different groups
        checkActivitiesDontBelongsToTheSameGroup(groups, funding1, funding5, funding8);
        
        checkActivityIsOnGroups(funding1, groups);
        checkActivityIsOnGroups(funding3, groups);
        checkActivityIsOnGroups(funding4, groups);
        checkActivityIsOnGroups(funding5, groups);
        checkActivityIsOnGroups(funding8, groups);
    }       
    
    /**
     * funding-1 -> A, B, C 
     * funding-2 -> C, D, E
     * funding-3 -> X, Y, Z
     * funding-4 -> Y, B, 1
     * funding-5 -> M, N, O 
     * funding-6 -> O, P, Q
     * funding-7 -> 1, 2, B  
     * funding-8 -> No external identifiers
     * funding-9 -> No external identifiers  
     * */
    private Map<String, FundingSummary> generateFundings() {
        Map<String, FundingSummary> result = new HashMap<String, FundingSummary>();
        for(int i = 1; i < 10; i++) {
            String name = "funding-" + i;
            FundingSummary funding = new FundingSummary();
            FundingTitle title = new FundingTitle();
            title.setTitle(new Title(name));
            funding.setTitle(title);
            FundingExternalIdentifiers fei = new FundingExternalIdentifiers();
            switch(i) {
            case 1:
                FundingExternalIdentifier f1 = new FundingExternalIdentifier();
                f1.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f1.setValue("A");
                FundingExternalIdentifier f2 = new FundingExternalIdentifier();
                f2.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f2.setValue("B");
                FundingExternalIdentifier f3 = new FundingExternalIdentifier();
                f3.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f3.setValue("C");
                fei.getExternalIdentifier().add(f1);
                fei.getExternalIdentifier().add(f2);
                fei.getExternalIdentifier().add(f3);
                break;
            case 2:
                FundingExternalIdentifier f4 = new FundingExternalIdentifier();
                f4.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f4.setValue("C");
                FundingExternalIdentifier f5 = new FundingExternalIdentifier();
                f5.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f5.setValue("D");
                FundingExternalIdentifier f6 = new FundingExternalIdentifier();
                f6.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f6.setValue("E");
                fei.getExternalIdentifier().add(f4);
                fei.getExternalIdentifier().add(f5);
                fei.getExternalIdentifier().add(f6);
                break;
            case 3:
                FundingExternalIdentifier f7 = new FundingExternalIdentifier();
                f7.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f7.setValue("X");
                FundingExternalIdentifier f8 = new FundingExternalIdentifier();
                f8.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f8.setValue("Y");
                FundingExternalIdentifier f9 = new FundingExternalIdentifier();
                f9.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f9.setValue("Z");
                fei.getExternalIdentifier().add(f7);
                fei.getExternalIdentifier().add(f8);
                fei.getExternalIdentifier().add(f9);
                break;
            case 4:
                FundingExternalIdentifier f10 = new FundingExternalIdentifier();
                f10.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f10.setValue("Y");
                FundingExternalIdentifier f11 = new FundingExternalIdentifier();
                f11.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f11.setValue("B");
                FundingExternalIdentifier f12 = new FundingExternalIdentifier();
                f12.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f12.setValue("1");
                fei.getExternalIdentifier().add(f10);
                fei.getExternalIdentifier().add(f11);
                fei.getExternalIdentifier().add(f12);
                break;
            case 5:
                FundingExternalIdentifier f13 = new FundingExternalIdentifier();
                f13.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f13.setValue("M");
                FundingExternalIdentifier f14 = new FundingExternalIdentifier();
                f14.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f14.setValue("N");
                FundingExternalIdentifier f15 = new FundingExternalIdentifier();
                f15.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f15.setValue("O");
                fei.getExternalIdentifier().add(f13);
                fei.getExternalIdentifier().add(f14);
                fei.getExternalIdentifier().add(f15);
                break;
            case 6:
                FundingExternalIdentifier f16 = new FundingExternalIdentifier();
                f16.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f16.setValue("O");
                FundingExternalIdentifier f17 = new FundingExternalIdentifier();
                f17.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f17.setValue("P");
                FundingExternalIdentifier f18 = new FundingExternalIdentifier();
                f18.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f18.setValue("Q");
                fei.getExternalIdentifier().add(f16);
                fei.getExternalIdentifier().add(f17);
                fei.getExternalIdentifier().add(f18);
                break;
            case 7:
                FundingExternalIdentifier f19 = new FundingExternalIdentifier();
                f19.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f19.setValue("1");
                FundingExternalIdentifier f20 = new FundingExternalIdentifier();
                f20.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f20.setValue("2");
                FundingExternalIdentifier f21 = new FundingExternalIdentifier();
                f21.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f21.setValue("B");
                fei.getExternalIdentifier().add(f19);
                fei.getExternalIdentifier().add(f20);
                fei.getExternalIdentifier().add(f21);
                break;
            }
            funding.setExternalIdentifiers(fei);
            result.put(name, funding);
        }
        return result;
    }        
}
