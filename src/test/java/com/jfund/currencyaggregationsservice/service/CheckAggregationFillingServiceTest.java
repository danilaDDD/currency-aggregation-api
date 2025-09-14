package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.data.CurrencyValue;
import com.jfund.currencyaggregationsservice.data.DateTimeAndValue;
import com.jfund.currencyaggregationsservice.data.Sequence;
import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import com.jfund.currencyaggregationsservice.entity.CurrencyPeriod;
import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
import com.jfund.currencyaggregationsservice.repository.ChangeCurrencyValuesEventRepository;
import com.jfund.currencyaggregationsservice.repository.CurrencyPeriodRepository;
import com.jfund.currencyaggregationsservice.repository.CurrencyValuesAggregationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@SpringBootTest
@ActiveProfiles("test")
class CheckAggregationFillingServiceTest {
    private static final LocalDateTime START_DATE_TIME = LocalDateTime
            .of(2023, 10, 5, 1, 1, 30);
    private static final String[] KEYS = {"EURUSD", "USDAUD"};

    @Autowired
    private CheckAggregationFillingService service;
    @Autowired
    private CurrencyValuesAggregationRepository aggregationRepository;
    @Autowired
    private CurrencyPeriodRepository periodRepository;
    @Autowired
    private ChangeCurrencyValuesEventRepository eventRepository;

    public static Stream<Arguments> getAggregationSequences() {
        return Stream.of(
                get1FillingAggregationShouldStayNewStatus(),
                get1FillingAggregationAnd1NotExistNextAggregationShouldMarkingFillingStatus(),
                get1AggregationWithDeficientValuesAnd1NotEmptyAggregationShouldMarkingErrorStatus()
        );
    }

    private static Arguments get1AggregationWithDeficientValuesAnd1NotEmptyAggregationShouldMarkingErrorStatus() {
        List<CurrencyPeriod> periods = List.of(
                new CurrencyPeriod("hour", 3600L, START_DATE_TIME),
                new CurrencyPeriod("30min", 1800L, START_DATE_TIME.plusSeconds(15)),
                new CurrencyPeriod("2hour", 7200L, START_DATE_TIME)
        );

        var globalEnd = START_DATE_TIME.plusSeconds(7200);
        int length = periods.size() * KEYS.length + 2;
        List<CurrencyValuesAggregation> inputAggregations = new ArrayList<>(length);
        List<CurrencyValuesAggregation> expectedAggregations = new ArrayList<>(length);
        Random random = new Random();

        for(String key: KEYS){
            Sequence sequence = generateSequence(START_DATE_TIME, globalEnd, 100);
            for(CurrencyPeriod period: periods){
                var start1 = period.getStartDateTime();
                var end1 = start1.plusSeconds(period.getSeconds());
                List<DateTimeAndValue> values1 = sequence.toRangeList(start1, end1);
                var agg1 = new CurrencyValuesAggregation(start1, end1, period.getLabel(), key);
                agg1.setTimeAndValueList(values1);
                inputAggregations.add(agg1);
                var clone1 = agg1.clone();
                clone1.setStatus(CurrencyValuesAggregation.Status.ERROR);

                var end2 = end1.plusSeconds(period.getSeconds());
                var agg2 = new CurrencyValuesAggregation(end1, end2, period.getLabel(), key);
                List<DateTimeAndValue> values2 = List.of(
                        new DateTimeAndValue(end1.plusSeconds(3), random.nextFloat()),
                        new DateTimeAndValue(end1.plusSeconds(10), random.nextFloat())
                );
                agg2.setTimeAndValueList(values2);
                inputAggregations.add(agg2);
                expectedAggregations.add(agg2.clone());
            }
        }

        return Arguments.of(globalEnd, periods, inputAggregations, expectedAggregations);
    }

    private static Arguments get1FillingAggregationAnd1NotExistNextAggregationShouldMarkingFillingStatus() {
        List<CurrencyPeriod> periods = List.of(
                new CurrencyPeriod("min", 60L, START_DATE_TIME),
                new CurrencyPeriod("30min", 1800L, START_DATE_TIME)
        );
        var globalStart = START_DATE_TIME;
        var globalEnd = globalStart.plusSeconds(3600L);
        int length = 2 * periods.size() * KEYS.length;
        List<CurrencyValuesAggregation> inputAggregations = new ArrayList<>(length);
        List<CurrencyValuesAggregation> expectedAggregations = new ArrayList<>(length);

        for(String key: KEYS){
            Sequence sequence = generateSequence(globalStart, globalEnd, 30);
            for(CurrencyPeriod period: periods){
                LocalDateTime end1 = globalStart.plusSeconds(period.getSeconds());
                List<DateTimeAndValue> values = sequence.toRangeList(globalStart, end1);
                var agg1 = new CurrencyValuesAggregation(globalStart, end1, period.getLabel(), key);
                agg1.setTimeAndValueList(values);
                inputAggregations.add(agg1);
                var aggClone1 = agg1.clone();
                aggClone1.setStatus(CurrencyValuesAggregation.Status.FILLING);
                expectedAggregations.add(aggClone1);

                var end2 = end1.plusSeconds(period.getSeconds());
                List<DateTimeAndValue> values2 = sequence.toRangeList(end1, end2);
                var agg2 = new CurrencyValuesAggregation(end1, end2, period.getLabel(), key);
                agg2.setTimeAndValueList(values2);
                inputAggregations.add(agg2);
                expectedAggregations.add(agg2.clone());
            }
        }

        return Arguments.of(globalEnd, periods, inputAggregations, expectedAggregations);
    }

    private static Arguments get1FillingAggregationShouldStayNewStatus() {
        List<CurrencyPeriod> periods = List.of(
                new CurrencyPeriod("10min", 600L, START_DATE_TIME),
                new CurrencyPeriod("15min", 900L, START_DATE_TIME),
                new CurrencyPeriod("hour", 3600L, START_DATE_TIME)
        );
        var globalStart = START_DATE_TIME;
        var globalEnd = START_DATE_TIME.plusSeconds(3600L);

        int length = 2 * periods.size() * KEYS.length;
        List<CurrencyValuesAggregation> inputAggregations = new ArrayList<>(length);
        List<CurrencyValuesAggregation> expectedAggregations = new ArrayList<>(length);

        for(String key: KEYS){
            Sequence sequence = generateSequence(globalStart, globalEnd, 20);

            for(CurrencyPeriod period: periods){
                var end = globalStart.plusSeconds(period.getSeconds());
                List<DateTimeAndValue> values = sequence.toRangeList(globalStart, end);
                var agg = new CurrencyValuesAggregation(globalStart, end, period.getLabel(), key);
                agg.setTimeAndValueList(values);
                inputAggregations.add(agg);
                expectedAggregations.add(agg.clone());
            }
        }

        return Arguments.of(globalEnd, periods, inputAggregations, expectedAggregations);
    }

    private static Sequence generateSequence(LocalDateTime startDateTime, LocalDateTime endDateTime, int valuesCount) {
        long step = Duration.between(startDateTime, endDateTime).getSeconds() / valuesCount;
        LocalDateTime dateTime = startDateTime;
        Random random = new Random();
        Sequence sequence = new Sequence();

        for (int i = 0; dateTime.isBefore(endDateTime); i++) {
            sequence.add(dateTime, random.nextFloat());
            dateTime = dateTime.plusSeconds(step * i);
        }

        return sequence;
    }

    @BeforeEach
    public void setUp(){
        aggregationRepository.deleteAll().block();
        periodRepository.deleteAll().block();
        eventRepository.deleteAll().block();
    }

    @ParameterizedTest
    @MethodSource("getAggregationSequences")
    void testCheckShouldSuccessfullySave(
            LocalDateTime lastChangeDateTime,
            List<CurrencyPeriod> periods,
            List<CurrencyValuesAggregation> inputAggregations,
            List<CurrencyValuesAggregation> expectedAggregations){

        Random random = new Random();
        List<CurrencyValue> currencyValues = Stream.of(KEYS)
                .map(key -> new CurrencyValue(key, random.nextFloat()))
                .toList();
        eventRepository
                .save(new ChangeCurrencyValuesEvent(currencyValues, lastChangeDateTime))
                .block();

        aggregationRepository
                .saveAll(inputAggregations)
                .collectList()
                .block();
        periodRepository.saveAll(periods).collectList().block();

        service.check().block();

        Mono<Set<CurrencyValuesAggregation>> actual = aggregationRepository
                .findAll()
                .collectList()
                .map(HashSet::new);
        StepVerifier.create(actual)
                .expectNext(new HashSet<>(expectedAggregations))
                .verifyComplete();

    }
}