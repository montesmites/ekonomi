package se.montesmites.ekonomi.report.xml;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.function.Function;

class XmlSectionRef implements XmlSectionSupplier {
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
        return sections.apply(id);
    }
}
