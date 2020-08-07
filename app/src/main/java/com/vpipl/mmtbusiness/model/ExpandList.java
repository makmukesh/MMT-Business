package com.vpipl.mmtbusiness.model;

import java.util.List;


public class ExpandList {
    String name = "";
    String icon = "";
    String image = "";
    String id = "";
    String type = "";
    String IsComboPack = "";
    List<ExpandList> expandList;

    public ExpandList(String name, String icon, String image, String id, String type, String IsComboPack, List<ExpandList> expandList) {
        this.name = name;
        this.icon = icon;
        this.image = image;
        this.id = id;
        this.type = type;
        this.IsComboPack = IsComboPack;
        this.expandList = expandList;
    }

    public String getName() {
        return this.name;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getImage() {
        return this.image;
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public List<ExpandList> getExpandList() {
        return this.expandList;
    }
}
