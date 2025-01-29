package com.jfund.currencyaggregationsservice.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document
public class Candle {
    @Id
    private String id;
    private String currencyKey;
    private String periodLabel;
    private String periodId;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Float openValue;
    private Float closeValue;
    private Float minValue;
    private Float maxValue;
}
