package se.montesmites.ekonomi.report.xml;

import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.SectionBuilder;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class XmlSection {
    private String id;
    private String description;
    private List<XmlAccountGroup> accountGroups;

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

    @XmlElement(name = "account-group")
    public List<XmlAccountGroup> getAccountGroups() {
        return accountGroups;
    }

    public void setAccountGroups(List<XmlAccountGroup> accountGroups) {
        this.accountGroups = accountGroups;
    }

    SectionBuilder toSectionBuilder(CashflowDataFetcher fetcher, java.time.Year year) {
        final SectionBuilder builder = new SectionBuilder(description);
        accountGroups.stream().map(group -> group.toRowBuilder(fetcher, year)).forEach(builder::addBodyRowBuilder);
        return builder;
    }
}
