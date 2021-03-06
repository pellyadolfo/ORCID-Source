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
 * <p>Java class for cfFund_Meas__Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cfFund_Meas__Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cfFundId" type="{urn:xmlns:org:eurocris:cerif-1.6-2}cfId__Type"/>
 *         &lt;element name="cfMeasId" type="{urn:xmlns:org:eurocris:cerif-1.6-2}cfId__Type"/>
 *         &lt;element name="cfClassSchemeId" type="{urn:xmlns:org:eurocris:cerif-1.6-2}cfId__Type"/>
 *         &lt;element name="cfStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="cfEndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="cfFraction" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="cfAmount" type="{urn:xmlns:org:eurocris:cerif-1.6-2}cfAmount__Type" minOccurs="0"/>
 *         &lt;element name="cfClassId" type="{urn:xmlns:org:eurocris:cerif-1.6-2}cfId__Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cfFund_Meas__Type", propOrder = {
    "cfFundId",
    "cfMeasId",
    "cfClassSchemeId",
    "cfStartDate",
    "cfEndDate",
    "cfFraction",
    "cfAmount",
    "cfClassId"
})
public class CfFundMeasType {

    @XmlElement(required = true)
    protected String cfFundId;
    @XmlElement(required = true)
    protected String cfMeasId;
    @XmlElement(required = true)
    protected String cfClassSchemeId;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar cfStartDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar cfEndDate;
    protected Float cfFraction;
    protected CfAmountType cfAmount;
    @XmlElement(required = true)
    protected String cfClassId;

    /**
     * Gets the value of the cfFundId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfFundId() {
        return cfFundId;
    }

    /**
     * Sets the value of the cfFundId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfFundId(String value) {
        this.cfFundId = value;
    }

    /**
     * Gets the value of the cfMeasId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfMeasId() {
        return cfMeasId;
    }

    /**
     * Sets the value of the cfMeasId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfMeasId(String value) {
        this.cfMeasId = value;
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

    /**
     * Gets the value of the cfAmount property.
     * 
     * @return
     *     possible object is
     *     {@link CfAmountType }
     *     
     */
    public CfAmountType getCfAmount() {
        return cfAmount;
    }

    /**
     * Sets the value of the cfAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link CfAmountType }
     *     
     */
    public void setCfAmount(CfAmountType value) {
        this.cfAmount = value;
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

}
