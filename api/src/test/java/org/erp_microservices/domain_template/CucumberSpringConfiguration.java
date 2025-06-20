package org.erp_microservices.domain_template;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("bdd")
public class CucumberSpringConfiguration {
}