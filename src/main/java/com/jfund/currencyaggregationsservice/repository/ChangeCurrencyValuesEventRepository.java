package com.jfund.currencyaggregationsservice.repository;

import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChangeCurrencyValuesEventRepository
        extends ReactiveMongoRepository<ChangeCurrencyValuesEvent, String> {
    Flux<ChangeCurrencyValuesEvent> findAllBySyncedFalse();
}