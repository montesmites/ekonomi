package se.montesmites.ekonomi.ui.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import java.util.List;
import java.util.Optional;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.service.FiscalYearService;
import se.montesmites.ekonomi.session.SessionAccessor;
import se.montesmites.ekonomi.ui.component.FiscalYearSelector;
import se.montesmites.ekonomi.ui.view.ChartOfAccountsView;
import se.montesmites.ekonomi.ui.view.GenerateCashflowReportView;

public class MainLayout extends AppLayout implements Translator {

  private final FiscalYearService fiscalYearService;
  private final SessionAccessor sessionAccessor;

  private final H1 title;
  private final Tabs menu;

  public MainLayout(FiscalYearService fiscalYearService, SessionAccessor sessionAccessor) {
    this.fiscalYearService = fiscalYearService;
    this.sessionAccessor = sessionAccessor;

    this.title = createTitle();
    this.menu = createMenu();

    getElement().getStyle().set("height", "100%");
    setPrimarySection(Section.DRAWER);

    addToNavbar(createHeaderContents());
    addToDrawer(createDrawerContents(this.menu));
  }

  private H1 createTitle() {
    var title = new H1();
    title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");
    title.setWidthFull();
    return title;
  }

  private Component createHeaderContents() {
    var header = new HorizontalLayout();

    header.setId("header");
    header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    header.setWidth("100%");
    header.addClassNames("py-0", "px-m");

    header.expand(this.title);
    header.add(
        new DrawerToggle(),
        this.title,
        new FiscalYearSelector(this.fiscalYearService, this.sessionAccessor));
    header.setAlignItems(FlexComponent.Alignment.BASELINE);

    return header;
  }

  private Component createDrawerContents(Tabs menu) {
    var drawer = new VerticalLayout();
    drawer.add(menu);
    return drawer;
  }

  private Tabs createMenu() {
    var tabs = new Tabs();
    tabs.setOrientation(Tabs.Orientation.VERTICAL);
    tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
    tabs.setId("tabs");
    tabs.add(createMenuItems());
    return tabs;
  }

  private List<NavigationTarget> navigationTargets() {
    return List.of(
        new NavigationTarget(
            t(Dictionary.GENERATE_CASHFLOW_REPORT),
            GenerateCashflowReportView.class,
            VaadinIcon.PRINT),
        new NavigationTarget(
            t(Dictionary.CHART_OF_ACCOUNTS), ChartOfAccountsView.class, VaadinIcon.TABLE));
  }

  private Tab[] createMenuItems() {
    return this.navigationTargets().stream().map(this::createTab).toArray(Tab[]::new);
  }

  private Tab createTab(NavigationTarget navigationTarget) {
    var tab =
        new Tab(
            navigationTarget.iconFactory().create(),
            new RouterLink(navigationTarget.label(), navigationTarget.target()));
    ComponentUtil.setData(tab, Class.class, navigationTarget.target());
    return tab;
  }

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    getTabForComponent(getContent()).ifPresent(this.menu::setSelectedTab);
    title.setText(getCurrentPageTitle());
  }

  private Optional<Tab> getTabForComponent(Component component) {
    return this.menu
        .getChildren()
        .filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
        .findFirst()
        .map(Tab.class::cast);
  }

  private String getCurrentPageTitle() {
    if (getContent() instanceof HasDynamicTitle dynamicTitle) {
      return dynamicTitle.getPageTitle();
    } else {
      return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
  }
}
