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
package org.orcid.api.t1.stats.delegator.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.orcid.api.common.writer.stats.StatsTimelineList;
import org.orcid.api.t1.stats.delegator.StatsApiServiceDelegator;
import org.orcid.core.manager.impl.StatisticsCacheManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;

public class StatsApiServiceDelegatorImpl implements StatsApiServiceDelegator {

    @Resource
    StatisticsCacheManager statisticsCacheManager;

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getStatsSummary() {
        StatisticsSummary summary = statisticsCacheManager.retrieve();
        if (summary == null)
            return Response.status(Status.NOT_FOUND).build();

        return Response.ok(summary).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getAllStatsTimelines() {
        StatisticsSummary summary = statisticsCacheManager.retrieve();
        if (summary == null)
            return Response.status(Status.NOT_FOUND).build();
        StatsTimelineList statsTimelines = new StatsTimelineList();
        for (String key : summary.getStatistics().keySet()){
            StatisticsTimeline timeline = statisticsCacheManager.getStatisticsTimelineModel(StatisticsEnum.fromString(key));
            if (timeline !=null)
                statsTimelines.getTimelines().add(timeline);
        }
        return Response.ok(statsTimelines).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getStatsTimeline(StatisticsEnum type) {
        StatisticsTimeline timeline = statisticsCacheManager.getStatisticsTimelineModel(type);
        if (timeline == null)
            return Response.status(Status.NOT_FOUND).build();

        return Response.ok(timeline).build();
    }
    
}
