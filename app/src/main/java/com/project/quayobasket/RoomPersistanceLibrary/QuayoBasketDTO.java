package com.project.quayobasket.RoomPersistanceLibrary;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class QuayoBasketDTO implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "customerId" )
    private String customerId;
    @ColumnInfo(name = "productid" )
    private String productId;
    @ColumnInfo(name = "productname")
    private String productName;
    @ColumnInfo(name = "productMRP")
    private double productMRP;
    @ColumnInfo(name = "producDiscountPercent")
    private double producDiscount;
    @ColumnInfo(name = "producDiscountAmount")
    private double producDiscountAmount;
    @ColumnInfo(name = "productShippingCharges")
    private double productShippingCharges;
    @ColumnInfo(name = "quantity")
    private int quantity;
    @ColumnInfo(name = "productImage")
    private String productImage;
    @ColumnInfo(name = "addtocart")
    private String addtocart;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "isvegnonveg")
    private String isvegnonveg;
    @ColumnInfo(name = "taxpercent")
    private double taxpercent;
    @ColumnInfo(name = "taxamount")
    private double taxamount;
    @ColumnInfo(name = "amountwithtax")
    private double amountplustax;
    @ColumnInfo(name = "strikeprice")
    private double strikeprice;
    @ColumnInfo(name = "IsDiscount")
    private int IsDiscount;

    public int getIsDiscount() {
        return IsDiscount;
    }

    public void setIsDiscount(int isDiscount) {
        IsDiscount = isDiscount;
    }

    public double getStrikeprice() {
        return strikeprice;
    }

    public void setStrikeprice(double strikeprice) {
        this.strikeprice = strikeprice;
    }

    public double getTaxpercent() {
        return taxpercent;
    }

    public void setTaxpercent(double taxpercent) {
        this.taxpercent = taxpercent;
    }

    public double getTaxamount() {
        return taxamount;
    }

    public void setTaxamount(double taxamount) {
        this.taxamount = taxamount;
    }

    public double getAmountplustax() {
        return amountplustax;
    }

    public void setAmountplustax(double amountplustax) {
        this.amountplustax = amountplustax;
    }

    public String getIsvegnonveg() {
        return isvegnonveg;
    }

    public void setIsvegnonveg(String isvegnonveg) {
        this.isvegnonveg = isvegnonveg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddtocart() {
        return addtocart;
    }

    public void setAddtocart(String addtocart) {
        this.addtocart = addtocart;
    }

    public double getProductMRP() {
        return productMRP;
    }

    public void setProductMRP(double productMRP) {
        this.productMRP = productMRP;
    }



    public double getProducDiscount() {
        return producDiscount;
    }

    public void setProducDiscount(double producDiscountPercent) {
        this.producDiscount = producDiscountPercent;
    }

    public double getProducDiscountAmount() {
        return producDiscountAmount;
    }

    public void setProducDiscountAmount(double producDiscountAmount) {
        this.producDiscountAmount = producDiscountAmount;
    }

    public double getProductShippingCharges() {
        return productShippingCharges;
    }

    public void setProductShippingCharges(double productShippingCharges) {
        this.productShippingCharges = productShippingCharges;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }


}
