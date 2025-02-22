package com.app.dashboard.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_layout_menu")
public class TLayoutMenu {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String route_name;
    private String menu_name;
    private String menu_name_id;
    private String menu_icon;
    private int parent_id;
    private int sequence;
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime trans_datetime = LocalDateTime.now();

    public TLayoutMenu(Long id, String route_name, String menu_name, String menu_name_id, String menu_icon, int parent_id, int sequence, LocalDateTime trans_datetime) {
        this.id = id;
        this.route_name = route_name;
        this.menu_name = menu_name;
        this.menu_name_id = menu_name_id;
        this.menu_icon = menu_icon;
        this.parent_id = parent_id;
        this.sequence = sequence;
        this.trans_datetime = trans_datetime;
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

    public LocalDateTime getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(LocalDateTime trans_datetime) {
        this.trans_datetime = trans_datetime;
    }
}
