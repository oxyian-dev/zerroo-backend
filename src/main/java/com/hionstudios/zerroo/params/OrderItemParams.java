package com.hionstudios.zerroo.params;

public class OrderItemParams {
    public String name;
    public String sku;
    public String hsn;
    public int quantity;
    public double price;
    public double basic;
    public double gstValue;
    public double gstPercent;
    public double discountValue;
    public double discountPercent;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return this.sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getHsn() {
        return this.hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getBasic() {
        return this.basic;
    }

    public void setBasic(double basic) {
        this.basic = basic;
    }

    public double getGstValue() {
        return this.gstValue;
    }

    public void setGstValue(double gstValue) {
        this.gstValue = gstValue;
    }

    public double getGstPercent() {
        return this.gstPercent;
    }

    public void setGstPercent(double gstPercent) {
        this.gstPercent = gstPercent;
    }

    public double getDiscountValue() {
        return this.discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public double getDiscountPercent() {
        return this.discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

}
