package com.jfund.currencyaggregationsservice.entity;

import com.jfund.currencyaggregationsservice.data.CurrencyValue;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document
public class ChangeCurrencyValuesEvent {
    @Id
    private String id;
    private List<CurrencyValue> values;
    private LocalDateTime dateTime;
    private boolean synced;

    public ChangeCurrencyValuesEvent(String id, List<CurrencyValue> values, LocalDateTime dateTime) {
        this(id, values, dateTime, false);
    }

    public ChangeCurrencyValuesEvent(List<CurrencyValue> values, LocalDateTime dateTime) {
       this(null, values, dateTime, false);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ChangeCurrencyValuesEvent that = (ChangeCurrencyValuesEvent) object;
        if(id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }else {
            return Objects.equals(dateTime, that.dateTime);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id != null ? id : dateTime);
    }
}
