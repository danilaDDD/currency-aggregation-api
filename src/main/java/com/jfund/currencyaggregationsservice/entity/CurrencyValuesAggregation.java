package com.jfund.currencyaggregationsservice.entity;

import com.jfund.currencyaggregationsservice.data.DateTimeAndValue;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document
public class CurrencyValuesAggregation implements Cloneable {
    @Id
    private String id;
    private String currencyKey;
    private LocalDateTime minDateTime;
    private LocalDateTime maxDateTime;
    private String periodLabel;
    private List<DateTimeAndValue> timeAndValueList;
    private Status status;

    public CurrencyValuesAggregation(LocalDateTime minDateTime, LocalDateTime maxDateTime, String periodLabel, String currencyKey) {
        this(null, currencyKey, minDateTime, maxDateTime, periodLabel, new ArrayList<>(), Status.NEW);
    }

    public Long getDurationSeconds(){
        return Duration.between(minDateTime, maxDateTime).getSeconds();
    }

    public void add(DateTimeAndValue dateTimeAndValue) {
        timeAndValueList.add(dateTimeAndValue);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CurrencyValuesAggregation that = (CurrencyValuesAggregation) object;
        return Objects.equals(currencyKey, that.currencyKey) &&
                Objects.equals(status, that.status) &&
                Objects.equals(minDateTime.withNano(0), that.minDateTime.withNano(0)) &&
                Objects.equals(maxDateTime.withNano(0), that.maxDateTime.withNano(0));
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyKey, status,
                minDateTime.withNano(0), maxDateTime.withNano(0));
    }

    @Override
    public CurrencyValuesAggregation clone() {
        try {
            CurrencyValuesAggregation clone = (CurrencyValuesAggregation) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static enum Status{
        NEW, FILLING, HANDED, ERROR
    }
}
