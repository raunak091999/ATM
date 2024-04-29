package com.deloitte.atm.demo;

import com.deloitte.atm.demo.core.AtmService;
import com.deloitte.atm.demo.core.impl.MapBasedAtmService;
import com.deloitte.atm.demo.dto.Amount;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.math.BigDecimal;

@Slf4j
public class Main {

    private static final AtmService atmService = MapBasedAtmService.getInstance();

    public static void main(String[] args) {
        log.info("Started execution");
        Thread transaction1 = new Thread(() -> executeTransaction("1", 100));
        Thread transaction2 = new Thread(() -> executeTransaction("2", 10200));
        Thread transaction3 = new Thread(() -> log.info("ATM Balance is {}", atmService.checkBalance()));
        transaction1.start();
        transaction2.start();
        transaction3.start();
        try {
            transaction1.join();
            transaction2.join();
            transaction3.join();
        } catch (InterruptedException e) {
            log.error("Error occurred while joining threads", e);
        }
        Thread transaction4 = new Thread(() -> log.info("ATM Balance after transactions is {}", atmService.checkBalance()));
        transaction4.start();
        log.info("Ended execution");
    }

    private static void executeTransaction(String transactionId, int amount) {
        MDC.put("transactionId", transactionId);
        log.info("Withdrawal Authorization for {}", amount);
        Amount withdrawal = atmService.withdrawalAuthorization(new BigDecimal(amount));
        log.info("Withdrawal Authorization for {}: {}", amount, withdrawal);
    }
}