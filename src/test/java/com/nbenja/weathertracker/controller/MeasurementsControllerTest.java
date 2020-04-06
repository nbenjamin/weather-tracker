package com.nbenja.weathertracker.controller;

import com.nbenja.weathertracker.domain.Measurement;
import com.nbenja.weathertracker.service.MeasurementServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class MeasurementsControllerTest {

    @Mock
    private MeasurementServiceImpl measurementService;
    @InjectMocks
    private MeasurementsController subject;
    private DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendInstant(3)
            .toFormatter();


    @Before
    public void before() {
        setField(subject, "dateTimeFormatter", dateTimeFormatter);
    }

    @Test
    public void createMeasurement_withValidMeasurement_returnSuccess() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-09-01T16:20:00.000Z");
        Measurement expected = new Measurement(zonedDateTime, new Double(27.1), new Double(16.7), new Double(0));
        doNothing().when(measurementService).add(expected);
        ResponseEntity<?> actual = subject.createMeasurement(expected);
        assertThat(actual.getHeaders().getLocation().toString(), is(equalTo("/measurements/2015-09-01T16:20:00.000Z")));
        verify(measurementService).add(expected);
    }

    @Test
    public void getMeasurement_withValidZoneDateTime_returnMeasurement() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-09-01T16:20:00.000Z");
        Measurement expected = new Measurement(zonedDateTime, new Double(27.1), new Double(16.7), new Double(0));

        when(measurementService.fetch(zonedDateTime)).thenReturn(expected);
        ResponseEntity<Measurement> actual = subject.getMeasurement(zonedDateTime);
        assertThat(actual.getBody().getDewPoint(), is(equalTo(expected.getDewPoint())));
        verify(measurementService).fetch(zonedDateTime);

    }

    @Test
    public void getMeasurement_withInvalidZoneDateTime_returnNotFound() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-09-01T16:20:00.000Z");

        when(measurementService.fetch(zonedDateTime)).thenReturn(null);
        ResponseEntity<Measurement> actual = subject.getMeasurement(zonedDateTime);
        assertThat(actual.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
        verify(measurementService).fetch(zonedDateTime);

    }

}
