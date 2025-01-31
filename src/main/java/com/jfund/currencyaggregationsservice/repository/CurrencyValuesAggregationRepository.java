package com.jfund.currencyaggregationsservice.repository;

import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CurrencyValuesAggregationRepository
        extends ReactiveMongoRepository<CurrencyValuesAggregation, String> {

    Flux<CurrencyValuesAggregation> findAllByOrderByMinDateTimeAscMaxDateTimeAsc();
}