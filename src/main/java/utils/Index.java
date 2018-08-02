package utils;

import org.mongodb.morphia.annotations.Entity;

@Entity("index")
public class Index {
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Index(){}

    public Index(int index) {
        this.index = index;
    }
}
