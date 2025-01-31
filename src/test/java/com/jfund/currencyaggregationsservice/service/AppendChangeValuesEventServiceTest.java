package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.entity.ChangeCurrencyValuesEvent;
import com.jfund.currencyaggregationsservice.entity.CurrencyPeriod;
import com.jfund.currencyaggregationsservice.entity.CurrencyValuesAggregation;
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
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AppendChangeValuesEventServiceTest {
    @Autowired
    private CurrencyPeriodRepository periodRepository;
    @Autowired
    private CurrencyValuesAggregationRepository aggregationRepository;
    @Autowired
    private AppendChangeValuesEventService service;
    @Autowired
    private ChangeCurrencyValuesEventRepository changeCurrencyValuesEventRepository;
    @Autowired
    private CurrencyPeriodRepository currencyPeriodRepository;

    public static Stream<Arguments> getSuccessfullyTestsData() {
        return Stream.of(
                getFullFillingEveryOfFirstPeriods()
        );
    }

    private static Arguments getFullFillingEveryOfFirstPeriods() {
        return Arguments.of()
    }

    private static void assertAggregation(CurrencyValuesAggregation expectedAggregation, CurrencyValuesAggregation actualAggregation) {
        assertEquals(expectedAggregation.getCurrencyKey(), actualAggregation.getCurrencyKey());
        assertEquals(expectedAggregation.getMinDateTime().getSecond(), actualAggregation.getMinDateTime().getSecond());
        assertEquals(expectedAggregation.getMaxDateTime().getSecond(), actualAggregation.getMaxDateTime().getSecond());
        assertEquals(expectedAggregation.getPeriodLabel(), actualAggregation.getPeriodLabel());
        assertEquals(new HashSet<>(expectedAggregation.getTimeAndValueList()), new HashSet<>(actualAggregation.getTimeAndValueList()));
        assertEquals(expectedAggregation.isClosed(), actualAggregation.isClosed());
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
        LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 2, 12, 10);
        currencyPeriodRepository
                .saveAll(periods)
                .collectList()
                .block();

        inputEvents.forEach(service::append);

        var firsStep = StepVerifier.create(aggregationRepository
                .findAllByOrderByMinDateTimeAscMaxDateTimeAsc());
        StepVerifier.Step<CurrencyValuesAggregation> step = null;
        for(CurrencyValuesAggregation expectedAggregation : expectedAggregations){
            step = firsStep.assertNext(actualAggregation -> {
               assertAggregation(expectedAggregation, actualAggregation);
            });
        }
        if(step != null){
            step.verifyComplete();
        }
    }



    @Test
    void testAppend_WhenEventWithNotNullValuesAndNotExistCurrencyPeriods_ThenThrowAppendChangeValuesEventException(){

    }

    @Test
    void testAppend_WhenEventWithEmptyValues_ThenThrowAppendChangeValuesEventException(){

    }

    private List<CurrencyPeriod> saveCurrencyPeriods(LocalDateTime startDateTime) {

        List<CurrencyPeriod> currencyPeriods = List.of(
                new CurrencyPeriod("min", 60L, startDateTime),
                new CurrencyPeriod("hour", 3600L, startDateTime),
                new CurrencyPeriod("day", 86400L, startDateTime)
        );
        return periodRepository.saveAll(currencyPeriods).collectList().block();
    }

    private ChangeCurrencyValuesEvent saveChangeCurrencyValuesEvent() {
        return changeCurrencyValuesEventRepository.save(new ChangeCurrencyValuesEvent());
    }

}