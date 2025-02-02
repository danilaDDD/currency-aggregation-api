package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.data.CurrencyValue;
import com.jfund.currencyaggregationsservice.data.DateTimeAndValue;
import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import com.jfund.currencyaggregationsservice.entity.CurrencyPeriod;
import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
import com.jfund.currencyaggregationsservice.exception.ApplyChangeValuesEventException;
import com.jfund.currencyaggregationsservice.repository.ChangeCurrencyValuesEventRepository;
import com.jfund.currencyaggregationsservice.repository.CurrencyPeriodRepository;
import com.jfund.currencyaggregationsservice.repository.CurrencyValuesAggregationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class ApplyChangeValuesEventServiceTest {
    @Autowired
    private CurrencyPeriodRepository periodRepository;
    @Autowired
    private CurrencyValuesAggregationRepository aggregationRepository;
    @Autowired
    private ApplyChangeValuesEventService service;
    @Autowired
    private ChangeCurrencyValuesEventRepository changeCurrencyValuesEventRepository;
    @Autowired
    private CurrencyPeriodRepository currencyPeriodRepository;

    public static Stream<Arguments> getSuccessfullyTestsData() {
        return Stream.of(
                performApply1EventWith1Period(),
                performSequentialAddingOfEvents()
                //performAddingOfEventsWithDifferentCurrencyKeys()
        );
    }

    private static Arguments performApply1EventWith1Period() {
        long periodSeconds = 3600L;
        LocalDateTime now, startDateTime, endDateTime;
        now = LocalDateTime.now();
        startDateTime = now.minusMinutes(10L);
        endDateTime = startDateTime.plusSeconds(periodSeconds);


        CurrencyPeriod period = new CurrencyPeriod("hour", periodSeconds, startDateTime);
        CurrencyValue value = new CurrencyValue("EURUSD", 1.0F);
        ChangeCurrencyValuesEvent event = new ChangeCurrencyValuesEvent(List.of(value), now);

        CurrencyValuesAggregation expectedAggregation = new CurrencyValuesAggregation(
                startDateTime, endDateTime, period.getLabel(), value.getKey());
        expectedAggregation.add(new DateTimeAndValue(event.getDateTime(), value.getValue()));

        return Arguments.of(List.of(period), List.of(event), List.of(expectedAggregation));
    }

    private static Arguments performSequentialAddingOfEvents() {
        LocalDateTime startDateTime = LocalDateTime.of(2021, 1, 1, 0, 0);
        List<CurrencyPeriod> periods = List.of(
                new CurrencyPeriod("min", 60L, startDateTime),
                new CurrencyPeriod("15min", 900L, startDateTime),
                new CurrencyPeriod("hour", 3600L, startDateTime)
        );

        List<String> currencyPairs = List.of("EURUSD", "USDJPY", "GBPUSD");

        LocalDateTime endDateTime = startDateTime.plusHours(3);
        List<CurrencyValuesAggregation> expectedAggregations = new ArrayList<>();
        int secondsStep = 5;
        Random random = new Random();
        Map<CurrencyPeriod, CurrencyValuesAggregation> periodToAggregation = new HashMap<>();
        List<ChangeCurrencyValuesEvent> events = new ArrayList<>();

        for(LocalDateTime dateTime = startDateTime; dateTime.isBefore(endDateTime); dateTime = dateTime.plusSeconds(secondsStep)){
            long diffSeconds = Duration.between(startDateTime, dateTime).getSeconds();
            List<CurrencyValue> currencyValues = new ArrayList<>();
            for(String pair: currencyPairs){
                CurrencyValue value = new CurrencyValue(pair, random.nextFloat());
                currencyValues.add(value);

                for(CurrencyPeriod period: periods){
                    if(diffSeconds % period.getSeconds() == 0){
                        CurrencyValuesAggregation newAggregation = new CurrencyValuesAggregation(
                                dateTime, dateTime.plusSeconds(period.getSeconds()), period.getLabel(), pair);
                        periodToAggregation.put(period, newAggregation);
                        expectedAggregations.add(newAggregation);
                    }

                    CurrencyValuesAggregation aggregation = periodToAggregation.get(period);
                    aggregation.add(new DateTimeAndValue(dateTime, value.getValue()));
                }

            }

            events.add(new ChangeCurrencyValuesEvent(currencyValues, dateTime));
        }

        return Arguments.of(periods, events, expectedAggregations);
    }

    @BeforeEach
    public void setUp() {
        periodRepository.deleteAll().block();
        aggregationRepository.deleteAll().block();
    }

    @ParameterizedTest
    @MethodSource("getSuccessfullyTestsData")
    void testCreateAggregationsByChangeValueEvents(List<CurrencyPeriod> periods,
                                                   List<ChangeCurrencyValuesEvent> inputEvents,
                                                   List<CurrencyValuesAggregation> expectedAggregations){

        currencyPeriodRepository
                .saveAll(periods)
                .collectList()
                .block();

        inputEvents.forEach(event -> service.apply(event).collectList().block());

        Mono<Set<CurrencyValuesAggregation>> actualAggregationsSet = Mono.from(
                aggregationRepository
                .findAll()
                .collectList()
                .map(HashSet::new));
        StepVerifier.create(actualAggregationsSet)
                .expectNext(new HashSet<>(expectedAggregations))
                .verifyComplete();
    }





    @Test
    void testAppend_WhenEventWithNotNullValuesAndNotExistCurrencyPeriods_ThenThrowApplyChangeValuesEventException(){
        ChangeCurrencyValuesEvent event = new ChangeCurrencyValuesEvent(
                List.of(new CurrencyValue("EURUSD", 1.0F)),
                LocalDateTime.now());

        StepVerifier.create(service.apply(event))
                .expectError(ApplyChangeValuesEventException.class)
                .verify();
    }

    @Test
    void testAppend_WhenEventWithEmptyValues_ThenThrowApplyChangeValuesEventException(){
        ChangeCurrencyValuesEvent event = new ChangeCurrencyValuesEvent(
                List.of(),
                LocalDateTime.now());

        CurrencyPeriod period = new CurrencyPeriod("hour", 3600L, LocalDateTime.now().minusMinutes(10L));

        StepVerifier.create(service.apply(event))
                .expectError(ApplyChangeValuesEventException.class)
                .verify();
    }


}