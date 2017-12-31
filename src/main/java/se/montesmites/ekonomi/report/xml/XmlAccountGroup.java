package se.montesmites.ekonomi.report.xml;

import se.montesmites.ekonomi.report.*;

public class XmlAccountGroup {
    private String description;
    private String regex;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public RowBuilder toRowBuilder(CashflowDataFetcher fetcher, java.time.Year year) {
        return new RowBuilder(fetcher, new AccountFilterByRegex(regex), year, description);
    }
}
