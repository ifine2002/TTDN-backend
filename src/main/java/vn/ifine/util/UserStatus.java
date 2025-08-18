package vn.ifine.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserStatus {
//  @JsonProperty("active")
  ACTIVE,
//  @JsonProperty("inactive")
  INACTIVE,
//  @JsonProperty("none")
  NONE,
//  @JsonProperty("deleted")
  DELETED;

//  @JsonCreator
//  public static UserStatus fromString(String value) {
//    if (value == null) {
//      return null;
//    }
//
//    String lowerCase = value.toLowerCase();
//    for (UserStatus status : UserStatus.values()) {
//      if (status.name().toLowerCase().equals(lowerCase)) {
//        return status;
//      }
//    }
//
//    throw new IllegalArgumentException("Invalid status value: " + value);
//  }
}
