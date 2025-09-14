package com.jfund.currencyaggregationsservice.repository;

import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface CurrencyValuesAggregationRepository
        extends ReactiveMongoRepository<CurrencyValuesAggregation, String> {

    Flux<CurrencyValuesAggregation> findAllByStatus(CurrencyValuesAggregation.Status status);

    Flux<CurrencyValuesAggregation> findByMinDateTimeLessThanEqualAndMaxDateTimeGreaterThanEqualAndStatus(
            LocalDateTime minDateTime, LocalDateTime maxDateTime,
            CurrencyValuesAggregation.Status status);

    Flux<CurrencyValuesAggregation> findByMaxDateTimeLessThanEqualAndStatus(
            LocalDateTime dateTime,
            CurrencyValuesAggregation.Status status);
}