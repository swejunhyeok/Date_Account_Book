package kr.co.kangnam.date;

/**
 * Created by aks56 on 2018-05-08.
 */

public class MyItem2 {
    private int money;
    private String sex;
    private int other; // 0 spend 1 save
    private int num;
    public MyItem2(int money, String sex, int other, int num){
        this.money = money;
        this.sex = sex;
        this.other = other;
        this.num = num;
    }
    public int getMoney(){
        return money;
    }
    public String getSex(){
        return sex;
    }
    public int getOther(){ return other; }
    public int getNum(){ return num;}
    public void setMoney(int moeny){
        this.money = moeny;
    }
    public void setSex(String sex){
        this.sex = sex;
    }
    public void setOther(int ohter){
        this.other = ohter;
    }
    public void setNum(int num){ this.num = num;}
}
