package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.entity.CurrencyPeriod;
import com.jfund.currencyaggregationsservice.repository.CurrencyPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CurrencyPeriodService {
    private final CurrencyPeriodRepository repository;

    public Flux<CurrencyPeriod> getActualPeriods() {
        return repository.findAllByActual(true);
    }
}
