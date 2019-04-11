package kr.co.kangnam.date;

/**
 * Created by aks56 on 2018-05-07.
 */

public class MyItem {
    private String sex = null;
    private String num = null;
    public MyItem(String sex, String num){
        this.sex = sex;
        this.num = num;
    }
    public String getSex(){
        return sex;
    }
    public String getNum(){
        return num;
    }
    public void setSex(String sex){
        this.sex = sex;
    }
    public void setNum(String num){
        this.num = num;
    }
}
