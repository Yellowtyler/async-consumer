package ru.uip.asyncconsumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.uip.model.JsonAccount;
import ru.uip.model.MoneyTransfer;
import ru.uip.model.MoneyTransferResult;


@Service
public class MoneyTransferService {

    @Autowired
    private AccountProxyService accountService;

    public Mono<Tuple2<JsonAccount, JsonAccount>> getAccounts(MoneyTransfer transfer) {
        return accountService.getAccountByNumber(transfer.getFromAccountNumber())
                .zipWith(accountService.getAccountByNumber(transfer.getToAccountNumber()));
    }

    //default visibility for testing
    Tuple2<JsonAccount, JsonAccount> doChangeBalance(Tuple2<JsonAccount, JsonAccount> accounts, double amount) {

        JsonAccount fromAccount = accounts.getT1();
        JsonAccount toAccount = accounts.getT2();
        double fromBalance = fromAccount.getAccountBalance();
        double toBalance = toAccount.getAccountBalance();
        fromBalance -= amount;
        toBalance += amount;

        JsonAccount updatedFrom = new JsonAccount(
                fromAccount.getAccountNumber(),
                fromAccount.getAccountName(),
                fromBalance,
                fromAccount.getAccountStatus());
        JsonAccount updatedTo = new JsonAccount(
                toAccount.getAccountNumber(),
                toAccount.getAccountName(),
                toBalance,
                toAccount.getAccountStatus());
        return Tuples.of(updatedFrom, updatedTo);
    }

    public Mono<Tuple2<JsonAccount, JsonAccount>> changeBalance(MoneyTransfer transfer) {
        return getAccounts(transfer)
                .map(accounts -> doChangeBalance(accounts, transfer.getAmount()));
    }

    public Mono<Tuple2<JsonAccount, JsonAccount>> updateAccounts(Tuple2<JsonAccount, JsonAccount> balance) {
        return accountService.updateAccount(balance.getT1())
                .zipWith(accountService.updateAccount(balance.getT2()));
    }

    public Mono<MoneyTransferResult> toMoneyTransferResult(Mono<Tuple2<JsonAccount, JsonAccount>> tuple) {
        return tuple.transform(t -> t.map(t1 -> new MoneyTransferResult(t1.getT1(), t1.getT2())));
    }


    public Mono<MoneyTransferResult> transfer(MoneyTransfer transfer) {
        return changeBalance(transfer)
                .map(this::updateAccounts)
                .flatMap(this::toMoneyTransferResult);
    }

}
