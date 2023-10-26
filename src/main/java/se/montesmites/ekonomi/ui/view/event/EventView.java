package se.montesmites.ekonomi.ui.view.event;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

    eventId.setEnabled(false);
    fiscalYearId.setEnabled(false);
    eventNo.setEnabled(false);
    date.setEnabled(false);
    description.setEnabled(false);

    var eventLayout = new VerticalLayout(eventId, fiscalYearId, eventNo, date, description);

    eventLayout.setSizeUndefined();
    entryGrid.setSizeUndefined();

    var dataLayout = new HorizontalLayout();
    dataLayout.setPadding(false);
    dataLayout.setMargin(false);
    dataLayout.add(eventLayout, entryGrid);
    dataLayout.setFlexGrow(0.0, eventLayout);
    dataLayout.setFlexGrow(1.0, entryGrid);
    dataLayout.setWidthFull();

    var buttonLayout = new HorizontalLayout();
    var editButton = new Button(t(Dictionary.EDIT));
    editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    var cancelButton = new Button(t(Dictionary.CANCEL));
    cancelButton.setEnabled(false);
    editButton.addClickListener(
        __ -> {
          eventNo.setEnabled(true);
          date.setEnabled(true);
          description.setEnabled(true);
          editButton.setText(t(Dictionary.SAVE));
          cancelButton.setEnabled(true);
        });
    cancelButton.addClickListener(
        __ -> {
          fetchEventId(this.eventIdParam);
          eventNo.setEnabled(false);
          date.setEnabled(false);
          description.setEnabled(false);
          editButton.setText(t(Dictionary.EDIT));
          cancelButton.setEnabled(false);
        });

    buttonLayout.add(editButton, cancelButton);

    add(dataLayout, buttonLayout);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    fetchEventId(this.eventIdParam);
  }

  private void fetchEventId(Long eventId) {
    var maybeEvent = eventViewEndpoint.findByEventId(eventId);
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
