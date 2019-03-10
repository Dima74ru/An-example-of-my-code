package com.karbox.carspeed;

import java.io.Serializable;

public class item_model implements Serializable {

    private boolean mode_model; // Тру - это свитч

    private String text_label;
    private String text_info;
    private String text_edit;
    private boolean flg_switch;

    public void setMode_model(Boolean mode_model)
    {
        this.mode_model = mode_model;
    }

    public Boolean getMode_model(){ return mode_model;}

    public void setText_label(String text_label) {
        this.text_label = text_label;
    }

    public String getText_label()
    {
        return text_label;
    }

    public void setText_info(String text_info) {
        this.text_info = text_info;
    }

    public String getText_info()
    {
        return text_info;
    }

    public void setText_edit(String text_edit) {this.text_edit = text_edit;}

    public String getText_edit() {return text_edit;}

    public void setFlg_switch(Boolean flg_switch) {
        this.flg_switch = flg_switch;
    }

    public Boolean getFlg_switch()
    {
        return flg_switch;
    }

    public item_model(Boolean mode_model,String text_label, String text_info, Boolean flg_switch)
    {
        this.mode_model = mode_model;
        this.text_label = text_label;
        this.text_info = text_info;
        this.flg_switch = flg_switch;
    }

    public item_model(Boolean mode_model,String text_label, String text_info, String text_edit)
    {
        this.mode_model = mode_model;
        this.text_label = text_label;
        this.text_info = text_info;
        this.text_edit = text_edit;
    }
}
