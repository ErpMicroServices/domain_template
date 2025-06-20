package org.erp_microservices.domain_template;

import org.junit.jupiter.api.Tag;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@Tag("bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:build/reports/cucumber/cucumber.html, json:build/reports/cucumber/cucumber.json")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.erp_microservices.domain_template.steps")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources/features")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@bdd")
public class CucumberTestRunner {
}