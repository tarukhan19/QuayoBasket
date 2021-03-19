package com.project.quayobasket.Model;

public class OrderHistoryDTO {
    String Srno,Id,Order_No,Order_Date,Order_Aount,Total_Tax,Status,orderItem,orderItemCode;

    public String getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(String orderItem) {
        this.orderItem = orderItem;
    }

    public String getOrderItemCode() {
        return orderItemCode;
    }

    public void setOrderItemCode(String orderItemCode) {
        this.orderItemCode = orderItemCode;
    }

    public String getSrno() {
        return Srno;
    }

    public void setSrno(String srno) {
        Srno = srno;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getOrder_No() {
        return Order_No;
    }

    public void setOrder_No(String order_No) {
        Order_No = order_No;
    }

    public String getOrder_Date() {
        return Order_Date;
    }

    public void setOrder_Date(String order_Date) {
        Order_Date = order_Date;
    }

    public String getOrder_Aount() {
        return Order_Aount;
    }

    public void setOrder_Aount(String order_Aount) {
        Order_Aount = order_Aount;
    }

    public String getTotal_Tax() {
        return Total_Tax;
    }

    public void setTotal_Tax(String total_Tax) {
        Total_Tax = total_Tax;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
