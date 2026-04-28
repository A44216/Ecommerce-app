package com.example.ecommerceapp.data.model.response.admin.management.shop;

public class AdminShopAutocompleteResponse {
    private Integer id;
    private String label;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label != null ? label : "";
    }
}
