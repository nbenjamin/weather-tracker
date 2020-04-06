package com.nbenja.weathertracker.repository;


import com.nbenja.weathertracker.domain.Measurement;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class MeasurementStoreInMemoryImplTest {

    private MeasurementStoreInMemoryImpl subject;

    @Before
    public void before() {
        subject = new MeasurementStoreInMemoryImpl();
    }

    @Test
    public void add_withValidMeasurement_storeSuccessfully() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-09-01T16:20:00.000Z");
        Measurement expected = new Measurement(zonedDateTime, new Double(27.1), new Double(16.7), new Double(0));
        subject.add(expected);
        Measurement actual = subject.fetch(zonedDateTime);
        assertThat(actual.getDewPoint(), is(equalTo(expected.getDewPoint())));
    }

    @Test
    public void add_measurementWithOutTimestamp_dontStoreMeasurement() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-09-01T16:20:00.000Z");
        Measurement expected = new Measurement(null, new Double(27.1), new Double(16.7), new Double(0));
        subject.add(expected);
        Measurement actual = subject.fetch(zonedDateTime);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void fetch_withValidTimestamp_returnValidMeasurement() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-09-01T16:20:00.000Z");
        Measurement expected = new Measurement(zonedDateTime, new Double(27.1), new Double(16.7), new Double(0));
        subject.add(expected);
        Measurement actual = subject.fetch(zonedDateTime);
        assertThat(actual.getDewPoint(), is(equalTo(expected.getDewPoint())));
    }

    @Test
    public void fetch_withNullTimestamp_returnEmptyMeasurement() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-09-01T16:20:00.000Z");
        ZonedDateTime zonedDateTimeWithNextDay = ZonedDateTime.parse("2015-10-01T16:20:00.000Z");
        Measurement expected = new Measurement(zonedDateTime, new Double(27.1), new Double(16.7), new Double(0));
        subject.add(expected);
        Measurement actual = subject.fetch(zonedDateTimeWithNextDay);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void queryDateRange_withValidTimestamp_returnListOfMeasurements() {
        Measurement m1 = new Measurement(ZonedDateTime.parse("2015-09-01T16:20:00.000Z"), new Double(27.1), new Double(16.7), new Double(0));
        subject.add(m1);
        Measurement m2 = new Measurement(ZonedDateTime.parse("2015-09-01T17:20:00.000Z"), new Double(27.1), new Double(16.7), new Double(0));
        subject.add(m2);
        Measurement m3 = new Measurement(ZonedDateTime.parse("2015-09-01T15:20:00.000Z"), new Double(27.1), new Double(16.7), new Double(0));
        subject.add(m3);
        Measurement m4 = new Measurement(ZonedDateTime.parse("2015-09-01T16:21:00.000Z"), new Double(27.1), new Double(16.7), new Double(0));
        subject.add(m4);
        Measurement m5 = new Measurement(ZonedDateTime.parse("2015-09-01T16:20:01.000Z"), new Double(27.1), new Double(16.7), new Double(0));
        subject.add(m5);
        List<Measurement> actual = subject.queryDateRange(ZonedDateTime.parse("2015-09-01T16:20:00.000Z"), ZonedDateTime.parse("2015-09-01T17:20:00.000Z"));
        assertThat(actual.size(), is(equalTo(3)));

        List<Measurement> actual1 = subject.queryDateRange(ZonedDateTime.parse("2015-09-01T17:20:00.000Z"), ZonedDateTime.parse("2015-09-01T17:20:00.000Z"));
        assertThat(actual1.size(), is(equalTo(0)));
    }
}