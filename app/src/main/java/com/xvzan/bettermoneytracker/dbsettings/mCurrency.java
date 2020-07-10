package com.xvzan.bettermoneytracker.dbsettings;

import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class mCurrency extends RealmObject {
    @PrimaryKey
    private ObjectId _id = new ObjectId();

    private String symbol;
    private String name;
    private String pattern;
    private int fDigits;
    private int order;

    public void setALL(String s_Symbol, String s_Name, String s_Pattern, int i_Fractional_Digits) {
        symbol = s_Symbol;
        name = s_Name;
        pattern = s_Pattern;
        fDigits = i_Fractional_Digits;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public int getFractionalDigits() {
        return fDigits;
    }

    public int getOrder() {
        return order;
    }
}
