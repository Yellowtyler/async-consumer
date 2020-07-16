package ru.uip.asyncconsumer;

import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureStubRunner(
        ids = {"ru.uip:async-producer:+:stubs:9090"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@SpringBootTest(properties = {"account.url=http://localhost:9090"})
public abstract class BaseTest {

    @Autowired
    private ApplicationContext applicationContext;


    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentation) {
        final WebTestClient client = WebTestClient.bindToApplicationContext(applicationContext)
                .configureClient()
                .filter(documentationConfiguration(restDocumentation))
                .build();
        RestAssuredWebTestClient.webTestClient(client);
    }
}
