package com.jfund.currencyaggregationsservice.data;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Sequence {
    private final Set<DateTimeAndValue> valueSet;

    public Sequence(){
        valueSet = new TreeSet<>(
                Comparator.comparing(DateTimeAndValue::getDateTime));
    }

    public Sequence(Set<DateTimeAndValue> valueSet) {
        this.valueSet = new TreeSet<>(Comparator.comparing(DateTimeAndValue::getDateTime));
        this.valueSet.addAll(valueSet);
    }

    public void add(LocalDateTime dateTime, float value){
        valueSet.add(new DateTimeAndValue(dateTime, value));
    }

    public List<DateTimeAndValue> toRangeList(LocalDateTime start, LocalDateTime end){
        return valueSet.stream()
                .filter(data -> {
                    LocalDateTime dateTime = data.getDateTime();
                    return (dateTime.equals(start) || dateTime.isAfter(start)) &&
                            (dateTime.equals(end) || dateTime.isBefore(end));
                })
                .toList();
    }
}
