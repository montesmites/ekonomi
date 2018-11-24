package se.montesmites.ekonomi.report.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

class XmlReport {

  private String description;
  private List<XmlSectionSupplier> sectionSuppliers;

  @XmlAttribute
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @XmlElements({
    @XmlElement(name = "section", type = XmlSection.class),
    @XmlElement(name = "section-ref", type = XmlSectionRef.class)
  })
  List<XmlSectionSupplier> getSectionSuppliers() {
    if (sectionSuppliers == null) {
      this.sectionSuppliers = new ArrayList<>();
    }
    return this.sectionSuppliers;
  }

  public void setSectionSuppliers(List<XmlSectionSupplier> sectionSuppliers) {
    this.sectionSuppliers = sectionSuppliers;
  }
}
