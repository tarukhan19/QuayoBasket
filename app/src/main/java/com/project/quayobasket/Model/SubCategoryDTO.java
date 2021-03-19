package com.project.quayobasket.Model;

public class SubCategoryDTO {
    String i_name,dept,SUD_dept,outlet_id,Discount,Item_Image,addtocart,Item_Descp,isvegnong,item_id;
    double Rate,taxpercent,taxamount,amountplustax,strikeprice;
    int quantity,isDiscount;

    public int getIsDiscount() {
        return isDiscount;
    }

    public void setIsDiscount(int isDiscount) {
        this.isDiscount = isDiscount;
    }

    public double getStrikeprice() {
        return strikeprice;
    }

    public void setStrikeprice(double strikeprice) {
        this.strikeprice = strikeprice;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
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

    public String getIsvegnong() {
        return isvegnong;
    }

    public void setIsvegnong(String isvegnong) {
        this.isvegnong = isvegnong;
    }

    public String getItem_Descp() {
        return Item_Descp;
    }

    public void setItem_Descp(String item_Descp) {
        Item_Descp = item_Descp;
    }

    public String getAddtocart() {
        return addtocart;
    }

    public void setAddtocart(String addtocart) {
        this.addtocart = addtocart;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

//    public boolean isSelected() {
//        return isSelected;
//    }
//
//    public void setSelected(boolean selected) {
//        isSelected = selected;
//    }

    public String getItem_Image() {
        return Item_Image;
    }

    public void setItem_Image(String item_Image) {
        Item_Image = item_Image;
    }

    public String getI_name() {
        return i_name;
    }

    public void setI_name(String i_name) {
        this.i_name = i_name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getSUD_dept() {
        return SUD_dept;
    }

    public void setSUD_dept(String SUD_dept) {
        this.SUD_dept = SUD_dept;
    }

    public double getRate() {
        return Rate;
    }

    public void setRate(double rate) {
        Rate = rate;
    }

    public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }


}
