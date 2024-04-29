package com.deloitte.atm.demo.repository.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import static org.junit.jupiter.api.Assertions.*;

class MapBasedAtmStorageTest {

    @Test
    void getDenominations_whenStorageIsEmpty_thenReturnEmpty() {

        final ConcurrentSkipListMap<Integer, Integer> atmStorage = new ConcurrentSkipListMap<>();
        final MapBasedAtmStorage<Integer> mapBasedAtmStorage = new MapBasedAtmStorage<>(atmStorage, 0);
        assertTrue(mapBasedAtmStorage.getDenominations().isEmpty());
    }

    @Test
    void getDenominations_whenStorageIsNotEmpty_thenReturnDenominationsInSortedOrder() {
        Map<Integer, Integer> atmStorage = Map.of(
                2000, 10,
                500, 10,
                200, 10,
                100, 10,
                50, 10,
                20, 10,
                10, 10
        );

        final MapBasedAtmStorage<Integer> mapBasedAtmStorage = new MapBasedAtmStorage<>(atmStorage, 0);
        assertArrayEquals(new Integer[]{2000, 500, 200, 100, 50, 20, 10}, mapBasedAtmStorage.getDenominations().toArray());
    }

    @Test
    void getDenominationCount_whenEmpty_thenReturnZero() {
        final ConcurrentSkipListMap<Integer, Integer> atmStorage = new ConcurrentSkipListMap<>();
        final MapBasedAtmStorage<Integer> mapBasedAtmStorage = new MapBasedAtmStorage<>(atmStorage, 0);
        assertEquals(0, (int) mapBasedAtmStorage.getDenominationCount(2000));
    }

    @Test
    void getDenominationCount_whenDenominationIsPresent_thenReturnCount() {
        Map<Integer, Integer> atmStorage = Map.of(
                2000, 10
        );

        final MapBasedAtmStorage<Integer> mapBasedAtmStorage = new MapBasedAtmStorage<>(atmStorage, 0);
        assertEquals(10, mapBasedAtmStorage.getDenominationCount(2000).intValue());
    }

    @Test
    void getAllDenominationCount_whenEmpty_thenReturnEmpty() {
        final ConcurrentSkipListMap<Integer, Integer> atmStorage = new ConcurrentSkipListMap<>();
        final MapBasedAtmStorage<Integer> mapBasedAtmStorage = new MapBasedAtmStorage<>(atmStorage, 0);
        assertTrue(mapBasedAtmStorage.getAllDenominationCount().isEmpty());
    }

    @Test
    void getAllDenominationCount_whenNotEmpty_thenReturnAllDenominations() {
        Map<Integer, Integer> atmStorage = Map.of(
                2000, 10,
                500, 10,
                200, 10,
                100, 10,
                50, 10,
                20, 10,
                10, 10
        );

        final MapBasedAtmStorage<Integer> mapBasedAtmStorage = new MapBasedAtmStorage<>(atmStorage, 0);
        assertEquals(atmStorage, mapBasedAtmStorage.getAllDenominationCount());
    }

    @Test
    void updateDenominationCount_whenEmpty_thenAdd() {
        final ConcurrentSkipListMap<Integer, Integer> atmStorage = new ConcurrentSkipListMap<>();
        final MapBasedAtmStorage<Integer> mapBasedAtmStorage = new MapBasedAtmStorage<>(atmStorage, 0);
        // When empty, even Optional.empty() can be returned but here, zero is also feasible
        assertEquals(0, (int) mapBasedAtmStorage.updateDenominationCount(2000, 10));
        assertEquals(10, (int) mapBasedAtmStorage.getDenominationCount(2000));
    }

    @Test
    void updateAllDenominationCount() {
        Map<Integer, Integer> atmStorage = Map.of(
                2000, 10,
                500, 10,
                200, 10,
                100, 10,
                50, 10,
                20, 10,
                10, 10
        );

        final MapBasedAtmStorage<Integer> mapBasedAtmStorage = new MapBasedAtmStorage<>(atmStorage, 0);
        assertEquals(atmStorage, mapBasedAtmStorage.updateAllDenominationCount(Map.of(2000, 20, 10, 0)));
        Map<Integer, Integer> updatedBalance = mapBasedAtmStorage.getAllDenominationCount();
        Assertions.assertEquals(20, updatedBalance.get(2000));
        Assertions.assertNull(updatedBalance.get(10));
    }
}