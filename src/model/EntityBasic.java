package model;

import orm.annotations.Column;
import orm.annotations.GeneratedValue;
import orm.annotations.ID;
import orm.annotations.MappedSuperclass;

@MappedSuperclass
public class EntityBasic {

    @ID
    @GeneratedValue
    @Column(name="ID")
    private int id;

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj == null)
            return false;

        if(this.getClass() != obj.getClass())
            return false;

        EntityBasic other = (EntityBasic)obj;

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
