package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.data.CurrencyValue;
import com.jfund.currencyaggregationsservice.data.DateTimeAndValue;
import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import com.jfund.currencyaggregationsservice.entity.CurrencyPeriod;
import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
import com.jfund.currencyaggregationsservice.exception.ApplyChangeValuesEventException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ApplyChangeValuesEventService {
    private final CurrencyValuesAggregationService aggregationService;
    private final CurrencyPeriodService periodService;
    private final ChangeCurrencyValuesEventService changeCurrencyValuesEventService;


    public Flux<CurrencyValuesAggregation> apply(ChangeCurrencyValuesEvent event) {

        return periodService
                .getActualPeriods()
                .collectList()
                .flatMapMany(periodList -> aggregationService
                        .findNewInRangeAggregations(event.getDateTime())
                        .collectList()
                        .flatMapMany(aggregations ->
                                performAction(aggregations, periodList, event)));


    }

    private Flux<CurrencyValuesAggregation> performAction(List<CurrencyValuesAggregation> aggregations,
                                                          List<CurrencyPeriod> periodList,
                                                          ChangeCurrencyValuesEvent event) {
        if(periodList == null || periodList.isEmpty()){
            return Flux.error(new ApplyChangeValuesEventException("No periods found"));
        }

        if(event == null || event.getValues() == null || event.getValues().isEmpty()){
            return Flux.error(new ApplyChangeValuesEventException("No values found"));
        }

        Map<PeriodAndCurrencyKey, CurrencyValuesAggregation> aggregationMap = buildAggregationMap(aggregations);
        List<CurrencyValuesAggregation> aggregationsToSave = new LinkedList<>();

        for(CurrencyPeriod period: periodList){
           for(CurrencyValue value: event.getValues()){
               CurrencyValuesAggregation aggregation = aggregationMap.get(new PeriodAndCurrencyKey(period.getSeconds(), value.getKey()));
               if(aggregation == null){
                   aggregation = createAggregation(period, value, event);
               }

               aggregation.add(new DateTimeAndValue(event.getDateTime(), value.getValue()));
               aggregationsToSave.add(aggregation);
           }
        }

        return aggregationService.saveAll(aggregationsToSave);
    }

    private CurrencyValuesAggregation createAggregation(CurrencyPeriod period, CurrencyValue value, ChangeCurrencyValuesEvent event) {
        long durationSeconds = Duration.between(period.getStartDateTime(), event.getDateTime()).getSeconds();
        LocalDateTime minDateTime = event.getDateTime().minusSeconds(durationSeconds % period.getSeconds());
        LocalDateTime maxDateTime = minDateTime.plusSeconds(period.getSeconds());

        return new CurrencyValuesAggregation(minDateTime, maxDateTime, period.getLabel(), value.getKey());
    }

    private Map<PeriodAndCurrencyKey, CurrencyValuesAggregation> buildAggregationMap(List<CurrencyValuesAggregation> aggregations) {
        Map<PeriodAndCurrencyKey, CurrencyValuesAggregation> map = new HashMap<>();
        aggregations.forEach(aggregation -> map.put(
                new PeriodAndCurrencyKey(aggregation.getDurationSeconds(), aggregation.getCurrencyKey()),
                aggregation));

        return map;

    }

    @Data
    @AllArgsConstructor
    private static class PeriodAndCurrencyKey {
        private Long periodSeconds;
        private String currency;
    }
}
