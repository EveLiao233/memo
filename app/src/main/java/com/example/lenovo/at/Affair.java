package com.example.lenovo.at;

/**
 * Created by lenovo on 2016/12/13.
 */
public class Affair {
    private String thing;
    private int process;
    private String start_time;
    private String end_time;
    private int category;
    private int icon;

    public Affair(){}
    public Affair(String thing, int process, String start_time, String end_time, int category, int icon) {
        this.thing = thing;
        this.process = process;
        this.start_time =start_time;
        this.end_time = end_time;
        this.category = category;
        this.icon = icon;
    }

    public void setThing(String thing) {
        this.thing = thing;
    }
    public void setProcess(int process) {
        this.process = process;
    }
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
    public void setCategory(int category) { this.category = category; }
    public void setIcon(int icon) { this.icon = icon; }

    public String getThing() {
        return thing;
    }
    public int getProcess() {
        return process;
    }
    public String getStart_time() {
        return start_time;
    }
    public String getEnd_time() {
        return end_time;
    }
    public int getCategory() { return category; }
    public int getIcon() {return icon;}
}
