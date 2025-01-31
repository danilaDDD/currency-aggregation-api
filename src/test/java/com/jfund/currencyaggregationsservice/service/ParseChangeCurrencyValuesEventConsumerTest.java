package com.jfund.currencyaggregationsservice.service;

import com.jfund.currencyaggregationsservice.data.CurrencyValue;
import com.jfund.currencyaggregationsservice.exception.ConsumingChangeCurrencyValuesException;
import com.jfund.currencyaggregationsservice.repository.ChangeCurrencyValuesEventRepository;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ParseChangeCurrencyValuesEventConsumerTest {
    @Autowired
    private ParseChangeCurrencyValuesEventService service;

    @Test
    void testParse_WhenValidInput_ThenSaveSuccessfully() {
        String currencyValuesString = "{ \"id\":\"04241b74-679d-47dd-bc4c-37566c766209\",\"serializedValues\":\"[{\\\"key\\\":\\\"CADAUD\\\",\\\"value\\\":1.1114}]\",\"changedDateTime\":[2025,1,29,19,22,2,564000000]}";

        StepVerifier.create(service.fromString(currencyValuesString))
                .assertNext(event -> {
                    assertNotNull(event);
                    assertEquals("04241b74-679d-47dd-bc4c-37566c766209", event.getId());
                    assertEquals(new CurrencyValue("CADAUD", 1.1114F), event.getValues().get(0));
                    assertEquals(1, event.getValues().size());
                    assertEquals(
                            LocalDateTime.of(2025, 1, 29, 19, 22, 2, 564000000),
                            event.getDateTime());
                })
                .verifyComplete();

    }

    @ParameterizedTest
    @MethodSource("getInvalidCurrencyValuesStrings")
    void testParse_WhenInvalid_ThenThrowException(String currencyValuesString) {
        StepVerifier.create(service.fromString(currencyValuesString))
                .expectError(ConsumingChangeCurrencyValuesException.class)
                .verify();
    }

    public static Stream<String> getInvalidCurrencyValuesStrings() {
        return Stream.of(
                "{ \"id\":\"04241b74-679d-47dd-bc4c-37566c766209\",\"serializedValues\":\\\"key\\\":\\\"CADAUD\\\",\\\"value\\\":1.1114}]\",\"changedDateTime\":[2025,1,29,19,22,2,564000000]}",
                "{ \"id\":\"04241b74-679d-47dd-bc4c-37566c766209\",\"serializedValueeeees\":\"[{\\\"key\\\":\\\"CADAUD\\\",\\\"value\\\":1.1114}]\",\"changedDateTime\":[2025,1,29,19,22,2,564000000]}",
                "{ \"id\":\"04241b74-679d-47dd-bc4c-37566c766209\",\"serializedValues\":\"{{\\\"key\\\":\\\"CADAUD\\\",\\\"value\\\":1.1114}}\",\"changedDateTime\":[2025,1,29,19,22,2,564000000]}",
                "{ \"id\":null,\"serializedValues\":\"[{\\\"key\\\":\\\"CADAUD\\\",\\\"value\\\":1.1114}]\",\"changedDateTime\":[2025,1,29,19,22,2,564000000]}",
                "{ \"id\":\"04241b74-679d-47dd-bc4c-37566c766209\",\"serializedValues\":\"[{\\\"key\\\":\\\"CADAUD\\\",\\\"value\\\":1.1114}]\",\"changedDateTime\":[2025,1,29,19,22,2,564000000]}",
                "", null);

    }
}