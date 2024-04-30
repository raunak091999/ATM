package com.deloitte.atm.demo.core.impl;

import com.deloitte.atm.demo.core.AtmService;
import com.deloitte.atm.demo.dto.Amount;
import com.deloitte.atm.demo.dto.TransactionClearType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.UUID;

class MapBasedAtmServiceTest {

    private AtmService atmServiceStorage;

    @BeforeEach
    void setUp() {
        atmServiceStorage = MapBasedAtmService.getInstance();
    }

    @Test
    void testWithdrawalAuthorization_whenAmountIsZero_thenThrowError() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> atmServiceStorage.withdrawalAuthorization(BigDecimal.ZERO));

    }

    @Test
    void testWithdrawalAuthorization_whenAmountIsGtZero_thenSuccess() {
        Amount withdrawal = atmServiceStorage.withdrawalAuthorization(new BigDecimal(100));
        Assertions.assertNotNull(withdrawal);
    }

    @ParameterizedTest
    @CsvSource({"2", "33", "444", "5555"})
    void testWithdrawalAuthorization_whenAmountIsNotAMultipleOf10_thenThrowError(Long amount) {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> atmServiceStorage.withdrawalAuthorization(BigDecimal.valueOf(amount)));
    }

    private UUID createTransaction(BigDecimal amount) {
        return atmServiceStorage.withdrawalAuthorization(amount).getAuthorizationId();
    }

    @Test
    void testWithdrawalClearing_whenAuthorizationIdIsInvalid_thenThrowError() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> atmServiceStorage.withdrawalClearing(UUID.randomUUID(), TransactionClearType.CLEARING));
    }

    @Test
    void testWithdrawalClearing_whenAuthorizationIdIsValid_thenSuccess() {
        Amount originalAmount = atmServiceStorage.checkBalance();
        UUID authorizationId = createTransaction(new BigDecimal(100));
        Amount withdrawal = atmServiceStorage.withdrawalClearing(authorizationId, TransactionClearType.CLEARING);
        Assertions.assertNotNull(withdrawal);
        Amount updatedAmount = atmServiceStorage.checkBalance();
        Assertions.assertNotEquals(originalAmount, updatedAmount);
    }


}