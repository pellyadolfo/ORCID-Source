//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.26 at 11:29:18 AM GMT 
//


package xmlns.org.eurocris.cerif_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for cfClass_Class__Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cfClass_Class__Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cfClassId1" type="{urn:xmlns:org:eurocris:cerif-1.6-2}cfId__Type"/>
 *         &lt;element name="cfClassId2" type="{urn:xmlns:org:eurocris:cerif-1.6-2}cfId__Type"/>
 *         &lt;element name="cfClassSchemeId1" type="{urn:xmlns:org:eurocris:cerif-1.6-2}cfId__Type"/>
 *         &lt;element name="cfClassSchemeId2" type="{urn:xmlns:org:eurocris:cerif-1.6-2}cfId__Type"/>
 *         &lt;group ref="{urn:xmlns:org:eurocris:cerif-1.6-2}cfCoreClassWithFraction__Group"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cfClass_Class__Type", propOrder = {
    "cfClassId1",
    "cfClassId2",
    "cfClassSchemeId1",
    "cfClassSchemeId2",
    "cfClassId",
    "cfClassSchemeId",
    "cfStartDate",
    "cfEndDate",
    "cfFraction"
})
public class CfClassClassType {

    @XmlElement(required = true)
    protected String cfClassId1;
    @XmlElement(required = true)
    protected String cfClassId2;
    @XmlElement(required = true)
    protected String cfClassSchemeId1;
    @XmlElement(required = true)
    protected String cfClassSchemeId2;
    @XmlElement(required = true)
    protected String cfClassId;
    @XmlElement(required = true)
    protected String cfClassSchemeId;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar cfStartDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar cfEndDate;
    protected Float cfFraction;

    /**
     * Gets the value of the cfClassId1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfClassId1() {
        return cfClassId1;
    }

    /**
     * Sets the value of the cfClassId1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfClassId1(String value) {
        this.cfClassId1 = value;
    }

    /**
     * Gets the value of the cfClassId2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfClassId2() {
        return cfClassId2;
    }

    /**
     * Sets the value of the cfClassId2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfClassId2(String value) {
        this.cfClassId2 = value;
    }

    /**
     * Gets the value of the cfClassSchemeId1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfClassSchemeId1() {
        return cfClassSchemeId1;
    }

    /**
     * Sets the value of the cfClassSchemeId1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfClassSchemeId1(String value) {
        this.cfClassSchemeId1 = value;
    }

    /**
     * Gets the value of the cfClassSchemeId2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfClassSchemeId2() {
        return cfClassSchemeId2;
    }

    /**
     * Sets the value of the cfClassSchemeId2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfClassSchemeId2(String value) {
        this.cfClassSchemeId2 = value;
    }

    /**
     * Gets the value of the cfClassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfClassId() {
        return cfClassId;
    }

    /**
     * Sets the value of the cfClassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfClassId(String value) {
        this.cfClassId = value;
    }

    /**
     * Gets the value of the cfClassSchemeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfClassSchemeId() {
        return cfClassSchemeId;
    }

    /**
     * Sets the value of the cfClassSchemeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfClassSchemeId(String value) {
        this.cfClassSchemeId = value;
    }

    /**
     * Gets the value of the cfStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCfStartDate() {
        return cfStartDate;
    }

    /**
     * Sets the value of the cfStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCfStartDate(XMLGregorianCalendar value) {
        this.cfStartDate = value;
    }

    /**
     * Gets the value of the cfEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCfEndDate() {
        return cfEndDate;
    }

    /**
     * Sets the value of the cfEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCfEndDate(XMLGregorianCalendar value) {
        this.cfEndDate = value;
    }

    /**
     * Gets the value of the cfFraction property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCfFraction() {
        return cfFraction;
    }

    /**
     * Sets the value of the cfFraction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCfFraction(Float value) {
        this.cfFraction = value;
    }

}
