//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.07.23 at 10:18:08 AM CEST 
//


package se.montesmites.ekonomi.jaxb.model;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{se/montesmites/ekonomi/report-definition}report"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "report"
})
@XmlRootElement(name = "definition")
public class Definition {

    @XmlElement(required = true)
    protected Report report;

    /**
     * Gets the value of the report property.
     *
     * @return possible object is
     * {@link Report }
     */
    public Report getReport() {
        return report;
    }

    /**
     * Sets the value of the report property.
     *
     * @param value allowed object is
     *              {@link Report }
     */
    public void setReport(Report value) {
        this.report = value;
    }

}
