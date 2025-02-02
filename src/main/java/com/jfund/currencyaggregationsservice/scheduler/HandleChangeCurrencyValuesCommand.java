package com.jfund.currencyaggregationsservice.scheduler;

import com.jfund.currencyaggregationsservice.service.ApplyChangeValuesEventService;
import com.jfund.currencyaggregationsservice.service.ChangeCurrencyValuesEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleChangeCurrencyValuesCommand implements Runnable {
    private final ApplyChangeValuesEventService applyChangeValuesEventService;
    private final ChangeCurrencyValuesEventService changeCurrencyValuesEventService;

    @Override
    public void run() {
        changeCurrencyValuesEventService
                .getActualEvents()
                .flatMap(applyChangeValuesEventService::apply)
                .doOnError(e -> log.error("Error while handling change currency values", e))
                .subscribe();
    }
}
