package com.deloitte.atm.demo.dto;

public enum TransactionClearType {
    /**
     * Clearing the transaction, i.e. the amount is withdrawn
     */
    CLEARING,
    /**
     * Reversing the transaction, i.e. the amount is not withdrawn and returned to the ATM
     */
    REVERSAL
}
