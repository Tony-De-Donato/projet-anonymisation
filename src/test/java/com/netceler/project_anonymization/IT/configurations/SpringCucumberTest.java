package com.netceler.project_anonymization.IT.configurations;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringCucumberTest {

    //TODO add testcontainer for postgresql
    @LocalServerPort
    protected int port;

}
