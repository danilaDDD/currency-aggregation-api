package com.jfund.currencyaggregationsservice.runner;

import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
import com.jfund.currencyaggregationsservice.service.CreateCandleService;
import com.jfund.currencyaggregationsservice.service.CurrencyValuesAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCandle implements Runnable {
    private final CreateCandleService createCandleService;
    private final CurrencyValuesAggregationService aggregationService;

    @Override
    public void run() {
        aggregationService
                .findAllActual()
                .flatMap(aggregation -> createCandleService
                            .createCandle(aggregation)
                            .then(disableAggregation(aggregation)))
                .doOnError(e -> log.error("Error while creating candle", e))
                .subscribe();
    }

    private Mono<CurrencyValuesAggregation> disableAggregation(CurrencyValuesAggregation aggregation) {
        aggregation.setClosed(true);
        return aggregationService.save(aggregation);
    }
}
