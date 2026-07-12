package service;

import model.IBuilder;
import model.DeliveryType;
import model.OrderBuilder;
import model.PaymentMethod;

import java.time.LocalDateTime;

public class OrderDirector {

    public OrderBuilder constructDeliveryOrder(OrderBuilder builder,
                                         String address,
                                         String couponCode,
                                         boolean rushOrder,
                                         String specialInstructions) {
        return builder
                .setDeliveryType(DeliveryType.DELIVERY)
                .setDeliveryAddress(address)
                .setCouponCode(couponCode)
                .setRushOrder(rushOrder)
                .setSpecialInstructions(specialInstructions);
    }

    public OrderBuilder constructPickupOrder(OrderBuilder builder) {
        return builder.setDeliveryType(DeliveryType.PICKUP);
    }

    public OrderBuilder constructScheduledGiftOrder(OrderBuilder builder,
                                              String address,
                                              LocalDateTime scheduledTime) {
        return builder
                .setDeliveryType(DeliveryType.DELIVERY)
                .setDeliveryAddress(address)
                .setPaymentMethod(PaymentMethod.CARD)
                .setScheduledTime(scheduledTime)
                .setCouponCode("WELCOME10")
                .setGiftWrap(true)
                .setCutleryRequired(false)
                .setLoyaltyPointsToRedeem(25)
                .setSpecialInstructions("Please call before delivery");
    }

    public OrderBuilder constructSampleFamilyOrder(OrderBuilder builder) {
        return builder
                .setDeliveryType(DeliveryType.DELIVERY)
                .setDeliveryAddress("House 25, Road 4, Dhanmondi")
                .setPaymentMethod(PaymentMethod.MOBILE_BANKING)
                .setCouponCode("FAMILY15")
                .setCutleryRequired(true)
                .setLoyaltyPointsToRedeem(50)
                .setRushOrder(true)
                .setSpecialInstructions("Deliver together");
    }
}