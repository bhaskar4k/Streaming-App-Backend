package com.app.dashboard.model;

import java.util.List;

public class Layout {
    private Long id;
    private String route_name;
    private String menu_name;
    private String menu_name_id;
    private String menu_icon;
    private int parent_id;
    private int sequence;
    private List<Layout> child;

    public Layout(){

    }

    public Layout(Long id, String route_name, String menu_name, String menu_name_id, String menu_icon, int parent_id, int sequence) {
        this.id = id;
        this.route_name = route_name;
        this.menu_name = menu_name;
        this.menu_name_id = menu_name_id;
        this.menu_icon = menu_icon;
        this.parent_id = parent_id;
        this.sequence = sequence;
    }

    public Layout(Long id, String route_name, String menu_name, String menu_name_id, String menu_icon, int parent_id, int sequence, List<Layout> child) {
        this.id = id;
        this.route_name = route_name;
        this.menu_name = menu_name;
        this.menu_name_id = menu_name_id;
        this.menu_icon = menu_icon;
        this.parent_id = parent_id;
        this.sequence = sequence;
        this.child = child;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getMenu_name_id() {
        return menu_name_id;
    }

    public void setMenu_name_id(String menu_name_id) {
        this.menu_name_id = menu_name_id;
    }

    public String getMenu_icon() {
        return menu_icon;
    }

    public void setMenu_icon(String menu_icon) {
        this.menu_icon = menu_icon;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public List<Layout> getChild() {
        return child;
    }

    public void setChild(List<Layout> child) {
        this.child = child;
    }
}
