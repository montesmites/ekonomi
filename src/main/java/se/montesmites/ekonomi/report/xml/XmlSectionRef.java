package se.montesmites.ekonomi.report.xml;

import java.util.function.Function;
import javax.xml.bind.annotation.XmlAttribute;

class XmlSectionRef extends XmlSectionCommonJAXB implements XmlSectionSupplier {

  private String id;

  @XmlAttribute
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public XmlSection get(Function<String, XmlSection> sections) {
    var section = sections.apply(id);
    return section;
  }
}
