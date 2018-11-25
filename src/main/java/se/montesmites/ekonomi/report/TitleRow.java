package se.montesmites.ekonomi.report;

@FunctionalInterface
public interface TitleRow extends RowWithGranularFormatters {

  static TitleRow of(String description) {
    return () -> description;
  }

  String getTitle();

  @Override
  default String formatDescription() {
    return getTitle().toUpperCase();
  }
}
