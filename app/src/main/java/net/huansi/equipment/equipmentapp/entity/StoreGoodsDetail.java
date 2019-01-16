package net.huansi.equipment.equipmentapp.entity;

public class StoreGoodsDetail extends WsData{
    public String STOCKPOSITION_CURRENT="";
    public String BOXBARCODE="";
    public String QUANTITY="";
    public String CHTNAME="";

    public StoreGoodsDetail(String STOCKPOSITION_CURRENT, String BOXBARCODE, String QUANTITY, String CHTNAME) {
        this.STOCKPOSITION_CURRENT = STOCKPOSITION_CURRENT;
        this.BOXBARCODE = BOXBARCODE;
        this.QUANTITY = QUANTITY;
        this.CHTNAME = CHTNAME;
    }
}
