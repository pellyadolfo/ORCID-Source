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
package org.orcid.jaxb.model.record_rc2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc2.CreditName;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.common_rc2.VisibilityType;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "givenNames", "familyName", "creditName" })
@XmlRootElement(name = "name", namespace = "http://www.orcid.org/ns/personal-details")
public class Name implements Serializable, VisibilityType {
    private static final long serialVersionUID = -7946486981092688675L;

    @XmlElement(name = "given-names", namespace = "http://www.orcid.org/ns/personal-details")
    private GivenNames givenNames;
    @XmlElement(name = "family-name", namespace = "http://www.orcid.org/ns/personal-details")
    private FamilyName familyName;
    @XmlElement(name = "credit-name", namespace = "http://www.orcid.org/ns/personal-details")
    private CreditName creditName;
    @XmlAttribute
    protected Visibility visibility;
    @XmlAttribute
    protected String path;

    public GivenNames getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(GivenNames givenNames) {
        this.givenNames = givenNames;
    }

    public FamilyName getFamilyName() {
        return familyName;
    }

    public void setFamilyName(FamilyName familyName) {
        this.familyName = familyName;
    }

    public CreditName getCreditName() {
        return creditName;
    }

    public void setCreditName(CreditName creditName) {
        this.creditName = creditName;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((creditName == null) ? 0 : creditName.hashCode());
        result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
        result = prime * result + ((givenNames == null) ? 0 : givenNames.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Name other = (Name) obj;
        if (creditName == null) {
            if (other.creditName != null)
                return false;
        } else if (!creditName.equals(other.creditName))
            return false;
        if (familyName == null) {
            if (other.familyName != null)
                return false;
        } else if (!familyName.equals(other.familyName))
            return false;
        if (givenNames == null) {
            if (other.givenNames != null)
                return false;
        } else if (!givenNames.equals(other.givenNames))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (visibility != other.visibility)
            return false;
        return true;
    }
}
