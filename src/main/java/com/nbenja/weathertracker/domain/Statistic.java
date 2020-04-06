package com.nbenja.weathertracker.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Statistic {
  @JsonProperty("min") MIN,
  @JsonProperty("max") MAX,
  @JsonProperty("average") AVERAGE,
}
