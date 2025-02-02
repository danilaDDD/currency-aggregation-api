package com.jfund.currencyaggregationsservice.repository;

import com.jfund.currencyaggregationsservice.entity.CurrencyPeriod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CurrencyPeriodRepository
        extends ReactiveMongoRepository<CurrencyPeriod, String> {
    Flux<CurrencyPeriod> findAllByActual(boolean actual);
}