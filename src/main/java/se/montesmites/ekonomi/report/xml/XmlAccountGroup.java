package se.montesmites.ekonomi.report.xml;

import se.montesmites.ekonomi.report.*;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.function.Function;

public class XmlAccountGroup implements XmlAccountGroupSupplier {
    private String id;
    private String description;
    private String regex;

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    RowBuilder toRowBuilder(CashflowDataFetcher fetcher, java.time.Year year) {
        return new RowBuilder(fetcher, new AccountFilterByRegex(regex), year, description);
    }

    @Override
    public XmlAccountGroup get(Function<String, XmlAccountGroup> accountGroups) {
        return this;
    }
}
