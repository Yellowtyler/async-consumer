package ru.uip.asyncconsumer.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.uip.model.JsonAccount;
import ru.uip.model.MoneyTransfer;
import ru.uip.model.MoneyTransferResult;


import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;
import static ru.uip.model.EnumAccountStatus.ACTIVE;
import static ru.uip.model.EnumAccountStatus.INACTIVE;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureStubRunner(
        ids = {"ru.uip:async-producer:+:stubs:9090"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"account.url=http://localhost:9090"}
)
public class MoneyTransferControllerTest {

    private WebTestClient webTestClient;


    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
                .configureClient()
                .filter(documentationConfiguration(restDocumentation))
                .build();
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testTransfer() {
        JsonAccount mikeAccount = new JsonAccount(
                "1",
                "MikeAccount",
                900,
                ACTIVE);

        JsonAccount alexAccount = new JsonAccount(
                "2",
                "AlexAccount",
                300,
                INACTIVE);

        final MoneyTransferResult expectedBody = new MoneyTransferResult(mikeAccount, alexAccount);

        MoneyTransfer transfer = new MoneyTransfer(
                "1",
                "2",
                100);

        webTestClient
                .post()
                .uri("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transfer)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MoneyTransferResult.class)
                .isEqualTo(expectedBody)
                .consumeWith(document("index"));

    }
}

