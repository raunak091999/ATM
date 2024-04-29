package com.deloitte.atm.demo.repository;

import java.util.Collection;
import java.util.Map;

public interface AtmStorage<T> {

    /**
     * Returns a set of denominations available in the ATM, in sorted order
     *
     * @return Set of denominations
     */
    Collection<T> getDenominations();

    /**
     * Returns the count of a particular denomination available in the ATM
     *
     * @param denomination Denomination to check
     * @return Count of the denomination
     */
    T getDenominationCount(T denomination);

    /**
     * Returns the count of each denomination available in the ATM
     *
     * @return Map of count for each denomination
     */
    Map<T, T> getAllDenominationCount();

    /**
     * Updates the count of a particular denomination in the ATM
     *
     * @param denomination Denomination to update
     * @param count        New Count to update
     * @return Existing count of the denomination
     */
    T updateDenominationCount(T denomination, T count);

    /**
     * Updates the count of each denomination in the ATM
     *
     * @param denominationCount New count of each denomination
     * @return Existing count of each denomination
     */
    Map<T, T> updateAllDenominationCount(Map<T, T> denominationCount);

}
