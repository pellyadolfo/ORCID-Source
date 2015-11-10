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
package org.orcid.api.common.writer.stats;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;

@Provider
@Produces({ OrcidApiConstants.TEXT_CSV }) // text/x-bibliography ?
public class StatisticsTimelineListMBWriter implements MessageBodyWriter<StatsTimelineList> {

    @Override
    public long getSize(StatsTimelineList arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] arg2, MediaType arg3) {
        return StatsTimelineList.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(StatsTimelineList data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        StringBuffer buf = new StringBuffer();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        buf.append("type");
        buf.append(',');
        buf.append("date");
        buf.append(',');
        buf.append("stat");
        buf.append('\n');
        for (StatisticsTimeline line : data.getTimelines()) {
            for (Date d : line.getTimeline().keySet()) {
                buf.append(line.getStatisticName());
                buf.append(',');
                buf.append(format.format((d)));
                buf.append(',');
                buf.append(line.getTimeline().get(d));
                buf.append('\n');
            }
        }
        final PrintStream printStream = new PrintStream(entityStream);
        printStream.print(buf.toString());
    }

}
