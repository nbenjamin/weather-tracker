package com.nbenja.weathertracker.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.text.DecimalFormat;

public class AggregateResult {
  private String metric;
  private Statistic statistic;
  private double value;
  private DecimalFormat df = new DecimalFormat("#.#");

  public AggregateResult() {
  }

  public AggregateResult(String metric, Statistic statistic, double value) {
    this.metric = metric;
    this.statistic = statistic;
    this.value = value;
  }

  @JsonGetter("metric")
  public String getMetric() {
    return this.metric;
  }

  @JsonGetter("stat")
  public Statistic getStatistic() {
    return this.statistic;
  }

  @JsonGetter("value")
  public double getValue() {
    return Double.parseDouble(df.format(this.value));
    //return this.value;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, true);
  }


  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, true);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
