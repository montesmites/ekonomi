package se.montesmites.ekonomi.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import java.util.stream.Stream;
import org.springframework.data.domain.PageRequest;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
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
              var grid = new Grid<Event>();
              grid.addColumn(Event::date);
              grid.addColumn(Event::description);

              grid.setDataProvider(
                  new AbstractBackEndDataProvider<>() {
                    @Override
                    protected Stream<Event> fetchFromBackEnd(Query<Event, Object> query) {
                      return journalEndpoint.findPageOfEventsByFiscalYear(
                          fiscalYear, PageRequest.of(query.getPage(), query.getPageSize()));
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
