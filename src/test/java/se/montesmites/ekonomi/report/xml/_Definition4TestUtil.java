package se.montesmites.ekonomi.report.xml;

import java.util.List;

class _Definition4TestUtil {
    static _ReportDefinition4Test definition(String description, List<_SectionDefinition4Test> sections) {
        return new _ReportDefinition4Test(description, sections);
    }

    static _SectionDefinition4Test section(String description, List<_RowDefinition4Test> rows) {
        return new _SectionDefinition4Test(description, rows);
    }

    static _RowDefinition4Test row(String description, String regex) {
        return new _RowDefinition4Test(description, regex);
    }
}
