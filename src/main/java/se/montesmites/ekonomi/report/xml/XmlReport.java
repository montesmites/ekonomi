package se.montesmites.ekonomi.report.xml;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

class XmlReport {
    private List<XmlSection> sections;

    @XmlElement(name = "section")
    List<XmlSection> getSections() {
        return sections;
    }

    public void setSections(List<XmlSection> sections) {
        this.sections = sections;
    }
}
