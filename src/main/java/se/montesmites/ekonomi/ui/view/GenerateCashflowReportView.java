package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import se.montesmites.ekonomi.ReportGenerator;

@Route("generate-cashflow-report")
@RouteAlias("")
public class GenerateCashflowReportView extends VerticalLayout {

  public GenerateCashflowReportView(ReportGenerator reportGenerator) {
    var generateCashflowReportButton = new Button("Generate cashflow report");
    generateCashflowReportButton.addClickListener(click -> reportGenerator.run());

    add(generateCashflowReportButton);
  }
}
