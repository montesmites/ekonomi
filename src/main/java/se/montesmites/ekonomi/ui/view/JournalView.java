package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.time.LocalDate;
import java.util.stream.Stream;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.jpa.model.Ver;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.service.JournalEndpoint;
import se.montesmites.ekonomi.session.SessionAccessor;
import se.montesmites.ekonomi.ui.layout.MainLayout;

@Route(value = JournalView.ROUTE, layout = MainLayout.class)
public class JournalView extends VerticalLayout implements Translator, HasDynamicTitle {

  public static final String ROUTE = "journal";

  public JournalView(JournalEndpoint journalEndpoint, SessionAccessor sessionAccessor) {
    sessionAccessor
        .fiscalYear()
        .ifPresent(
            fiscalYear -> {
              var grid = new TreeGrid<>(Row.class);
              grid.setMultiSort(true);
              var eventIdColumn =
                  grid.addHierarchyColumn(Row::first)
                      .setHeader(t(Dictionary.EVENT_ID))
                      .setKey(Ver.EVENT_ID_PROPERTY_NAME)
                      .setSortable(true);
              var dateColumn =
                  grid.addColumn(Row::second)
                      .setHeader(t(Dictionary.DATE))
                      .setKey(Ver.DATE_PROPERTY_NAME)
                      .setSortable(true);
              grid.addColumn(Row::third)
                  .setHeader(t(Dictionary.DESCRIPTION))
                  .setKey(Ver.DESCRIPTION_PROPERTY_NAME)
                  .setSortable(true);

              grid.sort(GridSortOrder.asc(dateColumn).thenAsc(eventIdColumn).build());
              var rootCount = journalEndpoint.countEventsByFiscalYear(fiscalYear);
              grid.setDataProvider(
                  new AbstractBackEndHierarchicalDataProvider<>() {
                    @Override
                    public int getChildCount(HierarchicalQuery<Row, Object> query) {
                      return switch (query.getParent()) {
                        case null -> rootCount;
                        case EventRow event -> journalEndpoint.countVerradByEventId(
                            event.event().eventId());
                        default -> 0;
                      };
                    }

                    @Override
                    public boolean hasChildren(Row row) {
                      return switch (row) {
                        case EventRow event -> journalEndpoint.countVerradByEventId(
                                event.event().eventId())
                            > 0;
                        default -> false;
                      };
                    }

                    @Override
                    protected Stream<Row> fetchChildrenFromBackEnd(
                        HierarchicalQuery<Row, Object> query) {
                      return switch (query.getParent()) {
                        case null -> journalEndpoint
                            .findPageOfEventsByFiscalYear(
                                fiscalYear, VaadinSpringDataHelpers.toSpringPageRequest(query))
                            .map(Row::from);
                        case EventRow event -> journalEndpoint
                            .findVerradByEventId(event.event().eventId())
                            .map(Row::from);
                        default -> Stream.empty();
                      };
                    }
                  });

              add(grid);
            });
  }

  @Override
  public String getPageTitle() {
    return t(Dictionary.JOURNAL);
  }

  private sealed interface Row {
    @SuppressWarnings("unused")
    Event event();

    @SuppressWarnings("unused")
    Entry entry();

    Object first();

    Object second();

    Object third();

    private static Row from(Event event) {
      return new EventRow(event, event.eventId().id(), event.date(), event.description());
    }

    private static Row from(Entry entry) {
      return new EntryRow(entry, entry.accountId().id(), entry.amount());
    }
  }

  private record EventRow(Event event, int eventId, LocalDate date, String description)
      implements Row {

    @Override
    public Entry entry() {
      return null;
    }

    @Override
    public Object first() {
      return eventId;
    }

    @Override
    public Object second() {
      return date;
    }

    @Override
    public Object third() {
      return description;
    }
  }

  private record EntryRow(Entry entry, String accountId, Currency amount) implements Row {

    @Override
    public Event event() {
      return null;
    }

    @Override
    public Object first() {
      return accountId;
    }

    @Override
    public Object second() {
      return amount.amount() / 100;
    }

    @Override
    public Object third() {
      return null;
    }
  }
}
