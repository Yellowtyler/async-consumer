package ru.uip.asyncconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.uip.asyncconsumer.service.MoneyTransferService;
import ru.uip.model.MoneyTransfer;
import ru.uip.model.MoneyTransferResult;


import javax.validation.Valid;

@RestController
public class MoneyTransferController {

    @Autowired
    private MoneyTransferService transferService;

    @PostMapping("/transfer")
    @ResponseBody
    public Mono<MoneyTransferResult> transfer(@RequestBody @Valid MoneyTransfer moneyTransfer) {
        return transferService.transfer(moneyTransfer);
    }
}
