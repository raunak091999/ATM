package com.deloitte.atm.demo.repository.impl;

import lombok.extern.slf4j.Slf4j;
import com.deloitte.atm.demo.repository.AtmStorage;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
public class MapBasedAtmStorage<T extends Comparable<T>> implements AtmStorage<T> {

    private final ConcurrentSkipListMap<T, T> atmStorage;
    private final T zeroValue;

    public MapBasedAtmStorage(Map<T, T> atmStorage, T zeroValue) {
        this.atmStorage = new ConcurrentSkipListMap<>(atmStorage);
        this.zeroValue = zeroValue;
    }

    /**
     * Returns a set of denominations available in the ATM, in sorted order
     *
     * @return Set of denominations
     */
    @Override
    public Collection<T> getDenominations() {
        return atmStorage.descendingKeySet();
    }

    /**
     * Returns the count of a particular denomination available in the ATM
     *
     * @param denomination Denomination to check
     * @return Count of the denomination
     */
    @Override
    public T getDenominationCount(T denomination) {
        return atmStorage.getOrDefault(denomination, zeroValue);
    }

    /**
     * Returns the count of each denomination available in the ATM
     *
     * @return Map of count for each denomination
     */
    @Override
    public Map<T, T> getAllDenominationCount() {
        return atmStorage.clone();
    }

    /**
     * Updates the count of a particular denomination in the ATM
     *
     * @param denomination Denomination to update
     * @param count        New Count to update
     * @return Existing count of the denomination
     */
    @Override
    public T updateDenominationCount(T denomination, T count) {
        if (count.compareTo(zeroValue) < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        if (count.compareTo(zeroValue) == 0) {
            T returnValue = atmStorage.remove(denomination);
            return returnValue != null ? returnValue : zeroValue;
        }
        T returnValue = atmStorage.put(denomination, count);
        return returnValue != null ? returnValue : zeroValue;
    }

    /**
     * Updates the count of each denomination in the ATM
     *
     * @param denominationCount New count of each denomination
     * @return Existing count of each denomination
     */
    @Override
    public Map<T, T> updateAllDenominationCount(Map<T, T> denominationCount) {
        Map<T, T> existingValues = atmStorage.clone();
        // Streams can be used, but streams are not feasible for other variable's modification like atmStorage
        denominationCount.forEach((denomination, count) -> {
            if (count.compareTo(zeroValue) < 0) {
                // Ideally should fail in prod, but for now just log and skip
                log.warn("Denomination {} count cannot be negative, skipping without fail", denomination);
            }
            if (count.compareTo(zeroValue) == 0) {
                atmStorage.remove(denomination);
            } else {
                atmStorage.put(denomination, count);
            }
        });
        return existingValues;
    }
}
