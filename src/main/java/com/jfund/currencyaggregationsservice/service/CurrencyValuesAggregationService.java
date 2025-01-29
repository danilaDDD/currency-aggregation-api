package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CurrencyValuesAggregationService {
    public Mono<CurrencyValuesAggregation> save(CurrencyValuesAggregation aggregation) {
        return Mono.empty();
    }

    public Flux<CurrencyValuesAggregation> findAllActual() {
        return Flux.empty();
    }
}
