package com.deloitte.atm.demo.core.impl;

import com.deloitte.atm.demo.repository.impl.MapBasedAtmStorage;
import lombok.extern.slf4j.Slf4j;
import com.deloitte.atm.demo.core.AtmService;
import com.deloitte.atm.demo.dto.Amount;
import com.deloitte.atm.demo.dto.TransactionClearType;
import com.deloitte.atm.demo.repository.AtmStorage;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.StreamSupport;

/**
 * ATM implementation using Map
 */
@Slf4j
public class MapBasedAtmService implements AtmService {

    private final AtmStorage<BigInteger> atmStorage;
    private final Map<UUID, Map<BigInteger, BigInteger>> userAuthorization = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    private MapBasedAtmService() {
        log.debug("Initializing ATM... Adding Random Values...");
        atmStorage = new MapBasedAtmStorage<>(Map.of(
                new BigInteger("2000"), new BigInteger("10"),
                new BigInteger("500"), new BigInteger("20"),
                new BigInteger("200"), new BigInteger("30"),
                new BigInteger("100"), new BigInteger("40")
        ), BigInteger.ZERO);
    }

    public MapBasedAtmService(Map<BigInteger, BigInteger> atmStorage) {
        this.atmStorage = new MapBasedAtmStorage<>(atmStorage, BigInteger.ZERO);
    }

    public static MapBasedAtmService getInstance() {
        return new MapBasedAtmService();
    }

    @Override
    public Amount checkBalance() {
        Map<BigInteger, BigInteger> allDenominationCount = atmStorage.getAllDenominationCount();
        return Amount.builder().amount(allDenominationCount).build();
    }

    @Override
    public Amount withdrawalClearing(UUID authorizationId, TransactionClearType clearType) {
        lock.lock();
        Map<BigInteger, BigInteger> heldTransactionAmount = userAuthorization.get(authorizationId);
        if (heldTransactionAmount != null) {
            if (clearType == TransactionClearType.REVERSAL) {
                // If the transaction is cancelled, then revert the amount back to ATM
                for (Map.Entry<BigInteger, BigInteger> entry : heldTransactionAmount.entrySet()) {
                    BigInteger denomination = entry.getKey();
                    BigInteger count = entry.getValue();
                    atmStorage.updateDenominationCount(denomination, atmStorage.getDenominationCount(denomination).add(count));
                }
                userAuthorization.remove(authorizationId);
            } else if (clearType == TransactionClearType.CLEARING) {
                // If the transaction is confirmed, then remove the amount from ATM
                userAuthorization.remove(authorizationId);
            }
        } else {
            // Ideally, in prod a proper Business code would be returned instead of error message, and that would be handled in a Global exception handler
            String errorMessage = "Transaction is already cleared or not found";
            log.warn(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        lock.unlock();
        return Amount.builder().amount(heldTransactionAmount).authorizationId(authorizationId).build();
    }

    @Override
    public Amount withdrawalAuthorization(BigDecimal amount) {
        // TODO: Check user balance first, which will be a downstream call to Bank's server
        lock.lock();
        Optional<String> errorMessage = validateAmount(amount);
        if (errorMessage.isPresent()) {
            log.error("Validation failed: {}", errorMessage.get()); // In prod, this will move to a common exception handler
            throw new RuntimeException(errorMessage.get()); // In prod, this will be a business exception not a generic one
        }

        // Withdraw the amount
        UUID authId = UUID.randomUUID(); // Create a unique key for the transaction
        Map<BigInteger, BigInteger> userStorage = new HashMap<>();
        BigDecimal remainingAmount = new BigDecimal(amount.toString()); // Create deep-copy of original amount
        for (BigInteger denomination : atmStorage.getDenominations()) {
            BigInteger countOfAvailableDenomination = atmStorage.getDenominationCount(denomination);
            BigInteger heldDenomination = remainingAmount // Calculate amount of denominations held by the user
                    .divide(new BigDecimal(denomination), RoundingMode.FLOOR)
                    .toBigInteger();
            // If available denomination is less than expected denomination, then hold the available denomination
            heldDenomination = heldDenomination.min(countOfAvailableDenomination);
            if (heldDenomination.compareTo(BigInteger.ZERO) > 0) {
                // If expected held denomination is available, then actually freeze the amount
                // Update the ATM storage
                atmStorage.updateDenominationCount(denomination, countOfAvailableDenomination.subtract(heldDenomination));
                // Update the user authorization
                userStorage.put(denomination, heldDenomination);
                // Update the remaining amount
                remainingAmount = remainingAmount.subtract(new BigDecimal(denomination).multiply(new BigDecimal(heldDenomination)));
            }
        }
        if (!userStorage.isEmpty()) {
            userAuthorization.put(authId, userStorage);
        } else {
            log.warn("No denominations available for withdrawal");
        }

        lock.unlock();

        return Amount.builder().amount(userStorage).authorizationId(authId).build();
    }

    /**
     * Validates the user provided amount for Withdrawal
     *
     * @param amount Amount to be withdrawn
     * @return Optional error message if any
     * @implNote This method should return a proper Business error code in production, instead of error message
     */
    private Optional<String> validateAmount(BigDecimal amount) {
        String errorMessage = null;
        Optional<BigInteger> minDenomination = StreamSupport
                .stream(atmStorage.getDenominations().spliterator(), false)
                .min(Comparator.naturalOrder());
        if (minDenomination.isPresent()) {
            // Check whether the amount is greater than minimum denomination or not
            if (amount.compareTo(new BigDecimal(minDenomination.get())) < 0) {
                errorMessage = "Amount should be greater than " + minDenomination.get();
            }
            // Check whether the amount is a multiple of minimum denomination or not
            if (amount.remainder(new BigDecimal(minDenomination.get())).compareTo(BigDecimal.ZERO) != 0) {
                errorMessage = "Amount should be a multiple of " + minDenomination.get();
            }
        } else {
            errorMessage = "Balance unavailable";
        }

        return Optional.ofNullable(errorMessage);
    }
}
