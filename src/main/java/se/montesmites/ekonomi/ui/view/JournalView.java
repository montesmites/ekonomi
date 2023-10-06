package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.math.BigDecimal;
import java.util.stream.Stream;
import se.montesmites.ekonomi.db.EntryData;
import se.montesmites.ekonomi.db.EventData;
import se.montesmites.ekonomi.db.EventEntity;
import se.montesmites.ekonomi.db.FiscalYearData;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.service.JournalEndpoint;
import se.montesmites.ekonomi.session.SessionAccessor;
import se.montesmites.ekonomi.ui.layout.MainLayout;

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
    grid.setItemDetailsRenderer(entriesRenderer());

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

  private ComponentRenderer<EntryGrid, EventData> entriesRenderer() {
    return new ComponentRenderer<>(() -> new EntryGrid(journalEndpoint), EntryGrid::setEntries);
  }

  private static final class EntryGrid extends Grid<EntryData> implements Translator {

    private final JournalEndpoint journalEndpoint;

    EntryGrid(JournalEndpoint journalEndpoint) {
      super(EntryData.class, false);

      this.journalEndpoint = journalEndpoint;

      this.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
      this.setAllRowsVisible(true);

      addColumn(EntryData::accountId).setHeader(t(Dictionary.ACCOUNT));
      addColumn(
              entry ->
                  entry.amount().amount().compareTo(BigDecimal.ZERO) >= 0
                      ? entry.amount().format()
                      : null)
          .setHeader(t(Dictionary.DEBIT));
      addColumn(
              entry ->
                  entry.amount().amount().compareTo(BigDecimal.ZERO) < 0
                      ? entry.amount().format()
                      : null)
          .setHeader(t(Dictionary.CREDIT));
    }

    void setEntries(EventData event) {
      var entries = journalEndpoint.findEntriesByEventId(event.eventId());
      setItems(entries);
    }
  }
}
