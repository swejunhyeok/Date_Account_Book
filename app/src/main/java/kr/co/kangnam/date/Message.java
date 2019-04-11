package kr.co.kangnam.date;

/**
 * Created by aks56 on 2018-05-09.
 */

public class Message {

    String other;
    String money;
    String year;
    String month;
    String date;

    public Message() {
    }

    public Message(String other, String money, String year, String month, String date) {
        this.other = other;
        this.money = money;
        this.year = year;
        this.month = month;
        this.date = date;
    }

    public String getOther() {
        return other;
    }

    public String getMoney() {
        return money;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDate() {
        return date;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDate(String date) {
        this.date = date;
    }
}


