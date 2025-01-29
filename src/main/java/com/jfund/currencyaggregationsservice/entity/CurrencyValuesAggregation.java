package com.jfund.currencyaggregationsservice.entity;

import com.jfund.currencyaggregationsservice.data.DateTimeAndValue;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document
public class CurrencyValuesAggregation {
    @Id
    private String id;
    private String currencyKey;
    private LocalDateTime minDateTime;
    private LocalDateTime maxDateTime;
    private String periodLabel;
    private List<DateTimeAndValue> timeAndValueList;
    private boolean closed;

    public CurrencyValuesAggregation(LocalDateTime minDateTime, LocalDateTime maxDateTime, String periodLabel, String currencyKey) {
        this(null, currencyKey, minDateTime, maxDateTime, periodLabel, new LinkedList<>(), false);
    }

    public void add(DateTimeAndValue dateTimeAndValue) {
        timeAndValueList.add(dateTimeAndValue);
    }
}
