package se.montesmites.ekonomi.report.xml;

import se.montesmites.ekonomi.report.CompactSectionDecorator;
import se.montesmites.ekonomi.report.SectionDecorator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

@XmlTransient
abstract class XmlSectionCommonJAXB {
    private List<SectionDecorator> decorators;

    @XmlElements({
            @XmlElement(name = "compact-section", type = CompactSectionDecorator.class)
    })

    List<SectionDecorator> getDecorators() {
        if (decorators == null) {
            this.decorators = new ArrayList<>();
        }
        return this.decorators;
    }

    void setDecorators(List<SectionDecorator> decorators) {
        this.decorators = decorators;
    }
}
