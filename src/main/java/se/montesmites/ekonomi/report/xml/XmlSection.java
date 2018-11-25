package se.montesmites.ekonomi.report.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import se.montesmites.ekonomi.report.CashflowDataFetcher;

public class XmlSection extends XmlSectionCommonJAXB implements XmlSectionSupplier {

  private String id;
  private String description;
  private List<XmlAccountGroupSupplier> accountGroupSuppliers;

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

  @XmlElements({
    @XmlElement(name = "account-group", type = XmlAccountGroup.class),
    @XmlElement(name = "account-group-ref", type = XmlAccountGroupRef.class)
  })
  private List<XmlAccountGroupSupplier> getAccountGroupSuppliers() {
    if (accountGroupSuppliers == null) {
      this.accountGroupSuppliers = new ArrayList<>();
    }
    return this.accountGroupSuppliers;
  }

  public void setAccountGroupSuppliers(List<XmlAccountGroupSupplier> accountGroupSuppliers) {
    this.accountGroupSuppliers = accountGroupSuppliers;
  }

  XmlSectionBuilder toSectionBuilder(
      CashflowDataFetcher fetcher,
      java.time.Year year,
      Function<String, XmlAccountGroup> accountGroupsMap) {
    var builder = new XmlSectionBuilder(description);
    var accountGroups =
        getAccountGroupSuppliers().stream().map(accountGroup -> accountGroup.get(accountGroupsMap));
    accountGroups
        .map(group -> group.toRowBuilder(fetcher, year))
        .forEach(builder::addBodyRowBuilder);
    return builder;
  }

  @Override
  public XmlSection get(Function<String, XmlSection> sections) {
    return this;
  }
}
