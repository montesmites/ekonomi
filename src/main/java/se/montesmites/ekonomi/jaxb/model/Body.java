//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2020.07.23 at 10:18:08 AM CEST
//

package se.montesmites.ekonomi.jaxb.model;

import javax.xml.bind.annotation.*;

/**
 * Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{se/montesmites/ekonomi/report-definition}account-groups"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"accountGroups"})
@XmlRootElement(name = "body")
public class Body {

  @XmlElement(name = "account-groups", required = true)
  protected AccountGroups accountGroups;

  /**
   * Gets the value of the accountGroups property.
   *
   * @return possible object is {@link AccountGroups }
   */
  public AccountGroups getAccountGroups() {
    return accountGroups;
  }

  /**
   * Sets the value of the accountGroups property.
   *
   * @param value allowed object is {@link AccountGroups }
   */
  public void setAccountGroups(AccountGroups value) {
    this.accountGroups = value;
  }
}
