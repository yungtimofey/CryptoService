package com.example.cryptoservice.component;

import com.company.crypto.benaloh.algebra.prime.PrimeCheckerType;
import com.company.crypto.benaloh.algorithm.Benaloh;
import com.company.crypto.benaloh.algorithm.impl.BenalohImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class BenalohCreator {
    @Bean
    public Benaloh create() {
        // y = 224705143367096456854069233295853702432
        // r = 293
        // n = 151993793880119428705451239231315451118036209

        // f = 151993793880119428699944152239961553603340260
        // x = 82532460212552333508486689112692015569841483

        Benaloh.OpenKey openKey = new Benaloh.OpenKey(
                new BigInteger("224705143367096456854069233295853702432"),
                BigInteger.valueOf(293),
                new BigInteger("151993793880119428705451239231315451118036209")
        );

        Benaloh.PrivateKey privateKey = new Benaloh.PrivateKey(
                new BigInteger("151993793880119428699944152239961553603340260"),
                new BigInteger("82532460212552333508486689112692015569841483")
        );
        return new BenalohImpl(openKey, privateKey, PrimeCheckerType.MILLER_RABIN,
                0.9999999, 293);
    }

    public Benaloh createRandom() {
        Benaloh benaloh;
        byte[] nArray;
        do {
            benaloh = new BenalohImpl(PrimeCheckerType.MILLER_RABIN, 0.9999999, 293);
            nArray = benaloh.getOpenKey().getN().toByteArray();
        } while (nArray.length >= Byte.MAX_VALUE);
        return benaloh;
    }
}
