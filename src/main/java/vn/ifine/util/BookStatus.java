package vn.ifine.util;

public enum BookStatus {
//  @JsonProperty("active")
  ACTIVE,
//  @JsonProperty("inactive")
  INACTIVE,
//  @JsonProperty("none")
  NONE,
//  @JsonProperty("deleted")
  DELETED;

//  @JsonCreator
//  public static BookStatus fromString(String value) {
//    if (value == null) {
//      return null;
//    }
//
//    String lowerCase = value.toLowerCase();
//    for (BookStatus status : BookStatus.values()) {
//      if (status.name().toLowerCase().equals(lowerCase)) {
//        return status;
//      }
//    }
//
//    throw new IllegalArgumentException("Invalid status value: " + value);
//  }
}
