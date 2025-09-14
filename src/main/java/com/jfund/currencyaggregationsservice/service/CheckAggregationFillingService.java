package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.data.DateTimeAndValue;
import com.jfund.currencyaggregationsservice.entity.CurrencyPeriod;
import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckAggregationFillingService {
    private final ChangeCurrencyValuesEventService eventService;
    private final CurrencyValuesAggregationService aggregationService;
    private final CurrencyPeriodService periodService;

    public Mono<Void> check(){
        return eventService
                .getLastChangeValuesEvent()
                .flatMapMany(lastEvent -> periodService
                        .getActualPeriods()
                        .collectList()
                        .flatMapMany(periods -> aggregationService
                                .findNewLessAndEqualsMaxDateTime(lastEvent.getDateTime())
                                .collectList()
                                .flatMapMany(aggregations -> performAction(aggregations, periods))
                        )).then();

    }

    private Flux<CurrencyValuesAggregation> performAction(List<CurrencyValuesAggregation> aggregations,
                                                          List<CurrencyPeriod> periods) {
        if(periods.isEmpty())
            return Flux.empty();
        if(aggregations.isEmpty())
            return Flux.empty();

        Map<String, CurrencyPeriod> periodMap = new HashMap<>();
        periods.forEach(period -> periodMap.put(period.getLabel(),  period));

        List<CurrencyValuesAggregation> aggregationsToUpdate = aggregations.stream()
                .filter(agg -> setStatusAndCheck(agg, periodMap.get(agg.getPeriodLabel())))
                .toList();

        if(!aggregationsToUpdate.isEmpty())
            return aggregationService.saveAll(aggregationsToUpdate);

        return Flux.empty();
    }

    private boolean setStatusAndCheck(CurrencyValuesAggregation aggregation, CurrencyPeriod currencyPeriod) {
        List<DateTimeAndValue> values = aggregation.getTimeAndValueList();

        if(values.size() < currencyPeriod.getMinCountValues()){
            return false;
        }

        if(!aggregationIsEvenlyFilled(aggregation)){
            return false;
        }

        aggregation.setStatus(CurrencyValuesAggregation.Status.FILLING);
        return true;
    }

    private boolean aggregationIsEvenlyFilled(CurrencyValuesAggregation aggregation) {
        long durationSecond = Duration.between(aggregation.getMinDateTime(), aggregation.getMaxDateTime()).getSeconds();
        long step = durationSecond / 20;
        LocalDateTime dateTime = aggregation.getMaxDateTime().minusSeconds(step);

        long countAfterValues = aggregation.getTimeAndValueList().stream()
                .filter(data -> data.getDateTime().isAfter(dateTime))
                .count();

        return countAfterValues > 0;
    }
}
