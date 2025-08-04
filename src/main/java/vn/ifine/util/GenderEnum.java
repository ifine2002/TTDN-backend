package vn.ifine.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum GenderEnum {
  @JsonProperty("male")
  MALE,
  @JsonProperty("female")
  FEMALE,
  @JsonProperty("other")
  OTHER;

  @JsonCreator
  public static GenderEnum fromString(String value) {
    if (value == null) {
      return null;
    }

    String lowerCase = value.toLowerCase();
    for (GenderEnum gender : GenderEnum.values()) {
      if (gender.name().toLowerCase().equals(lowerCase)) {
        return gender;
      }
    }

    throw new IllegalArgumentException("Invalid gender value: " + value);
  }
}
