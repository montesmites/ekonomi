package se.montesmites.ekonomi.report;

import java.util.ArrayList;
import java.util.List;

public class SectionBuilder {
    private final String description;
    private final List<RowBuilder> bodyRowBuilders;

    public SectionBuilder(String description) {
        this.description = description;
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
}
