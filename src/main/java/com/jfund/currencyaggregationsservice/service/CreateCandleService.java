package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.entity.Candle;
import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateCandleService {
    public Mono<Candle> createCandle(CurrencyValuesAggregation aggregation) {
        return Mono.empty();
    }
}
