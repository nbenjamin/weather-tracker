package com.nbenja.weathertracker;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, features = "src/test/resources", glue = "com.nbenja.weathertracker.steps")
public class AcceptanceTest {
}
