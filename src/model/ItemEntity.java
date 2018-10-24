package model;

import orm.annotations.Entity;
import orm.annotations.JoinColumn;
import orm.annotations.ManyToOne;


@Entity
public class ItemEntity extends BasicEntity {

    @ManyToOne
    @JoinColumn(name = "ID")
    private CartEntity cart;


    public CartEntity getCart() {
        return cart;
    }

    public void setCart(CartEntity cart) {
        this.cart = cart;
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
