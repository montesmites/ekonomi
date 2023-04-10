package se.montesmites.ekonomi.jpa.model;

import java.io.Serializable;

public class VerradId implements Serializable {

  private String bokfaarId;
  private String verserie;
  private Integer vernr;
  private Integer rad;

  public VerradId() {}

  public VerradId(String bokfaarId, String verserie, Integer vernr, Integer rad) {
    this.bokfaarId = bokfaarId;
    this.verserie = verserie;
    this.vernr = vernr;
    this.rad = rad;
  }

  public String getBokfaarId() {
    return bokfaarId;
  }

  public void setBokfaarId(String bokfaarId) {
    this.bokfaarId = bokfaarId;
  }

  public String getVerserie() {
    return verserie;
  }

  public void setVerserie(String verserie) {
    this.verserie = verserie;
  }

  public Integer getVernr() {
    return vernr;
  }

  public void setVernr(Integer vernr) {
    this.vernr = vernr;
  }

  public Integer getRad() {
    return rad;
  }

  public void setRad(Integer rad) {
    this.rad = rad;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    VerradId verradId = (VerradId) o;

    if (bokfaarId != null ? !bokfaarId.equals(verradId.bokfaarId) : verradId.bokfaarId != null) {
      return false;
    }
    if (verserie != null ? !verserie.equals(verradId.verserie) : verradId.verserie != null) {
      return false;
    }
    if (vernr != null ? !vernr.equals(verradId.vernr) : verradId.vernr != null) {
      return false;
    }
    return rad != null ? rad.equals(verradId.rad) : verradId.rad == null;
  }

  @Override
  public int hashCode() {
    int result = bokfaarId != null ? bokfaarId.hashCode() : 0;
    result = 31 * result + (verserie != null ? verserie.hashCode() : 0);
    result = 31 * result + (vernr != null ? vernr.hashCode() : 0);
    result = 31 * result + (rad != null ? rad.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "VerradId{"
        + "bokfaarId='"
        + bokfaarId
        + '\''
        + ", verserie='"
        + verserie
        + '\''
        + ", vernr="
        + vernr
        + ", rad="
        + rad
        + '}';
  }
}
