package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

@FunctionalInterface
interface FooterRow extends RowWithAmounts {

  static FooterRow of(RowAggregator aggregator) {
    var aggregate = aggregator.aggregate();
    return aggregate::getMonthlyAmount;
  }

  @Override
  Currency getMonthlyAmount(Column column);
}
