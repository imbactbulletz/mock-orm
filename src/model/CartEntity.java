package model;

import orm.annotations.Entity;
import orm.annotations.OneToMany;

import java.util.List;

@Entity()
public class CartEntity extends BasicEntity {

    @OneToMany(mappedBy = "cart")
    private List<ItemEntity> items;

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public void setId(int id) {
        super.setId(id);
    }
}
