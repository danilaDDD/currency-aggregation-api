package com.jfund.currencyaggregationsservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CreateCandleCommandServiceTest {

        @Test
        void testCreateCandle_WhenAggregationWithValuesFillingWholePeriod_ThenCreateSuccessfully() {

        }

        @Test
        void testCreateCandle_WhenAggregationWithValuesNotFillingWholePeriod_ThenNotCreate(){

        }

        @Test
        void testCreateCandle_WhenAggregationWithValuesNotOutsidePeriod_ThenCandleByValuesIncludedInPeriod(){

        }

        @Test
        void testCreateCandle_WhenAggregationWithEmptyValues_ThenThrowCreateCandleException() {

        }

        @Test
        void testCreateCandle_WhenAggregationWithNullValues_ThenThrowCreateCandleException() {

        }

}