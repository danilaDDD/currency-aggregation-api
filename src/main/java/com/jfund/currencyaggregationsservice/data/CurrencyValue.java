package com.jfund.currencyaggregationsservice.data;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyValue {
    private String key;
    private Float value;
}
