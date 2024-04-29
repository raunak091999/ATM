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


}