package se.montesmites.ekonomi.ui.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.RouterLink;
import se.montesmites.ekonomi.ui.view.GenerateCashflowReportView;

public class MainLayout extends AppLayout {

  public MainLayout() {
    var toggle = new DrawerToggle();

    var title = new H1("Accounting");

    addToDrawer(getTabs());
    addToNavbar(toggle, title);
  }

  private Tab[] getTabs() {
    return new Tab[] {createTab(VaadinIcon.PRINT, "Generate cashflow report")};
  }

  private Tab createTab(VaadinIcon viewIcon, String viewName) {
    var icon = viewIcon.create();
    icon.getStyle()
        .set("box-sizing", "border-box")
        .set("margin-inline-end", "var(--lumo-space-m)")
        .set("padding", "var(--lumo-space-xs)");

    var link = new RouterLink();
    link.add(icon, new Span(viewName));
    link.setRoute(GenerateCashflowReportView.class);

    return new Tab(link);
  }
}
