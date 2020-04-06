package com.nbenja.weathertracker.steps;

import com.nbenja.weathertracker.WeatherTrackerApplication;
import com.nbenja.weathertracker.domain.Measurement;
import com.nbenja.weathertracker.domain.AggregateResult;
import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java8.En;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WeatherTrackerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration // needed for Cucumber
@Ignore
@DirtiesContext
public class WeatherTrackerSteps implements En {

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    private int port;

    private String BASE_URL = "http://localhost:";
    private String MEASUREMENTS_RESOURCE = "/measurements";
    private String url;
    private String statsURL;
    private String STATS_RESOURCE = "/stats";
    private ResponseEntity<List<AggregateResult>> actualAggregateResult;
    private List<AggregateResult> actualAR = new ArrayList<>();
    private ResponseEntity<Object> actual;
    private ResponseEntity<Measurement> actualMeasurementResponseEntity;
    private int httpstatusCode;
    private String locationHeader;

    @Before
    public void before() {
        url = BASE_URL + port + MEASUREMENTS_RESOURCE;
        statsURL = BASE_URL + port + STATS_RESOURCE;

    }

    public WeatherTrackerSteps() {

        When("^I submit a new measurement as follows:$", (DataTable dataTable) -> {

            List<Map<String, String>> measurement = dataTable.asMaps(String.class, String.class);
            measurement.stream().forEach(kv -> {
                String timestamp = kv.getOrDefault("timestamp", "\"\"").trim();
                String temperature = kv.get("temperature").trim();
                String dewPoint = kv.get("dewPoint").trim();
                String precipitation = kv.get("precipitation").trim();
                String json = "{ \"timestamp\" : " + timestamp + ", \"temperature\" : \"" + temperature + "\", \"dewPoint\" : \"" + dewPoint + "\", \"precipitation\" : \"" + precipitation + "\"}";
                createMeasurement(json);
            });

        });

        Then("^the response has a status code of (\\d+)$", (Integer statusCode) -> {
            assertThat(httpstatusCode, is(equalTo(statusCode)));
        });

        Then("^the Location header has the path \"([^\"]*)\"$", (String expected) -> {
            assertThat(locationHeader, is(equalTo(expected)));
        });

        Given("^I have submitted new measurements as follows:$", (DataTable dataTable) -> {
            List<Map<String, String>> measurement = dataTable.asMaps(String.class, String.class);
            measurement.stream().forEach(kv -> {
                String timestamp = kv.getOrDefault("timestamp", "\"\"").trim();
                String temperature = kv.getOrDefault("temperature", "0").trim();
                String dewPoint = kv.getOrDefault("dewPoint", "0").trim();
                String precipitation = kv.getOrDefault("precipitation", "0").trim();
                String json = "{ \"timestamp\" : " + timestamp + ", \"temperature\" : \"" + temperature + "\", \"dewPoint\" : \"" + dewPoint + "\", \"precipitation\" : \"" + precipitation + "\"}";
                createMeasurement(json);
            });
        });

        When("^I get a measurement for \"([^\"]*)\"$", (String timestamp) -> {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
            actualMeasurementResponseEntity = restTemplate.exchange(url + "/" + timestamp, GET, httpEntity, Measurement.class);
            httpstatusCode = actualMeasurementResponseEntity.getStatusCodeValue();

        });

        Then("^the response body is:$", (DataTable dataTable) -> {

            List<Map<String, String>> measurement = dataTable.asMaps(String.class, String.class);
            List<Measurement> expected = measurement.stream().map(kv -> {
                String timestamp = kv.getOrDefault("timestamp", "\"\"").trim();
                if(isNotBlank(timestamp)) {
                    timestamp = timestamp.substring(1, timestamp.length()-1);
                }
                String temperature = kv.getOrDefault("temperature", "0").trim();
                String dewPoint = kv.getOrDefault("dewPoint", "0").trim();
                String precipitation = kv.getOrDefault("precipitation", "0").trim();
                return new Measurement(ZonedDateTime.parse(timestamp), Double.parseDouble(temperature), Double.parseDouble(dewPoint), Double.parseDouble(precipitation));
            }).collect(Collectors.toList());

            Measurement actual = actualMeasurementResponseEntity.getBody();

            boolean booleanActual = expected.stream().anyMatch(e -> e.getTemperature().equals(actual.getTemperature()));
            assertThat(booleanActual, is(equalTo(true)));
        });


        When("^I get stats with parameters:$", (DataTable dataTable) -> {
            List<Map<String, String>> measurement = dataTable.asMaps(String.class, String.class);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            measurement.stream().forEach(k -> map.add(k.get("param"), k.get("value")));
            String url = createUri(map);

            actualAggregateResult = restTemplate.exchange(url, GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<AggregateResult>>() {});
            actualAR = actualAggregateResult.getBody();
            httpstatusCode = actualAggregateResult.getStatusCodeValue();

        });

        When("^I get invalid stats with parameters:$", (DataTable dataTable) -> {
            List<Map<String, String>> measurement = dataTable.asMaps(String.class, String.class);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            measurement.stream().forEach(k -> map.add(k.get("param"), k.get("value")));
            String url = createUri(map);
            ResponseEntity<Object> actual = restTemplate.exchange(url, GET, HttpEntity.EMPTY, Object.class);
            httpstatusCode = actual.getStatusCodeValue();
        });

        Then("^the response body is an array of:$", (DataTable dataTable) -> {
            List<Map<String, String>> response = dataTable.asMaps(String.class, String.class);
            List<AggregateResult> actualAR = actualAggregateResult.getBody();
            response.stream().forEach(kv -> {
                String metric = kv.get("metric").substring(1, kv.get("metric").length() - 1);
                String stat = kv.get("stat").substring(1, kv.get("stat").length() - 1);
                String value = kv.get("value");

                boolean actualMatch = actualAR.stream().anyMatch(ar -> ar.getMetric().equalsIgnoreCase(metric)
                        && ar.getStatistic().name().equalsIgnoreCase(stat)
                        && ar.getValue() == Double.parseDouble(value));
                MatcherAssert.assertThat(actualMatch, is(true));
            });
            actualAR.clear();

        });

        Then("^the response body is an empty array$", () -> {
            Assertions.assertThat(actualAR).isEmpty();
        });
    }

    private void createMeasurement(String json) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<>(json, httpHeaders);
        actual = restTemplate.exchange(url, POST, httpEntity, Object.class);
        httpstatusCode = actual.getStatusCodeValue();
        HttpHeaders headers = actual.getHeaders();
        locationHeader = headers.getLocation() != null ? headers.getLocation().toString() : "";
    }

    private String createUri(MultiValueMap<String, String> params) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(BASE_URL + port + STATS_RESOURCE)
                .queryParam("metric", StringUtils.join(params.get("metric"), ","))
                .queryParam("stat", StringUtils.join(params.get("stat"), ","))
                .queryParam("fromDateTime", params.get("fromDateTime").get(0))
                .queryParam("toDateTime", params.get("toDateTime").get(0))
                .build();

        return uriComponents.toUriString();
    }
}
