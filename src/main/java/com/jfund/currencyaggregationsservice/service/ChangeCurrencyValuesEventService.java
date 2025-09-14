package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.data.CurrencyValue;
import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import com.jfund.currencyaggregationsservice.exception.SaveChangeCurrencyValuesEventException;
import com.jfund.currencyaggregationsservice.repository.ChangeCurrencyValuesEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChangeCurrencyValuesEventService {
    private final ChangeCurrencyValuesEventRepository repository;

    public Flux<ChangeCurrencyValuesEvent> getActualEvents() {
        return repository
                .findAllBySyncedFalse();
    }

    Mono<ChangeCurrencyValuesEvent> save(ChangeCurrencyValuesEvent event) {
        try {
            verify(event);
            return repository
                    .save(event);
        }catch (SaveChangeCurrencyValuesEventException e){
            return Mono.error(e);
        }
    }

    private void verify(ChangeCurrencyValuesEvent event) {
        if(event.getValues().isEmpty()){
            throw new SaveChangeCurrencyValuesEventException("Values are empty");
        }

        Set<CurrencyValue> currencyValueSet = new HashSet<>(event.getValues());
        if(currencyValueSet.size() != event.getValues().size()){
            throw new SaveChangeCurrencyValuesEventException("Values contain duplicates");
        }
    }

    public Mono<ChangeCurrencyValuesEvent> getLastChangeValuesEvent() {
        return repository.findFirstByOrderByDateTimeDesc();
    }
}
