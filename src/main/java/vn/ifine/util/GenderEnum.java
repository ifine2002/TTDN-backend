package vn.ifine.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GenderEnum {
  @JsonProperty("male")
  MALE,
  @JsonProperty("female")
  FEMALE,
  @JsonProperty("other")
  OTHER;
}
