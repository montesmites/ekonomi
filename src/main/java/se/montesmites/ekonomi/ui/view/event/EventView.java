package se.montesmites.ekonomi.ui.view.event;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import java.time.format.DateTimeFormatter;
import se.montesmites.ekonomi.endpoint.event.EventViewEndpoint;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.ui.layout.MainLayout;

@Route(value = EventView.ROUTE, layout = MainLayout.class)
public class EventView extends VerticalLayout
    implements Translator, HasDynamicTitle, HasUrlParameter<Long> {

  public static final String ROUTE = "event";

  private final EventViewEndpoint eventViewEndpoint;

  private final TextField eventId = new TextField();
  private final TextField fiscalYearId = new TextField();
  private final TextField eventNo = new TextField();
  private final TextField date = new TextField();
  private final TextField description = new TextField();
  private final EntryGrid entryGrid;

  private Long eventIdParam;

  public EventView(EventViewEndpoint eventViewEndpoint) {
    this.eventViewEndpoint = eventViewEndpoint;
    this.entryGrid = new EntryGrid(eventViewEndpoint);

    eventId.setLabel(t(Dictionary.EVENT_ID));
    fiscalYearId.setLabel(t(Dictionary.FISCAL_YEAR_ID));
    eventNo.setLabel(t(Dictionary.EVENT_NO));
    date.setLabel(t(Dictionary.DATE));
    description.setLabel(t(Dictionary.DESCRIPTION));

    var eventLayout = new VerticalLayout(eventId, fiscalYearId, eventNo, date, description);

    eventLayout.setSizeUndefined();
    entryGrid.setSizeUndefined();

    var layout = new HorizontalLayout();
    layout.setPadding(false);
    layout.setMargin(false);
    layout.add(eventLayout, entryGrid);
    layout.setFlexGrow(0.0, eventLayout);
    layout.setFlexGrow(1.0, entryGrid);
    layout.setWidthFull();

    add(layout);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    var maybeEvent = eventViewEndpoint.findByEventId(this.eventIdParam);
    maybeEvent.ifPresent(
        event -> {
          this.eventId.setValue(String.valueOf(event.eventId()));
          this.fiscalYearId.setValue(String.valueOf(event.fiscalYearId()));
          this.eventNo.setValue(String.valueOf(event.eventNo()));
          this.date.setValue(DateTimeFormatter.ISO_DATE.format(event.date()));
          this.description.setValue(event.description());

          this.entryGrid.setEntries(event);
        });
  }

  @Override
  public String getPageTitle() {
    return t(Dictionary.EVENT);
  }

  @Override
  public void setParameter(BeforeEvent beforeEvent, Long eventId) {
    this.eventIdParam = eventId;
  }
}
