package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import se.montesmites.ekonomi.ReportGenerator;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.ui.layout.MainLayout;

@Route(value = GenerateCashflowReportView.ROUTE, layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class GenerateCashflowReportView extends VerticalLayout
    implements Translator, HasDynamicTitle {

  public static final String ROUTE = "generate-cashflow-report";

  public GenerateCashflowReportView(ReportGenerator reportGenerator) {
    var generateCashflowReportButton = new Button(t(Dictionary.GENERATE_CASHFLOW_REPORT));
    generateCashflowReportButton.addClickListener(
        click -> reportGenerator.generateReportAndRenderToFile());

    add(generateCashflowReportButton);
  }

  @Override
  public String getPageTitle() {
    return t(Dictionary.GENERATE_CASHFLOW_REPORT);
  }
}
