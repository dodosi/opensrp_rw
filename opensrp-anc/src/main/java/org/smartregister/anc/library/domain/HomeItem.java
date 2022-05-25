package org.smartregister.anc.library.domain;

public class HomeItem {
    private String title;
    private int number;
    private  int background;
    private  String type;
    private String startDate;
    private String endDate;

    public HomeItem(String title, int number, int background) {
        this.title = title;
        this.number = number;
        this.background = background;
    }
    public HomeItem() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background=background;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "HomeItem{" +
                "title='" + title + '\'' +
                ", number=" + number +
                ", background=" + background +
                ", type='" + type + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
