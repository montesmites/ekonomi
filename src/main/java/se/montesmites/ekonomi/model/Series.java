package se.montesmites.ekonomi.model;

import java.util.Objects;

public class Series {

  private final String series;

  public Series(String series) {
    this.series = series;
  }

  public String getSeries() {
    return series;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(this.series);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Series other = (Series) obj;
    return Objects.equals(this.series, other.series);
  }

  @Override
  public String toString() {
    return "Series{" + "series=" + series + '}';
  }
}
