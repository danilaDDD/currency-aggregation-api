package com.jfund.currencyaggregationsservice.exception;

import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;

public class ConsumingChangeCurrencyValuesException extends RuntimeException {
    public ConsumingChangeCurrencyValuesException(String message) {
        super(message);
    }

    public ConsumingChangeCurrencyValuesException(ChangeCurrencyValuesEvent event) {
        super("Invalid event: " + event);
    }
}
