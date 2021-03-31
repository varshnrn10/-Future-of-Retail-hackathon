package com.example.barcode.billing;

public class ProductHelperClass {

    String pcode,pcost,pname;

    public ProductHelperClass() {
    }

    public ProductHelperClass(String pcode, String pcost, String pname) {
        this.pcode = pcode;
        this.pcost = pcost;
        this.pname = pname;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getPcost() {
        return pcost;
    }

    public void setPcost(String pcost) {
        this.pcost = pcost;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
}
