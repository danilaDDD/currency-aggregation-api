package com.jfund.currencyaggregationsservice.consumer;

import com.jfund.currencyaggregationsservice.service.ChangeCurrencyValuesEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyValuesConsumer {
    /**
     *  @param currencyValuesString:
     * {
     * "id":"04241b74-679d-47dd-bc4c-37566c766209",
     * "serializedValues":"[{\"key\":\"CADAUD\",\"value\":1.1114}]",
     * "changedDateTime":[2025,1,29,19,22,2,564000000]
     * }
     */

    private final ChangeCurrencyValuesEventService changeCurrencyValuesEventService;

    @KafkaListener(topics = "${app.kafka.currency-values-topic}", groupId = "currency-values-group")
    public void listenCurrencyValues(String currencyValuesString) {
        changeCurrencyValuesEventService
                .consume(currencyValuesString)
                .doOnError(e -> log.error("Error consuming currency values", e))
                .subscribe();
    }
}
