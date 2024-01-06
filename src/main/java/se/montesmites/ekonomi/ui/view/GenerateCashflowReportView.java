package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import se.montesmites.ekonomi.configuration.EkonomiProperties;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.report.DataFetcher;
import se.montesmites.ekonomi.report.xml.JaxbReportBuilder;
import se.montesmites.ekonomi.sie.file.SieToOrganizationConverter;
import se.montesmites.ekonomi.ui.layout.MainLayout;

@Route(value = GenerateCashflowReportView.ROUTE, layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class GenerateCashflowReportView extends VerticalLayout
    implements Translator, HasDynamicTitle {

  public static final String ROUTE = "generate-cashflow-report";

  private static final String BASE_PATH_FOR_PROJECT =
      "C:\\ws\\dev\\git\\ekonomi\\src\\main\\resources";
  private static final String SIE_FILE_PATH =
      "C:\\ProgramData\\SPCS\\SPCS Administration\\Företag\\nikka\\sie\\2023_sie4_transaktioner-och-balanser.SE";

  private final TextField titleTextField;
  private final TextField templatePathTextField;
  private final TextField outputDirTextField;
  private final TextField siePathTextField;
  private final TextField fiscalYearTextField;

  public GenerateCashflowReportView(EkonomiProperties properties) {
    this.titleTextField = new TextField(t(Dictionary.TITLE));
    titleTextField.setWidthFull();
    titleTextField.setValue(properties.getReport().getTitle());

    this.templatePathTextField = new TextField(t(Dictionary.TEMPLATE_PATH));
    templatePathTextField.setWidthFull();
    templatePathTextField.setValue(
        BASE_PATH_FOR_PROJECT + properties.getReport().getTemplate().getPath());

    this.outputDirTextField = new TextField(t(Dictionary.OUTPUT_PATH));
    outputDirTextField.setWidthFull();
    outputDirTextField.setValue(properties.getReport().getOutputDir());

    this.fiscalYearTextField = new TextField(t(Dictionary.FISCAL_YEAR));
    fiscalYearTextField.setWidthFull();

    this.siePathTextField = new TextField(t(Dictionary.SIE_PATH));
    siePathTextField.setWidthFull();
    siePathTextField.addValueChangeListener(
        changeEvent -> {
          var path = changeEvent.getValue();
          var yearRegex = Pattern.compile("\\d{4}");
          var matcher = yearRegex.matcher(path);
          matcher
              .results()
              .findAny()
              .ifPresent(match -> fiscalYearTextField.setValue(match.group()));
        });
    siePathTextField.setValue(SIE_FILE_PATH);

    var generateCashflowReportButton = new Button(t(Dictionary.GENERATE_CASHFLOW_REPORT));
    generateCashflowReportButton.addClickListener(__ -> generateReportAndRenderToFile());

    add(
        titleTextField,
        templatePathTextField,
        outputDirTextField,
        siePathTextField,
        fiscalYearTextField,
        generateCashflowReportButton);
  }

  private void generateReportAndRenderToFile() {
    var organization =
        SieToOrganizationConverter.of().convert(Paths.get(siePathTextField.getValue()));
    var reportBuilder = new JaxbReportBuilder(Paths.get(templatePathTextField.getValue()));
    var dataFetcher = new DataFetcher(organization);
    var report = reportBuilder.report(dataFetcher, Year.parse(fiscalYearTextField.getValue()));
    var outputPath =
        outputPath(
            Paths.get(outputDirTextField.getValue()),
            titleTextField.getValue(),
            Year.parse(fiscalYearTextField.getValue()));
    report.renderToFile(report, outputPath);
  }

  private Path outputPath(Path outputDir, String title, Year year) {
    var pathFormat = "%s %s %d.txt";
    return outputDir.resolve(
        String.format(
            pathFormat,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")),
            title,
            year.getValue()));
  }

  @Override
  public String getPageTitle() {
    return t(Dictionary.GENERATE_CASHFLOW_REPORT);
  }
}
