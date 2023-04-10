//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2020.07.23 at 10:18:08 AM CEST
//

package se.montesmites.ekonomi.jaxb.model;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{se/montesmites/ekonomi/report-definition}account-group" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"description", "accountGroup"})
@XmlRootElement(name = "accumulate-account-groups")
public class AccumulateAccountGroups {

  @XmlElement(required = true)
  protected String description;

  @XmlElement(name = "account-group")
  protected List<AccountGroup> accountGroup;

  /**
   * Gets the value of the description property.
   *
   * @return possible object is {@link String }
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of the description property.
   *
   * @param value allowed object is {@link String }
   */
  public void setDescription(String value) {
    this.description = value;
  }

  /**
   * Gets the value of the accountGroup property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the accountGroup property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getAccountGroup().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link AccountGroup }
   */
  public List<AccountGroup> getAccountGroup() {
    if (accountGroup == null) {
      accountGroup = new ArrayList<AccountGroup>();
    }
    return this.accountGroup;
  }
}
