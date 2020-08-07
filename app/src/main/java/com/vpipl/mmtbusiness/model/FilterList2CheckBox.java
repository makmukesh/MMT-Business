package com.vpipl.mmtbusiness.model;


public class FilterList2CheckBox {
    String name = "";
    String id = "";
    boolean selected = false;

    public FilterList2CheckBox(String name, String id, boolean selected) {
        super();
        this.name = name;
        this.id = id;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}