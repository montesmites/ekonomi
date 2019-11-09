package se.montesmites.ekonomi.jpa.model;

import java.io.Serializable;

public class KontoId implements Serializable {

  private String bokfaarId;
  private String kontoId;

  public KontoId() {
  }

  public KontoId(String bokfaarId, String kontoId) {
    this.bokfaarId = bokfaarId;
    this.kontoId = kontoId;
  }

  public String getBokfaarId() {
    return bokfaarId;
  }

  public void setBokfaarId(String bokfaarId) {
    this.bokfaarId = bokfaarId;
  }

  public String getKontoId() {
    return kontoId;
  }

  public void setKontoId(String kontoId) {
    this.kontoId = kontoId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    KontoId kontoId1 = (KontoId) o;

    if (bokfaarId != null ? !bokfaarId.equals(kontoId1.bokfaarId) : kontoId1.bokfaarId != null) {
      return false;
    }
    return kontoId != null ? kontoId.equals(kontoId1.kontoId) : kontoId1.kontoId == null;
  }

  @Override
  public int hashCode() {
    int result = bokfaarId != null ? bokfaarId.hashCode() : 0;
    result = 31 * result + (kontoId != null ? kontoId.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "KontoId{" +
        "bokfaarId='" + bokfaarId + '\'' +
        ", kontoId='" + kontoId + '\'' +
        '}';
  }
}
