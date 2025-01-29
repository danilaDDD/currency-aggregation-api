package com.jfund.currencyaggregationsservice.runner;

import com.jfund.currencyaggregationsservice.service.AppendChangeValuesEventService;
import com.jfund.currencyaggregationsservice.service.ChangeCurrencyValuesEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleChangeCurrencyValues implements Runnable {
    private final AppendChangeValuesEventService appendChangeValuesEventService;
    private final ChangeCurrencyValuesEventService changeCurrencyValuesEventService;

    @Override
    public void run() {
        changeCurrencyValuesEventService
                .getActualEvents()
                .flatMap(appendChangeValuesEventService::append)
                .doOnError(e -> log.error("Error while handling change currency values", e))
                .subscribe();
    }
}
