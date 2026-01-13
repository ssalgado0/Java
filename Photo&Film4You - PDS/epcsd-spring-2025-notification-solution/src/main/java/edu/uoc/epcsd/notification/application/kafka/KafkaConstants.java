package edu.uoc.epcsd.notification.application.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class KafkaConstants {

    // misc
    public static final String SEPARATOR = ".";

    // topic items
    public static final String PRODUCT_TOPIC = "product";

    // topic items
    public static final String DIGITAL_ITEM_TOPIC = "digitalItem";

    // commands
    public static final String UNIT_AVAILABLE = "unit_available";

}
