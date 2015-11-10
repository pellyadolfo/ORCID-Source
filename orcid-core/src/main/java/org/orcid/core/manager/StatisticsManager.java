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
import java.util.Locale;
import java.util.Map;

import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;
import org.orcid.persistence.jpa.entities.StatisticValuesEntity;

public interface StatisticsManager {
    
    /**
     * Creates a new statistics key
     * 
     * @return the statistic key object
     * */
    public StatisticKeyEntity createKey();

    /**
     * Save an statistics record on database
     * 
     * @param id
     * @param name
     *            the name of the statistic
     * @param value
     *            the statistic value
     * @return the statistic value object
     * */
    public void saveStatistics(List<StatisticValuesEntity> statistics);

    /**
     * Get an statistics object from database
     * 
     * @param id
     * @param name
     * @return the Statistic value object associated with the id and name
     *         parameters
     * */
    public StatisticValuesEntity getStatistic(StatisticKeyEntity id, String name);

    /**
     * Get the list of the latest statistics
     * 
     * @return a list that contains the latest set of statistics
     * */
    public List<StatisticValuesEntity> getLatestStatistics();
    
    /**
     * Get the the latest statistics value for the statistics name parameter
     * @param statisticName
     * @return the latest statistics value for the statistics name parameter
     * */
    public StatisticValuesEntity getLatestStatistics(String statisticName);
    
    /**
     * Get the the latest live ids statistics 
     * @param locale
     * @return the latest statistics live ids statistics
     * */
    public String getLiveIds(Locale locale);
    
    /**
     * Get the last statistics key
     * 
     * @return the last statistics key
     * */
    public StatisticKeyEntity getLatestKey();
    
    /**
     * Get all entries with a given name as a domain model;
     * @param statisticName
     * @return all statistics values for the statistics name parameter
     * */
    public StatisticsTimeline getStatisticsTimelineModel(StatisticsEnum statisticName);

    /**
     * Get the list of the latest statistics as a domain model
     * 
     * @return a list that contains the latest set of statistics
     * */
    public StatisticsSummary getLatestStatisticsModel();
}
