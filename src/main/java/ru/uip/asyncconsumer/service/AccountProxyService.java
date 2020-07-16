package ru.uip.asyncconsumer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.uip.model.JsonAccount;


@Service
public class AccountProxyService {

    private final String accountServiceURL;

    private WebClient webClient;

    public AccountProxyService(@Value("${account.url}") String accountServiceURL) {
        this.accountServiceURL = accountServiceURL;
        webClient = WebClient.builder().baseUrl(accountServiceURL).build();
    }

    public Mono<JsonAccount> getAccountByNumber(String accountNumber) {
        return webClient
                .get()
                .uri("/account/" + accountNumber)
                .retrieve().bodyToMono(JsonAccount.class);
    }

    public Mono<JsonAccount> updateAccount(JsonAccount account) {
        final Mono<JsonAccount> jsonAccountMono = webClient
                .post()
                .uri("/account")
                .bodyValue(account)
                .retrieve()
                .bodyToMono(JsonAccount.class);
        return jsonAccountMono;
    }
}
