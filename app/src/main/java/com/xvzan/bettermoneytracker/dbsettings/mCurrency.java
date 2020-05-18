package com.xvzan.bettermoneytracker.dbsettings;

import io.realm.RealmObject;

public class mCurrency extends RealmObject {
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
