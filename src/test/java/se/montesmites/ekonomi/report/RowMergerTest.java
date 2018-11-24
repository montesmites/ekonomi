package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import org.junit.jupiter.api.Test;

class RowMergerTest {

  @Test
  void onlyTemplateRow() {
    var template = (Row) Column::name;
    var merger = RowMerger.template(template);
    var exp = template;
    var act = merger.merge();
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void mergeTwoRows() {
    var template = (Row) column -> column.name() + "_template";
    var row1 = (Row) column -> column.name() + "_row1";
    var merger = RowMerger.template(template).add(DESCRIPTION, row1);
    var exp = (Row) column -> (column == DESCRIPTION ? row1 : template).format(column);
    var act = merger.merge();
    assertTrue(act.isEquivalentTo(exp));
  }
}
