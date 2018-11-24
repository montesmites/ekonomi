package se.montesmites.ekonomi.model;

import java.util.Objects;

public class EventId {

  private final YearId yearId;
  private final int id;
  private final Series series;

  public EventId(YearId yearId, int id, Series series) {
    this.yearId = yearId;
    this.id = id;
    this.series = series;
  }

  public YearId getYearId() {
    return yearId;
  }

  public int getId() {
    return id;
  }

  public Series getSeries() {
    return series;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + Objects.hashCode(this.yearId);
    hash = 97 * hash + this.id;
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
    final EventId other = (EventId) obj;
    if (this.id != other.id) {
      return false;
    }
    if (!Objects.equals(this.yearId, other.yearId)) {
      return false;
    }
    return Objects.equals(this.series, other.series);
  }

  @Override
  public String toString() {
    return "EventId{" + "yearId=" + yearId + ", id=" + id + ", series=" + series + '}';
  }
}
