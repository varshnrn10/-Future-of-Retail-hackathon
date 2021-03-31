package com.example.barcode.packing;

public class BillingHelperClass {
    String Cash;
    String Card;
    String Upi;

    public String getTotSale() {
        return TotSale;
    }

    public void setTotSale(String totSale) {
        TotSale = totSale;
    }

    String TotSale;

    public BillingHelperClass(String cash, String card, String upi,String TotSale) {
        Cash = cash;
        Card = card;
        Upi = upi;
        this.TotSale = TotSale;
    }

    public String getCash() {
        return Cash;
    }

    public void setCash(String cash) {
        Cash = cash;
    }

    public String getCard() {
        return Card;
    }

    public void setCard(String card) {
        Card = card;
    }

    public String getUpi() {
        return Upi;
    }

    public void setUpi(String upi) {
        Upi = upi;
    }
}
