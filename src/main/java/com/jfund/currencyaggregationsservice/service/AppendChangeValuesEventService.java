package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AppendChangeValuesEventService {
    public Mono<Void> append(ChangeCurrencyValuesEvent event) {
        return Mono.empty();
    }
}
