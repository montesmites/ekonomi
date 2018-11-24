package se.montesmites.ekonomi.report;

@FunctionalInterface
public interface TitleRow extends RowWithGranularFormatters {

  String getTitle();

  @Override
  default String formatDescription() {
    return getTitle().toUpperCase();
  }
}
