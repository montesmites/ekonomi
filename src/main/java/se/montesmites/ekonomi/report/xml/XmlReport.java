package se.montesmites.ekonomi.report.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

class XmlReport {
    private String description;
    private List<XmlSection> sections;

    @XmlAttribute
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "section")
    List<XmlSection> getSections() {
        return sections;
    }

    public void setSections(List<XmlSection> sections) {
        this.sections = sections;
    }
}
