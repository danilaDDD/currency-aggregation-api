package com.jfund.currencyaggregationsservice.scheduler;

import com.jfund.currencyaggregationsservice.service.CheckAggregationFillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckAggregationFillingCommand implements Runnable {
    private final CheckAggregationFillingService service;

    @Override
    public void run() {
        service
                .check()
                .doOnError(e -> log.error("error with check aggregation", e))
                .subscribe();
    }
}
