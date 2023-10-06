package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import se.montesmites.ekonomi.db.AccountWithQualifierAndName;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.service.ChartOfAccountsService;
import se.montesmites.ekonomi.session.SessionAccessor;
import se.montesmites.ekonomi.ui.layout.MainLayout;

@Route(value = ChartOfAccountsView.ROUTE, layout = MainLayout.class)
public class ChartOfAccountsView extends VerticalLayout implements Translator, HasDynamicTitle {

  public static final String ROUTE = "chart-of-accounts";

  public ChartOfAccountsView(
      ChartOfAccountsService chartOfAccountsService, SessionAccessor sessionAccessor) {
    var grid = new Grid<>(AccountWithQualifierAndName.class);

    grid.addColumn(AccountWithQualifierAndName::qualifier);
    grid.addColumn(AccountWithQualifierAndName::name);

    sessionAccessor
        .fiscalYear()
        .ifPresent(
            fiscalYear ->
                grid.setItems(
                    chartOfAccountsService.findAllByFiscalYear(fiscalYear.calendarYear())));

    this.add(grid);
  }

  @Override
  public String getPageTitle() {
    return t(Dictionary.CHART_OF_ACCOUNTS);
  }
}
