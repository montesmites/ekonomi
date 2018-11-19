package se.montesmites.ekonomi.report.xml;

import se.montesmites.ekonomi.report.SectionDecorator;

import java.util.ArrayList;
import java.util.List;

public class XmlSectionBuilder {
    private final String description;
    private final List<SectionDecorator> decorators;
    private final List<XmlRowBuilder> bodyRowBuilders;

    public XmlSectionBuilder(String description) {
        this.description = description;
        this.decorators = new ArrayList<>();
        this.bodyRowBuilders = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void addBodyRowBuilder(XmlRowBuilder rowBuilder) {
        this.bodyRowBuilders.add(rowBuilder);
    }

    public List<XmlRowBuilder> getBodyRowBuilders() {
        return bodyRowBuilders;
    }

    public List<SectionDecorator> getDecorators() {
        return decorators;
    }

    public void addSectionDecorator(SectionDecorator decorator) {
        this.decorators.add(decorator);
    }
}
