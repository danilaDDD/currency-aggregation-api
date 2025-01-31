package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChangeCurrencyValuesEventConsumer {
    private final ParseChangeCurrencyValuesEventService parseEventService;
    private final ChangeCurrencyValuesEventService eventService;

    public Mono<ChangeCurrencyValuesEvent> consume(String currencyValuesString) {
        return parseEventService
                .fromString(currencyValuesString)
                .flatMap(eventService::save);
    }
}
