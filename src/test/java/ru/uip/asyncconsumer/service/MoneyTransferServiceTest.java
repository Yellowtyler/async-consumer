package ru.uip.asyncconsumer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.uip.model.EnumAccountStatus;
import ru.uip.model.JsonAccount;
import ru.uip.model.MoneyTransfer;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.uip.model.EnumAccountStatus.ACTIVE;
import static ru.uip.model.EnumAccountStatus.INACTIVE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"account.url=http://localhost:9090"})
@AutoConfigureStubRunner(ids = {"ru.uip:async-producer:+:stubs:9090"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class MoneyTransferServiceTest {

    @Autowired
    private MoneyTransferService service;


    @Test
    public void testGetAccounts() {
        MoneyTransfer transfer = new MoneyTransfer(
                "1",
                "2",
                100);

        final Tuple2<JsonAccount, JsonAccount> accounts = service.getAccounts(transfer).block();
        JsonAccount from = accounts.getT1();
        JsonAccount to = accounts.getT2();

        assertThat(accounts, notNullValue());
        assertThat(accounts.getT1(), notNullValue());
        assertThat(accounts.getT2(), notNullValue());

        assertThat(from.getAccountNumber(), equalTo("1"));
        assertThat(from.getAccountName(), equalTo("MikeAccount"));
        assertThat(from.getAccountStatus(), equalTo(ACTIVE));
        assertThat(from.getAccountBalance(), equalTo(1000d));

        assertThat(to.getAccountNumber(), equalTo("2"));
        assertThat(to.getAccountName(), equalTo("AlexAccount"));
        assertThat(to.getAccountStatus(), equalTo(INACTIVE));
        assertThat(to.getAccountBalance(), equalTo(200d));
    }

    @Test
    public void testDoChangeBalance() {
        double mikeStartBalance = 1000;
        double alexStartBalance = 200;
        double amount = 500;

        JsonAccount mikeAccount = new JsonAccount(
                "1",
                "MikeAccount",
                mikeStartBalance,
                ACTIVE);

        JsonAccount alexAccount = new JsonAccount(
                "2",
                "AlexAccount",
                alexStartBalance,
                INACTIVE);

        Tuple2<JsonAccount, JsonAccount> accounts = Tuples.of(mikeAccount, alexAccount);
        final Tuple2<JsonAccount, JsonAccount> changed = service.doChangeBalance(accounts, amount);

        assertThat(changed.getT1().getAccountBalance(), equalTo(mikeStartBalance - amount));
        assertThat(changed.getT2().getAccountBalance(), equalTo(alexStartBalance + amount));
    }
}
