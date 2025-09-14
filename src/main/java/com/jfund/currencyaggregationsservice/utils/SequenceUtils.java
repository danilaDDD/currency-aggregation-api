package com.jfund.currencyaggregationsservice.utils;

import com.jfund.currencyaggregationsservice.data.Sequence;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class SequenceUtils {
    Sequence generate(LocalDateTime start, LocalDateTime end, int valuesNumber){
        long step = Duration.between(start, end).getSeconds() / valuesNumber;
        Sequence sequence = new Sequence();
        Random random = new Random();
        for (int i = 0;; i++) {
            LocalDateTime dateTime = start.plusSeconds(step * i);

            if(dateTime.isAfter(end))
                break;

            sequence.add(dateTime, random.nextFloat());
        }

        return sequence;
    }

}
