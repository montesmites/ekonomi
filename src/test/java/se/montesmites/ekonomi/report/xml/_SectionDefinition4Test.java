package se.montesmites.ekonomi.report.xml;

import java.util.List;
import se.montesmites.ekonomi.report.SectionDecorator;

class _SectionDefinition4Test {

  private final String description;
  private final List<Class<? extends SectionDecorator>> decorators;
  private final List<_RowDefinition4Test> rows;

  _SectionDefinition4Test(
      String description,
      List<Class<? extends SectionDecorator>> decorators,
      List<_RowDefinition4Test> rows) {
    this.description = description;
    this.decorators = decorators;
    this.rows = rows;
  }

  public String getDescription() {
    return description;
  }

  public List<Class<? extends SectionDecorator>> getDecorators() {
    return decorators;
  }

  List<_RowDefinition4Test> getRows() {
    return rows;
  }
}
