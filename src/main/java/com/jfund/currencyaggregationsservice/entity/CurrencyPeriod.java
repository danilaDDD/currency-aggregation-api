package com.jfund.currencyaggregationsservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document
public class CurrencyPeriod {
    @Id
    private String id;
    private String label;
    private Long seconds;
    private LocalDateTime startDateTime;
    private boolean actual;
    private Long minCountValues;

    public CurrencyPeriod(String label, Long seconds, LocalDateTime startDateTime) {
       this(null, label, seconds, startDateTime, true, 5L);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CurrencyPeriod that = (CurrencyPeriod) object;
        if(id != null && that.id != null)
            return Objects.equals(id, that.id);
        else
            return Objects.equals(seconds, that.seconds);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id != null ? id: seconds);
    }
}
