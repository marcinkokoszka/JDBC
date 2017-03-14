package pl.marcinkokoszka.model;

/**
 * Created by kokoseq on 14.03.2017.
 */
public class Artist {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return name + ", id: " + id;
    }
}
