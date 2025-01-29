package com.jfund.currencyaggregationsservice.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
public class DateTimeAndValue {
    @EqualsAndHashCode.Include
    private LocalDateTime dateTime;
    private Float value;
}
