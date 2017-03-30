package jp.live2d.sample;

/**
 * Created by fuyuk on 16/2/15.
 */
public class SetGetManager {

    long id;
    String date;
    String title;
    String memo;

    public long getID(){
        return id;
    }

    public void setID(long id){
        this.id = id;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getMemo(){
        return memo;
    }

    public void setMemo(String memo){
        this.memo = memo;
    }
}

