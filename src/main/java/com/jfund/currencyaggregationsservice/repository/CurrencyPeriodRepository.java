package com.jfund.currencyaggregationsservice.repository;

import com.jfund.currencyaggregationsservice.entity.CurrencyPeriod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CurrencyPeriodRepository
        extends ReactiveMongoRepository<CurrencyPeriod, String> {
}