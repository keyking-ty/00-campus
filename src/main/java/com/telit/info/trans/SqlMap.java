package com.telit.info.trans;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SqlMap extends HashMap<String,Object> {

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Integer getInt(String key){
        Object value = this.get(key);
        if (value != null){
            return Integer.parseInt(value.toString());
        }
        return 0;
    }

    public Long getLong(String key){
        Object value = this.get(key);
        if (value != null){
            return Long.parseLong(value.toString());
        }
        return 0L;
    }

    public Float getFloat(String key){
        Object value = this.get(key);
        if (value != null){
            return Float.parseFloat(value.toString());
        }
        return 0.0f;
    }

    public String getString(String key){
        Object value = this.get(key);
        if (value != null){
            return value.toString();
        }
        return null;
    }

    public Date getDate(String key){
        Object value = this.get(key);
        if (value != null){
            try {
                String str = value.toString().substring(0,19);
                return dateFormat.parse(str);
            }catch (Exception e){ }
        }
        return null;
    }

    public Timestamp getTime(String key){
        Object value = this.get(key);
        if (value != null){
            return (Timestamp)value;
        }
        return null;
    }
}
