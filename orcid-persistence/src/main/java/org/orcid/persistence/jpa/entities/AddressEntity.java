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
package org.orcid.persistence.jpa.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.common.Iso3166Country;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@Entity
@Table(name = "address")
public class AddressEntity extends BaseEntity<Long> implements ProfileAware, SourceAware {
    private static final long serialVersionUID = -331185018871126442L;
    private Long id;
    private Iso3166Country iso2Country;
    private Visibility visibility;
    private Boolean primary = Boolean.FALSE;
    private ProfileEntity user;
    private SourceEntity source;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "iso2_country", length = 2)
    public Iso3166Country getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(Iso3166Country iso2Country) {
        this.iso2Country = iso2Country;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public SourceEntity getSource() {
        return source;
    }

    public void setSource(SourceEntity source) {
        this.source = source;
    }

    public void setUser(ProfileEntity user) {
        this.user = user;
    }
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orcid", nullable = false)
    public ProfileEntity getUser() {
        return user;
    }
    
    @Transient
    @Override
    public ProfileEntity getProfile() {
        return user;
    }

    @Column(name = "is_primary", columnDefinition = "boolean default false")
    public Boolean getPrimary() {
        return primary == null ? Boolean.FALSE : primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((iso2Country == null) ? 0 : iso2Country.hashCode());
        result = prime * result + ((primary == null) ? 0 : primary.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
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
        AddressEntity other = (AddressEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (iso2Country != other.iso2Country)
            return false;
        if (primary == null) {
            if (other.primary != null)
                return false;
        } else if (!primary.equals(other.primary))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (visibility != other.visibility)
            return false;
        return true;
    }

}