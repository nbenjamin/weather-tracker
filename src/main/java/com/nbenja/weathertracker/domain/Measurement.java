package com.nbenja.weathertracker.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class Measurement {

    @JsonProperty(value = "timestamp") @NotNull
    private ZonedDateTime timestamp;

    @JsonProperty("temperature")
    private Double temperature;

    @JsonProperty("dewPoint")
    private Double dewPoint;

    @JsonProperty("precipitation")
    private Double precipitation;

    public Measurement(ZonedDateTime timestamp, Double temperature, Double dewPoint, Double precipitation) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.dewPoint = dewPoint;
        this.precipitation = precipitation;
    }

    public Measurement() {
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(Double dewPoint) {
        this.dewPoint = dewPoint;
    }

    public Double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Double precipitation) {
        this.precipitation = precipitation;
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
