package com.jfund.currencyaggregationsservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfund.currencyaggregationsservice.data.CurrencyValue;
import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParseChangeCurrencyValuesEventService {
    private final ObjectMapper objectMapper;

    public Mono<ChangeCurrencyValuesEvent> fromString(String currencyValuesString) {
        return Mono.fromCallable(() ->{
            SerializedChangeCurrencyValuesEvent serializedEvent = objectMapper.readValue(currencyValuesString, SerializedChangeCurrencyValuesEvent.class);
            List<CurrencyValue> currencyValues = objectMapper.readValue(serializedEvent.serializedValues,
                    objectMapper
                    .getTypeFactory()
                    .constructCollectionType(List.class, CurrencyValue.class));

            return new ChangeCurrencyValuesEvent(serializedEvent.id, currencyValues, serializedEvent.changedDateTime);
        });
    }

    @Data
    private static class SerializedChangeCurrencyValuesEvent {
        private String id;
        private String serializedValues;
        private LocalDateTime changedDateTime;
    }
}
