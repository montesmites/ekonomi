package se.montesmites.ekonomi.ui.view.event;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import java.math.BigDecimal;
import java.util.Optional;
import se.montesmites.ekonomi.db.EventData;
import se.montesmites.ekonomi.db.model.Amount;
import se.montesmites.ekonomi.db.model.Amount.Sign;
import se.montesmites.ekonomi.endpoint.event.EventViewEndpoint;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;

public class EntryGrid extends VerticalLayout implements Translator {

  // https://martinelli.ch/vaadin-editable-grid/
  // https://stackoverflow.com/questions/68564189/vaadin-flow-grid-add-an-empty-row

  private static final int DEFAULT_MINIMUM_GRID_SIZE = 10;

  private final EventViewEndpoint eventViewEndpoint;
  private final Grid<EntryGridRow> grid;
  private final EntryGridTable entryGridTable = new EntryGridTable(DEFAULT_MINIMUM_GRID_SIZE);

  private Editor<EntryGridRow> editor;
  private Optional<Column<EntryGridRow>> focusedColumn;
  private Optional<EntryGridRow> focusedData;

  public EntryGrid(EventViewEndpoint eventViewEndpoint) {
    this.eventViewEndpoint = eventViewEndpoint;

    this.grid = new Grid<>(EntryGridRow.class, false);
    this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
    this.grid.setAllRowsVisible(true);
    this.grid.setWidthFull();
    this.grid.setDataProvider(this.entryGridTable.listDataProvider());

    var addRowButton = new Button();
    addRowButton.addClickListener(__ -> grid.getEditor().editItem(entryGridTable.addNewRow()));

    this.setupGrid();

    this.setWidthFull();

    add(grid, addRowButton);
  }

  void setEntries(EventData event) {
    var entries = eventViewEndpoint.findEntriesByEventId(event.eventId());
    entryGridTable.setRows(
        entries.stream()
            .sorted(nullsLast(comparing(data -> data.entryData().rowNo())))
            .map(EntryGridRow::from)
            .toList());
  }

  private void setupGrid() {
    var editorAccountQualifier = new TextField();
    var editorAccountName = new TextField();
    var editorDebit = new TextField();
    var editorCredit = new TextField();

    this.grid
        .addColumn(EntryGridRow::qualifier)
        .setHeader(t(Dictionary.ACCOUNT_QUALIFIER))
        .setFlexGrow(0)
        .setEditorComponent(editorAccountQualifier);
    this.grid
        .addColumn(EntryGridRow::name)
        .setHeader(t(Dictionary.ACCOUNT_NAME))
        .setFlexGrow(1)
        .setEditorComponent(editorAccountName);
    this.grid
        .addColumn(
            row ->
                row != null
                        && row.amount() != null
                        && row.amount().amount().compareTo(BigDecimal.ZERO) >= 0
                    ? row.amount().format()
                    : null)
        .setHeader(t(Dictionary.DEBIT))
        .setFlexGrow(0)
        .setEditorComponent(editorDebit);
    this.grid
        .addColumn(
            row ->
                row != null
                        && row.amount() != null
                        && row.amount().amount().compareTo(BigDecimal.ZERO) < 0
                    ? row.amount().negate().format()
                    : null)
        .setHeader(t(Dictionary.CREDIT))
        .setFlexGrow(0)
        .setEditorComponent(editorCredit);

    var binder = new BeanValidationBinder<>(EntryGridRow.class);
    this.editor = this.grid.getEditor();
    editor.setBinder(binder);
    editor.setBuffered(true);
    editor.addSaveListener(__ -> {});

    binder.forField(editorAccountQualifier).bind(EntryGridRow::qualifier, EntryGridRow::qualifier);
    binder.forField(editorAccountName).bind(EntryGridRow::name, EntryGridRow::name);
    binder
        .forField(editorDebit)
        .bind(
            row -> row.amount().sign() == Sign.NEGATIVE ? null : row.amount().format(),
            (row, debit) -> row.amount(new Amount(new BigDecimal(debit))));
    binder
        .forField(editorCredit)
        .bind(
            row -> row.amount().sign() == Sign.NEGATIVE ? row.amount().negate().format() : null,
            (row, credit) -> row.amount(new Amount(new BigDecimal(credit)).negate()));

    this.grid.addCellFocusListener(
        entryGridDataCellFocusEvent -> {
          this.focusedData = entryGridDataCellFocusEvent.getItem();
          this.focusedColumn = entryGridDataCellFocusEvent.getColumn();
        });

    this.grid.addSelectionListener(
        selectionEvent ->
            selectionEvent
                .getFirstSelectedItem()
                .ifPresent(
                    row -> {
                      editor.save();
                      if (!editor.isOpen()) {
                        this.grid.getEditor().editItem(row);
                        focusedColumn.ifPresent(
                            column -> {
                              if (column.getEditorComponent() instanceof Focusable<?> component) {
                                component.focus();
                              }
                            });
                      }
                    }));

    Shortcuts.addShortcutListener(grid, __ -> focusedData.ifPresent(grid::select), Key.END)
        .listenOn(grid);
    Shortcuts.addShortcutListener(
            grid,
            () -> {
              if (editor.isOpen()) {
                editor.cancel();
              }
            },
            Key.ESCAPE)
        .listenOn(grid);
  }
}
