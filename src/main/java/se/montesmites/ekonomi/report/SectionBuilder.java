package se.montesmites.ekonomi.report;

import java.util.ArrayList;
import java.util.List;

public class SectionBuilder {
    private final String description;
    private final List<SectionDecorator> decorators;
    private final List<RowBuilder> bodyRowBuilders;

    public SectionBuilder(String description) {
        this.description = description;
        this.decorators = new ArrayList<>();
        this.bodyRowBuilders = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void addBodyRowBuilder(RowBuilder rowBuilder) {
        this.bodyRowBuilders.add(rowBuilder);
    }

    public List<RowBuilder> getBodyRowBuilders() {
        return bodyRowBuilders;
    }

    public List<SectionDecorator> getDecorators() {
        return decorators;
    }

    public void addSectionDecorator(SectionDecorator decorator) {
        this.decorators.add(decorator);
    }
}
