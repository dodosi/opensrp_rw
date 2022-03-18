package org.smartregister.anc.library.domain;

public class HomeItem {
    private String title;
    private int number;
    private  int background;

    public HomeItem(String title, int number, int background) {
        this.title = title;
        this.number = number;
        this.background = background;
    }
    public HomeItem() {
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
        this.background = background;
    }
}
