package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OrderBuilder implements IBuilder {
    // required fields
    String orderId;
    String customerName;
    String phone;
    List<OrderItem> items;

    // optional fields with default values
    DeliveryType deliveryType = DeliveryType.PICKUP;
    String deliveryAddress = "";
    PaymentMethod paymentMethod = PaymentMethod.CASH;
    LocalDateTime scheduledTime = null;
    String couponCode = "";
    boolean giftWrap = false;
    boolean cutleryRequired = true;
    int loyaltyPointsToRedeem = 0;
    boolean rushOrder = false;
    String specialInstructions = "";

    public OrderBuilder(String orderId, String customerName, String phone, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.phone = phone;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public OrderBuilder setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
        return this;
    }

    public OrderBuilder setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public OrderBuilder setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderBuilder setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
        return this;
    }

    public OrderBuilder setCouponCode(String couponCode) {
        this.couponCode = couponCode;
        return this;
    }

    public OrderBuilder setGiftWrap(boolean giftWrap) {
        this.giftWrap = giftWrap;
        return this;
    }

    public OrderBuilder setCutleryRequired(boolean cutleryRequired) {
        this.cutleryRequired = cutleryRequired;
        return this;
    }

    public OrderBuilder setLoyaltyPointsToRedeem(int loyaltyPointsToRedeem) {
        this.loyaltyPointsToRedeem = loyaltyPointsToRedeem;
        return this;
    }

    public OrderBuilder setRushOrder(boolean rushOrder) {
        this.rushOrder = rushOrder;
        return this;
    }

    public OrderBuilder setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
        return this;
    }

    public Order build() {
        this.orderId = requireNonBlank(this.orderId, "Order id");
        this.customerName = requireNonBlank(this.customerName, "Customer name");
        this.phone = requireNonBlank(this.phone, "Phone");
 
        this.deliveryType = this.deliveryType != null ? this.deliveryType : DeliveryType.PICKUP;
        this.paymentMethod = this.paymentMethod != null ? this.paymentMethod : PaymentMethod.CASH;
        this.couponCode = this.couponCode != null ? this.couponCode.trim().toUpperCase() : "";
        this.loyaltyPointsToRedeem = Math.max(0, this.loyaltyPointsToRedeem);
        this.specialInstructions = this.specialInstructions != null ? this.specialInstructions.trim() : "";
 
        if (this.deliveryType == DeliveryType.DELIVERY) {
            this.deliveryAddress = requireNonBlank(this.deliveryAddress, "Delivery address");
        } else {
            this.deliveryAddress = this.deliveryAddress != null ? this.deliveryAddress.trim() : "";
        }

        Objects.requireNonNull(items, "Items cannot be null");
        if (this.items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        this.items = Collections.unmodifiableList(this.items);
 
        return new Order(this);
    }

    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return trimmed;
    }
}