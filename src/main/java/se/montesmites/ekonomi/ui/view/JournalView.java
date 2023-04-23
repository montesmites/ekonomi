package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.stream.Stream;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.jpa.model.Ver;
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
              var grid = new Grid<>(Event.class);
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

              add(grid);
            });
  }

  @Override
  public String getPageTitle() {
    return t(Dictionary.JOURNAL);
  }
}
