package net.wendal.nutzbook.core.bean.datatable;

/**
 * Created by wendal on 2015/12/20.
 */
public class DataTableOrder {
    protected int column;
    protected String dir;

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
