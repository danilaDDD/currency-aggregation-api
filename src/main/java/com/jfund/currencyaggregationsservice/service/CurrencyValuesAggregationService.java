package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
import com.jfund.currencyaggregationsservice.repository.CurrencyValuesAggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyValuesAggregationService {
    private final CurrencyValuesAggregationRepository repository;

    public Mono<CurrencyValuesAggregation> save(CurrencyValuesAggregation aggregation) {
        return repository.save(aggregation);
    }

    public Flux<CurrencyValuesAggregation> findAllFilling() {
        return repository.findAllByStatus(CurrencyValuesAggregation.Status.FILLING);
    }

    public Flux<CurrencyValuesAggregation> findNewInRangeAggregations(LocalDateTime dateTime) {
        return repository
                .findByMinDateTimeLessThanEqualAndMaxDateTimeGreaterThanEqualAndStatus(dateTime, dateTime, CurrencyValuesAggregation.Status.NEW);
    }

    public Flux<CurrencyValuesAggregation> saveAll(List<CurrencyValuesAggregation> aggregations) {
        return repository.saveAll(aggregations);
    }

    public Flux<CurrencyValuesAggregation> findNewLessAndEqualsMaxDateTime(LocalDateTime dateTime) {
        return repository.findByMaxDateTimeLessThanEqualAndStatus(dateTime, CurrencyValuesAggregation.Status.NEW);
    }
}
