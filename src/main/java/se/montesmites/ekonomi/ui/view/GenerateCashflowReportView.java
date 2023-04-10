package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import se.montesmites.ekonomi.ReportGenerator;
import se.montesmites.ekonomi.ui.layout.MainLayout;

@Route(value = "generate-cashflow-report", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class GenerateCashflowReportView extends VerticalLayout {

  public GenerateCashflowReportView(ReportGenerator reportGenerator) {
    var generateCashflowReportButton = new Button("Generate cashflow report");
    generateCashflowReportButton.addClickListener(click -> reportGenerator.run());

    add(generateCashflowReportButton);
  }
}
