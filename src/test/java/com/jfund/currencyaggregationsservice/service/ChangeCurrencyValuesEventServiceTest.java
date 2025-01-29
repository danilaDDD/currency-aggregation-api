package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.repository.ChangeCurrencyValuesEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ChangeCurrencyValuesEventServiceTest {
    @Autowired
    private ChangeCurrencyValuesEventService changeCurrencyValuesEventService;
    @Autowired
    private ChangeCurrencyValuesEventRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll().block();
    }

    @Test
    void testConsume_WhenValidInput_ThenSaveSuccessfully() {
    }

    @Test
    void testConsume_WhenInvalidInput_ThenThrowException() {
    }
}