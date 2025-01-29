package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChangeCurrencyValuesEventService {
    public Mono<ChangeCurrencyValuesEvent> consume(String currencyValuesString) {
        return Mono.empty();
    }

    public Flux<ChangeCurrencyValuesEvent> getActualEvents() {
        return Flux.error(new RuntimeException("Not implemented"));
    }
}
