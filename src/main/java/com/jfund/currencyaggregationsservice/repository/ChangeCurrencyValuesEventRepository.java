package com.jfund.currencyaggregationsservice.repository;

import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ChangeCurrencyValuesEventRepository extends ReactiveMongoRepository<ChangeCurrencyValuesEvent, String> {
}