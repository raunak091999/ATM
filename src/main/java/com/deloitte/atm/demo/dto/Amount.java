package com.deloitte.atm.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class Amount {
    private Map<BigInteger, BigInteger> amount;
    private UUID authorizationId;
    private String errorMessage;
}
