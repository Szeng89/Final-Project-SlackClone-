package rocks.zipcode.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ChannelTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Channel getChannelSample1() {
        return new Channel().id(1L).name("name1").description("description1");
    }

    public static Channel getChannelSample2() {
        return new Channel().id(2L).name("name2").description("description2");
    }

    public static Channel getChannelRandomSampleGenerator() {
        return new Channel().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
