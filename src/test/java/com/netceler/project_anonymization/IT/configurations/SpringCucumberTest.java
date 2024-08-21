package com.netceler.project_anonymization.IT.configurations;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Testcontainers
public class SpringCucumberTest {

    //    @Container
    //    @ServiceConnection
    //    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.3");

    @LocalServerPort
    protected int port;

}
