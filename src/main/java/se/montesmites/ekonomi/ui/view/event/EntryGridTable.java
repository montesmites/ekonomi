package se.montesmites.ekonomi.ui.view.event;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public class EntryGridTable {

  private final int minimumSize;
  private final ListDataProvider<EntryGridRow> listDataProvider;
  private final ArrayList<EntryGridRow> rows;

  public EntryGridTable(int minimumSize) {
    this.minimumSize = minimumSize;
    this.rows = new ArrayList<>(minimumSize);
    this.rows.addAll(
        Stream.iterate(0, that -> that < minimumSize, index -> index + 1)
            .map(__ -> addNewRow())
            .toList());
    this.listDataProvider = new ListDataProvider<>(this.rows);
  }

  public ListDataProvider<EntryGridRow> listDataProvider() {
    return this.listDataProvider;
  }

  public EntryGridRow addNewRow() {
    return add(EntryGridRow.empty());
  }

  public void setRows(Collection<EntryGridRow> rows) {
    this.rows.clear();
    this.rows.addAll(rows);
    for (var i = rows.size(); i < this.minimumSize; i++) {
      this.add(EntryGridRow.empty());
    }
    this.refresh();
  }

  public EntryGridRow add(EntryGridRow row) {
    var index = rows.size();
    row.index(index);
    rows.addLast(row);
    this.refresh();
    return row;
  }

  private void refresh() {
    if (this.listDataProvider != null) {
      listDataProvider.refreshAll();
    }
  }
}
