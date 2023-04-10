package se.montesmites.ekonomi.jpa.model;

import java.io.Serializable;

public class VerId implements Serializable {

  private String bokfaarId;
  private String verserie;
  private Integer vernr;

  public VerId() {}

  public VerId(String bokfaarId, String verserie, Integer vernr) {
    this.bokfaarId = bokfaarId;
    this.verserie = verserie;
    this.vernr = vernr;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    VerId verId = (VerId) o;

    if (bokfaarId != null ? !bokfaarId.equals(verId.bokfaarId) : verId.bokfaarId != null) {
      return false;
    }
    if (verserie != null ? !verserie.equals(verId.verserie) : verId.verserie != null) {
      return false;
    }
    return vernr != null ? vernr.equals(verId.vernr) : verId.vernr == null;
  }

  @Override
  public int hashCode() {
    int result = bokfaarId != null ? bokfaarId.hashCode() : 0;
    result = 31 * result + (verserie != null ? verserie.hashCode() : 0);
    result = 31 * result + (vernr != null ? vernr.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "VerId{"
        + "bokfaarId='"
        + bokfaarId
        + '\''
        + ", verserie='"
        + verserie
        + '\''
        + ", vernr="
        + vernr
        + '}';
  }
}
