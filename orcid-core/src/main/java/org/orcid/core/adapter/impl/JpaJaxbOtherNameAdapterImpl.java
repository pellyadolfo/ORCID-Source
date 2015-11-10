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
package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.JpaJaxbOtherNameAdapter;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.jaxb.model.common.LastModifiedDate;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;

public class JpaJaxbOtherNameAdapterImpl implements JpaJaxbOtherNameAdapter {

    private MapperFacade mapperFacade;
    
    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

	@Override
	public OtherNameEntity toOtherNameEntity(OtherName otherName) {
		if(otherName == null) {
            return null;
        }
        return mapperFacade.map(otherName, OtherNameEntity.class);
	}

	@Override
	public OtherName toOtherName(OtherNameEntity entity) {
		if(entity == null) {
			return null;
        }
        return mapperFacade.map(entity, OtherName.class);
	}

	@Override
	public OtherNames toOtherNameList(Collection<OtherNameEntity> entities) {
		if(entities == null) {
			return null;
        }
		List<OtherName> otherNameList = mapperFacade.mapAsList(entities, OtherName.class);
		
		OtherNames otherNames = new OtherNames();
		XMLGregorianCalendar tempDate = null;
		otherNames.setOtherNames(otherNameList);
		
		if(otherNameList != null && !otherNameList.isEmpty()) {
			tempDate = otherNameList.get(0).getLastModifiedDate().getValue();
			for(OtherName otherName : otherNameList) {
				if(tempDate.compare(otherName.getLastModifiedDate().getValue()) == -1) {
					tempDate = otherName.getLastModifiedDate().getValue();
				}
			}
		}
		if(tempDate != null)
			otherNames.setLastModifiedDate(new LastModifiedDate(tempDate));
		
        return otherNames;
	}

	@Override
	public OtherNameEntity toOtherNameEntity(OtherName otherName, OtherNameEntity existing) {
        if(otherName == null) {
            return null;
        }
        mapperFacade.map(otherName, existing);
        return existing;
	}

}
