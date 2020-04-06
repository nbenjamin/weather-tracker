package com.nbenja.weathertracker.controller;

import com.nbenja.weathertracker.domain.Measurement;
import com.nbenja.weathertracker.domain.Statistic;
import com.nbenja.weathertracker.service.MeasurementAggregator;
import com.nbenja.weathertracker.service.MeasurementServiceImpl;
import com.nbenja.weathertracker.domain.AggregateResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatsControllerTest {

    @Mock
    private MeasurementServiceImpl queryService;
    @Mock
    private MeasurementAggregator aggregator;

    @InjectMocks
    private StatsController subject;

    @Test
    public void getStats_withValidValues_returnListOFAggregateResults() {
        ZonedDateTime from = ZonedDateTime.parse("2015-09-01T16:20:00.000Z");
        ZonedDateTime to = ZonedDateTime.parse("2015-10-01T16:20:00.000Z");
        List<String> metrics = asList("temperature");
        List<Statistic> statistics = asList(Statistic.MAX);
        List<Measurement> expected = asList(new Measurement(from, new Double(27.1), new Double(16.7), new Double(0)));

        List<AggregateResult> aggregateResults = asList(new AggregateResult("temperature", Statistic.MAX, 10.1));
        when(queryService.queryDateRange(from, to)).thenReturn(expected);
        when(aggregator.analyze(expected, metrics, statistics)).thenReturn(aggregateResults);

        List<AggregateResult> actual = subject.getStats(metrics, statistics, from, to);

        assertThat(actual.size(), is(equalTo(1)));

        verify(queryService).queryDateRange(from, to);
        verify(aggregator).analyze(expected, metrics, statistics);
    }
}
