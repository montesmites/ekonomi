//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2018.05.01 at 08:23:50 PM CEST
//

package se.montesmites.ekonomi.jaxb.report_definition;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
 *         &lt;sequence>
 *           &lt;element ref="{se/montesmites/ekonomi/report-definition}account-group" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{se/montesmites/ekonomi/report-definition}account-group-ref" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{se/montesmites/ekonomi/report-definition}section-ref" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{se/montesmites/ekonomi/report-definition}section" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"description", "accountGroup", "accountGroupRef", "sectionRef", "section"})
@XmlRootElement(name = "section")
public class Section {

  @XmlElement(required = true)
  protected String description;

  @XmlElement(name = "account-group")
  protected List<AccountGroup> accountGroup;

  @XmlElement(name = "account-group-ref")
  protected List<AccountGroupRef> accountGroupRef;

  @XmlElement(name = "section-ref")
  protected List<SectionRef> sectionRef;

  protected List<Section> section;

  @XmlAttribute(name = "id", required = true)
  protected String id;

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

  /**
   * Gets the value of the accountGroupRef property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the accountGroupRef property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getAccountGroupRef().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link AccountGroupRef }
   */
  public List<AccountGroupRef> getAccountGroupRef() {
    if (accountGroupRef == null) {
      accountGroupRef = new ArrayList<AccountGroupRef>();
    }
    return this.accountGroupRef;
  }

  /**
   * Gets the value of the sectionRef property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the sectionRef property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getSectionRef().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link SectionRef }
   */
  public List<SectionRef> getSectionRef() {
    if (sectionRef == null) {
      sectionRef = new ArrayList<SectionRef>();
    }
    return this.sectionRef;
  }

  /**
   * Gets the value of the section property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the section property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getSection().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link Section }
   */
  public List<Section> getSection() {
    if (section == null) {
      section = new ArrayList<Section>();
    }
    return this.section;
  }

  /**
   * Gets the value of the id property.
   *
   * @return possible object is {@link String }
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of the id property.
   *
   * @param value allowed object is {@link String }
   */
  public void setId(String value) {
    this.id = value;
  }
}
