package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import se.montesmites.ekonomi.ReportGenerator;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.ui.layout.MainLayout;

@Route(value = "generate-cashflow-report", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class GenerateCashflowReportView extends VerticalLayout implements Translator {

  public GenerateCashflowReportView(ReportGenerator reportGenerator) {
    var generateCashflowReportButton = new Button(t(Dictionary.GENERATE_CASHFLOW_REPORT));
    generateCashflowReportButton.addClickListener(click -> reportGenerator.run());

    add(generateCashflowReportButton);
  }
}
