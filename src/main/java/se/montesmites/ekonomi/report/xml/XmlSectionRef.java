package se.montesmites.ekonomi.report.xml;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.function.Function;

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
        section.getDecorators().addAll(getDecorators());
        return section;
    }
}
