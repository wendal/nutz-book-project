package net.wendal.nutzbook.core.bean.datatable;

/**
 * Created by wendal on 2015/12/20.
 */
public class DataTableSearch {
    protected String value;
    protected boolean regex;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }
}
