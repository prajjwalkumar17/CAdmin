package com.thundersharp.cadmin.core.currency;


public class Model {

    private String countryCode;
    private String currencyCode;

    public Model(String countryCode, String currencyCode) {
        this.countryCode = countryCode;
        this.currencyCode = currencyCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
