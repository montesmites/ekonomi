package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.service.ChartOfAccountsService;
import se.montesmites.ekonomi.ui.layout.MainLayout;

@Route(value = "chart-of-accounts", layout = MainLayout.class)
public class ChartOfAccountsView extends VerticalLayout {

  public ChartOfAccountsView(ChartOfAccountsService chartOfAccountsService) {
    var grid = new Grid<>(Account.class);

    grid.addColumn(Account::accountId);
    grid.addColumn(Account::description);
    grid.addColumn(Account::accountStatus);

    grid.setItems(chartOfAccountsService.findAll());

    this.add(grid);
  }
}
