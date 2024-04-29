package com.deloitte.atm.demo.core;

import com.deloitte.atm.demo.dto.Amount;
import com.deloitte.atm.demo.dto.TransactionClearType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Interface for ATM
 * Could be implemented by several storage mechanisms like Database, Map, etc.
 */
public interface AtmService {

    /**
     * Check the balance of the ATM
     *
     * @return Amount present in the ATM
     */
    public Amount checkBalance();

    /**
     * Hold the amount from the ATM
     *
     * @param authorizationId Unique key for the help amount
     *                        to be withdrawn
     * @param clearType       Type of transaction clearing
     * @return Unique key for the help amount
     */
    public Amount withdrawalClearing(UUID authorizationId, TransactionClearType clearType);

    /**
     * Withdraw the amount from the ATM
     *
     * @param amount to be withdrawn
     * @return Amount held by the user
     */
    public Amount withdrawalAuthorization(BigDecimal amount);
}
