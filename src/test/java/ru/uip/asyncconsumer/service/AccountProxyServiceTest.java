package ru.uip.asyncconsumer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.uip.model.JsonAccount;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToObject;
import static ru.uip.model.EnumAccountStatus.ACTIVE;
import static ru.uip.model.EnumAccountStatus.INACTIVE;

@ExtendWith({SpringExtension.class})
@SpringBootTest(properties = {"account.url=http://localhost:9090"})
@AutoConfigureStubRunner(ids = {"ru.uip:async-producer:+:stubs:9090"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class AccountProxyServiceTest {

    @Autowired
    private  AccountProxyService service;


    @Test
    public void testGetAccount1ByNumber() {
        JsonAccount expectedAccount = new JsonAccount(
                "1",
                "MikeAccount",
                1000,
                ACTIVE);

        final JsonAccount account = service.getAccountByNumber("1").block();

        assertThat(account, equalToObject(expectedAccount));
    }

    @Test
    public void testGetAccount2ByNumber() {
        JsonAccount expectedAccount = new JsonAccount(
                "2",
                "AlexAccount",
                200,
                INACTIVE);

        final JsonAccount account = service.getAccountByNumber("2").block();

        assertThat(account, equalToObject(expectedAccount));
    }

    @Test
    public void testUpdateAccount() {
        JsonAccount expectedAccount = new JsonAccount(
                "1",
                "MikeAccount",
                900,
                ACTIVE);

        final JsonAccount account = service.updateAccount(expectedAccount).block();

        assertThat(account, equalToObject(expectedAccount));
    }
}
