package com.project.quayobasket.Model;

public class OrderHistoryDetailDTO {
    String Bill_No,Bill_Date,I_Code,I_Name,Qty,Bill_Amount,Tax,Payment_Mode;

    public String getBill_No() {
        return Bill_No;
    }

    public void setBill_No(String bill_No) {
        Bill_No = bill_No;
    }

    public String getBill_Date() {
        return Bill_Date;
    }

    public void setBill_Date(String bill_Date) {
        Bill_Date = bill_Date;
    }

    public String getI_Code() {
        return I_Code;
    }

    public void setI_Code(String i_Code) {
        I_Code = i_Code;
    }

    public String getI_Name() {
        return I_Name;
    }

    public void setI_Name(String i_Name) {
        I_Name = i_Name;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getBill_Amount() {
        return Bill_Amount;
    }

    public void setBill_Amount(String bill_Amount) {
        Bill_Amount = bill_Amount;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getPayment_Mode() {
        return Payment_Mode;
    }

    public void setPayment_Mode(String payment_Mode) {
        Payment_Mode = payment_Mode;
    }
}
