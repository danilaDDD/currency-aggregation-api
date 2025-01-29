package com.jfund.currencyaggregationsservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CurrencyValuesConsumer {
    /**
     *  @param currencyValuesString:
     * {
     * "id":"04241b74-679d-47dd-bc4c-37566c766209",
     * "serializedValues":"[{\"key\":\"CADAUD\",\"value\":1.1114}]",
     * "changedDateTime":[2025,1,29,19,22,2,564000000]
     * }
     */
    @KafkaListener(topics = "${app.kafka.currency-values-topic}", groupId = "currency-values-group")
    public void listenCurrencyValues(String currencyValuesString) {
        System.out.println("Received currency values: " + currencyValuesString);
    }
}
