package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserProfile getUserProfileSample1() {
        return new UserProfile().id(1L).fullName("fullName1").timezone("timezone1").phone("phone1").status("status1");
    }

    public static UserProfile getUserProfileSample2() {
        return new UserProfile().id(2L).fullName("fullName2").timezone("timezone2").phone("phone2").status("status2");
    }

    public static UserProfile getUserProfileRandomSampleGenerator() {
        return new UserProfile()
            .id(longCount.incrementAndGet())
            .fullName(UUID.randomUUID().toString())
            .timezone(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString());
    }
}
