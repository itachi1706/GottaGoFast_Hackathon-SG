package com.itachi1706.hackathonsg.Objects;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.Objects
 */
public class Barcode {

    public String format, contents;
    public String stringConcatValue;

    public Barcode(){}

    public Barcode(String format, String contents){
        this.format = format;
        this.contents = contents;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setToString(String toString){
        this.stringConcatValue = toString;
    }

    public String toString(){
        return this.stringConcatValue;
    }
}
