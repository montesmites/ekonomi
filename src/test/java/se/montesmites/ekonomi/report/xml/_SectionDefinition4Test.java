package se.montesmites.ekonomi.report.xml;

import java.util.List;

class _SectionDefinition4Test {
    private final String description;
    private final List<_RowDefinition4Test> rows;

    _SectionDefinition4Test(String description, List<_RowDefinition4Test> rows) {
        this.description = description;
        this.rows = rows;
    }

    public String getDescription() {
        return description;
    }

    public List<_RowDefinition4Test> getRows() {
        return rows;
    }
}
