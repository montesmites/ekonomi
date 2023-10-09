package se.montesmites.ekonomi.ui.view.journal;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.stream.Stream;
import se.montesmites.ekonomi.db.EventData;
import se.montesmites.ekonomi.db.EventEntity;
import se.montesmites.ekonomi.db.FiscalYearData;
import se.montesmites.ekonomi.endpoint.journal.JournalEndpoint;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.session.SessionAccessor;
import se.montesmites.ekonomi.ui.layout.MainLayout;
import se.montesmites.ekonomi.ui.view.event.EventView;

@Route(value = JournalView.ROUTE, layout = MainLayout.class)
public class JournalView extends VerticalLayout implements Translator, HasDynamicTitle {

  public static final String ROUTE = "journal";

  private final JournalEndpoint journalEndpoint;

  public JournalView(JournalEndpoint journalEndpoint, SessionAccessor sessionAccessor) {
    this.journalEndpoint = journalEndpoint;
    sessionAccessor.fiscalYear().ifPresent(fiscalYear -> add(gridOfEvents(fiscalYear)));
  }

  @Override
  public String getPageTitle() {
    return t(Dictionary.JOURNAL);
  }

  private Grid<EventData> gridOfEvents(FiscalYearData fiscalYear) {
    var grid = new Grid<>(EventData.class, false);

    grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    grid.setMultiSort(true);

    var eventIdColumn =
        grid.addColumn(EventData::eventId)
            .setHeader(t(Dictionary.EVENT_ID))
            .setKey(EventEntity.EVENT_ID_PROPERTY_NAME)
            .setSortable(true);
    var dateColumn =
        grid.addColumn(EventData::date)
            .setHeader(t(Dictionary.DATE))
            .setKey(EventEntity.DATE_PROPERTY_NAME)
            .setSortable(true);
    grid.addColumn(EventData::description)
        .setHeader(t(Dictionary.DESCRIPTION))
        .setKey(EventEntity.DESCRIPTION_PROPERTY_NAME)
        .setSortable(true);
    grid.addItemClickListener(
        eventDataItemClickEvent -> {
          var event = eventDataItemClickEvent.getItem();
          getUI().ifPresent(ui -> ui.navigate(EventView.class, event.eventId()));
        });

    grid.sort(GridSortOrder.asc(dateColumn).thenAsc(eventIdColumn).build());
    grid.setDataProvider(
        new AbstractBackEndDataProvider<>() {
          @Override
          protected Stream<EventData> fetchFromBackEnd(Query<EventData, Object> query) {
            return journalEndpoint.findPageOfEventsByFiscalYear(
                fiscalYear, VaadinSpringDataHelpers.toSpringPageRequest(query));
          }

          @Override
          protected int sizeInBackEnd(Query<EventData, Object> query) {
            return journalEndpoint.countEventsByFiscalYear(fiscalYear);
          }
        });

    return grid;
  }
}
