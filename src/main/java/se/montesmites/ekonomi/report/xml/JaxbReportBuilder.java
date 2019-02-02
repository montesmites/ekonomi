package se.montesmites.ekonomi.report.xml;

import static java.util.stream.Collectors.toList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import javax.xml.bind.JAXBContext;
import se.montesmites.ekonomi.jaxb.model.AccountGroups;
import se.montesmites.ekonomi.jaxb.model.Definition;
import se.montesmites.ekonomi.jaxb.model.Subtotal;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AccountsFetcher;
import se.montesmites.ekonomi.report.AmountsFetcher;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.TagFilter;
import se.montesmites.ekonomi.report.builder.ReportBuilder;

class JaxbReportBuilder {

  private final Path pathToXmlDefinition;

  JaxbReportBuilder(Path pathToXmlDefinition) {
    this.pathToXmlDefinition = pathToXmlDefinition;
  }

  Report report(AmountsFetcher amountsFetcher, Year year) {
    return report(AccountsFetcher.empty(), amountsFetcher, year);
  }

  private Report report(AccountsFetcher accountsFetcher, AmountsFetcher amountsFetcher, Year year) {
    var definition = readReportDefinition();
    var reportBuilder = new ReportBuilder(accountsFetcher, amountsFetcher, year);
    for (var constituent : definition.getReport().getReportConstituent()) {
      if (constituent instanceof AccountGroups) {
        var accountGroups = (AccountGroups) constituent;
        reportBuilder.accountGroups(
            accountGroups.getDescription(),
            accountGroups
                .getAccountGroup()
                .stream()
                .map(
                    accountGroup ->
                        AccountGroup.of(accountGroup.getDescription(), accountGroup.getRegex()))
                .collect(toList()));
      } else if (constituent instanceof Subtotal) {
        var subtotal = (Subtotal) constituent;
        reportBuilder.subtotal(subtotal.getDescription(), TagFilter.any());
      }
    }
    return reportBuilder.report();
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
}
