package com.jfund.currencyaggregationsservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class Scheduler {
    private final HandleChangeCurrencyValuesCommand handleChangeCurrencyValuesCommand;
    private final CheckAggregationFillingCommand checkAggregationFillingCommand;

    @Value("${app.scheduler.enabled}")
    private boolean enabled;

    @Scheduled(cron = "${app.scheduler.handle-change-currency-values}")
    public void handedChangeValues(){
        if(enabled)
            handleChangeCurrencyValuesCommand.run();
    }

    @Scheduled(cron = "${app.scheduler.check-aggregation-filling}")
    public void checkAggregationFilling(){
        if(enabled)
            checkAggregationFillingCommand.run();
    }
}
