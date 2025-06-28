package org.erp_microservices.domain_template;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.erp_microservices.domain_template.config.TestSecurityConfig;

@CucumberContextConfiguration
@SpringBootTest(
    classes = DomainTemplateApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Tag("bdd")
public class CucumberSpringConfiguration {
}