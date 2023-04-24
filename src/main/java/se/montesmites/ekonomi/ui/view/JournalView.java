package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.stream.Stream;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.jpa.model.Ver;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.Year;
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

  private Grid<Event> gridOfEvents(Year fiscalYear) {
    var grid = new Grid<>(Event.class, false);
    grid.setMultiSort(true);
    var eventIdColumn =
        grid.addColumn(event -> event.eventId().id())
            .setHeader(t(Dictionary.EVENT_ID))
            .setKey(Ver.EVENT_ID_PROPERTY_NAME)
            .setSortable(true);
    var dateColumn =
        grid.addColumn(Event::date)
            .setHeader(t(Dictionary.DATE))
            .setKey(Ver.DATE_PROPERTY_NAME)
            .setSortable(true);
    grid.addColumn(Event::description)
        .setHeader(t(Dictionary.DESCRIPTION))
        .setKey(Ver.DESCRIPTION_PROPERTY_NAME)
        .setSortable(true);
    grid.setItemDetailsRenderer(entriesRenderer());

    grid.sort(GridSortOrder.asc(dateColumn).thenAsc(eventIdColumn).build());
    grid.setDataProvider(
        new AbstractBackEndDataProvider<>() {
          @Override
          protected Stream<Event> fetchFromBackEnd(Query<Event, Object> query) {
            return journalEndpoint.findPageOfEventsByFiscalYear(
                fiscalYear, VaadinSpringDataHelpers.toSpringPageRequest(query));
          }

          @Override
          protected int sizeInBackEnd(Query<Event, Object> query) {
            return journalEndpoint.countEventsByFiscalYear(fiscalYear);
          }
        });

    return grid;
  }

  private ComponentRenderer<EntryGrid, Event> entriesRenderer() {
    return new ComponentRenderer<>(() -> new EntryGrid(journalEndpoint), EntryGrid::setEntries);
  }

  private static final class EntryGrid extends Grid<Entry> implements Translator {

    private final JournalEndpoint journalEndpoint;

    EntryGrid(JournalEndpoint journalEndpoint) {
      super(Entry.class, false);
      this.journalEndpoint = journalEndpoint;
      addColumn(entry -> entry.accountId().id()).setHeader(t(Dictionary.ACCOUNT));
      addColumn(entry -> entry.amount().amount() >= 0 ? entry.amount().format() : null)
          .setHeader(t(Dictionary.DEBIT));
      addColumn(entry -> entry.amount().amount() < 0 ? entry.amount().format() : null)
          .setHeader(t(Dictionary.CREDIT));
    }

    void setEntries(Event event) {
      var entries = journalEndpoint.findVerradByEvent(event);
      setItems(entries);
    }
  }
}
