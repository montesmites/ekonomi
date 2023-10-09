package se.montesmites.ekonomi.ui.view.event;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import java.math.BigDecimal;
import se.montesmites.ekonomi.db.EventData;
import se.montesmites.ekonomi.db.model.EntryDataAndAccountQualifierAndName;
import se.montesmites.ekonomi.endpoint.event.EventViewEndpoint;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;

public class EntryGrid extends Grid<EntryDataAndAccountQualifierAndName> implements Translator {

  private final EventViewEndpoint eventViewEndpoint;

  public EntryGrid(EventViewEndpoint eventViewEndpoint) {
    super(EntryDataAndAccountQualifierAndName.class, false);

    this.eventViewEndpoint = eventViewEndpoint;

    this.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
    this.setAllRowsVisible(true);
    this.setWidthFull();

    addColumn(
            entryDataAndAccountQualifierAndName ->
                entryDataAndAccountQualifierAndName
                    .accountQualifierAndName()
                    .qualifier()
                    .qualifier())
        .setHeader(t(Dictionary.ACCOUNT_QUALIFIER))
        .setFlexGrow(0);
    addColumn(
            entryDataAndAccountQualifierAndName ->
                entryDataAndAccountQualifierAndName.accountQualifierAndName().name())
        .setHeader(t(Dictionary.ACCOUNT_NAME))
        .setFlexGrow(1);
    addColumn(
            entryDataAndAccountQualifierAndName ->
                entryDataAndAccountQualifierAndName
                            .entryData()
                            .amount()
                            .amount()
                            .compareTo(BigDecimal.ZERO)
                        >= 0
                    ? entryDataAndAccountQualifierAndName.entryData().amount().format()
                    : null)
        .setHeader(t(Dictionary.DEBIT))
        .setFlexGrow(0);
    addColumn(
            entryDataAndAccountQualifierAndName ->
                entryDataAndAccountQualifierAndName
                            .entryData()
                            .amount()
                            .amount()
                            .compareTo(BigDecimal.ZERO)
                        < 0
                    ? entryDataAndAccountQualifierAndName.entryData().amount().negate().format()
                    : null)
        .setHeader(t(Dictionary.CREDIT))
        .setFlexGrow(0);
  }

  void setEntries(EventData event) {
    var entries = eventViewEndpoint.findEntriesByEventId(event.eventId());
    setItems(entries);
  }
}
