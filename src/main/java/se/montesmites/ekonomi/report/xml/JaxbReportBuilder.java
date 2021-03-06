package se.montesmites.ekonomi.report.xml;

import static java.util.stream.Collectors.toList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.List;
import javax.xml.bind.JAXBContext;
import se.montesmites.ekonomi.jaxb.model.AccountGroups;
import se.montesmites.ekonomi.jaxb.model.AccumulateAccountGroups;
import se.montesmites.ekonomi.jaxb.model.Body;
import se.montesmites.ekonomi.jaxb.model.Definition;
import se.montesmites.ekonomi.jaxb.model.Section;
import se.montesmites.ekonomi.jaxb.model.Subtotal;
import se.montesmites.ekonomi.jaxb.model.Subtotal.Addenda;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AccountsFetcher;
import se.montesmites.ekonomi.report.AmountsFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.builder.BodyBuilder;
import se.montesmites.ekonomi.report.builder.ReportBuilder;
import se.montesmites.ekonomi.report.builder.SectionBuilder;

public class JaxbReportBuilder {

  private final Path pathToXmlDefinition;

  public JaxbReportBuilder(Path pathToXmlDefinition) {
    this.pathToXmlDefinition = pathToXmlDefinition;
  }

  public Report report(AmountsFetcher amountsFetcher, Year year) {
    return report(AccountsFetcher.empty(), amountsFetcher, year);
  }

  private Report report(AccountsFetcher accountsFetcher, AmountsFetcher amountsFetcher, Year year) {
    var definition = readReportDefinition();
    var reportBuilder = new ReportBuilder(accountsFetcher, amountsFetcher, year);
    for (var constituent : definition.getReport().getReportConstituent()) {
      if (constituent instanceof AccountGroups) {
        var accountGroups = (AccountGroups) constituent;
        reportBuilder.accountGroups(
            accountGroups.getDescription(), convertAccountGroups(accountGroups));
      } else if (constituent instanceof Subtotal) {
        var subtotal = (Subtotal) constituent;
        reportBuilder.subtotal(
            sbttl ->
                sbttl
                    .description(subtotal.getDescription().toUpperCase())
                    .addenda(convertAddenda(subtotal.getAddenda(), amountsFetcher, year)));
      } else if (constituent instanceof Section) {
        var section = (Section) constituent;
        reportBuilder.section(sectionBuilder -> buildSection(section, sectionBuilder));
      } else if (constituent instanceof AccumulateAccountGroups) {
        var accumulation = (AccumulateAccountGroups) constituent;
        reportBuilder.accumulateAccountGroups(
            accumulation.getDescription(), convertAccountGroups(accumulation.getAccountGroup()));
      }
    }
    return reportBuilder.report();
  }

  private List<AccountGroup> convertAccountGroups(AccountGroups accountGroups) {
    return convertAccountGroups(accountGroups.getAccountGroup());
  }

  private List<AccountGroup> convertAccountGroups(
      List<se.montesmites.ekonomi.jaxb.model.AccountGroup> accountGroups) {
    return accountGroups.stream().map(this::convertAccountGroup).collect(toList());
  }

  private AccountGroup convertAccountGroup(
      se.montesmites.ekonomi.jaxb.model.AccountGroup jaxbAccountGroup) {
    var accountGroup =
        AccountGroup.of(jaxbAccountGroup.getDescription(), jaxbAccountGroup.getRegex());
    if (jaxbAccountGroup.getAccountGroupPostProcessor().size() > 0) {
      accountGroup = accountGroup.postProcessor(AmountsProvider::negate);
    }
    return accountGroup;
  }

  private List<AmountsProvider> convertAddenda(
      Addenda addenda, AmountsFetcher amountsFetcher, Year year) {
    return addenda == null
        ? List.of()
        : addenda
            .getAccountGroup()
            .stream()
            .flatMap(__ -> addenda.getAccountGroup().stream())
            .map(
                accountGroup ->
                    AmountsProvider.of(amountsFetcher, year, convertAccountGroup(accountGroup)))
            .collect(toList());
  }

  private Definition readReportDefinition() {
    try {
      var jaxbContext = JAXBContext.newInstance(Definition.class);
      var unmarshaller = jaxbContext.createUnmarshaller();
      return (Definition) unmarshaller.unmarshal(Files.newInputStream(pathToXmlDefinition));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private SectionBuilder buildSection(Section section, SectionBuilder sectionBuilder) {
    var body = section.getBody();
    return sectionBuilder.body(bodyBuilder -> buildBody(body, bodyBuilder));
  }

  private BodyBuilder buildBody(Body body, BodyBuilder bodyBuilder) {
    return bodyBuilder.accountGroups(convertAccountGroups(body.getAccountGroups()));
  }
}
