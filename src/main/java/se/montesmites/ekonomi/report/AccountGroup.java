package se.montesmites.ekonomi.report;

import java.util.function.UnaryOperator;

public interface AccountGroup {

  String description();

  String regex();

  default UnaryOperator<RowWithAmounts> postProcessor() {
    return row -> row;
  }
}
