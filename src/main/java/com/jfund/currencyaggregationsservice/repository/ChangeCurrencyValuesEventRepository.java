package com.jfund.currencyaggregationsservice.repository;

import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChangeCurrencyValuesEventRepository
        extends ReactiveMongoRepository<ChangeCurrencyValuesEvent, String> {
    Flux<ChangeCurrencyValuesEvent> findAllBySyncedFalse();

    Mono<ChangeCurrencyValuesEvent> findFirstByOrderByDateTimeDesc();
}