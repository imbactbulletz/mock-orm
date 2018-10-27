package model;

import orm.annotations.Column;
import orm.annotations.GeneratedValue;
import orm.annotations.ID;
import orm.annotations.MappedSuperclass;

@MappedSuperclass
public class BasicEntity {

    @ID
    @GeneratedValue
    @Column(name="ID")
    private int id;



    // getters and setters

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj == null)
            return false;

        if(this.getClass() != obj.getClass())
            return false;

        BasicEntity other = (BasicEntity)obj;

        if(id != other.id)
            return false;

        return true;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
