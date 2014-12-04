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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.24 at 04:27:39 PM GMT 
//

package org.orcid.jaxb.model.notification.addactivities;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}externalIdType"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}externalIdValue"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "externalIdType", "externalIdValue" })
@XmlRootElement(name = "externalId")
public class ExternalId implements Serializable {

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String externalIdType;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String externalIdValue;

    /**
     * Gets the value of the externalIdType property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getExternalIdType() {
        return externalIdType;
    }

    /**
     * Sets the value of the externalIdType property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setExternalIdType(String value) {
        this.externalIdType = value;
    }

    /**
     * Gets the value of the externalIdValue property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getExternalIdValue() {
        return externalIdValue;
    }

    /**
     * Sets the value of the externalIdValue property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setExternalIdValue(String value) {
        this.externalIdValue = value;
    }

}
