package kitchenpos.menu.domain;

import kitchenpos.common.valueobject.Quantity;
import kitchenpos.product.domain.Product;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class MenuProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Embedded
    private Quantity quantity;

    protected MenuProduct() {
    }

    public MenuProduct(Long seq, Menu menu, Product product, long quantity) {
        this.seq = seq;
        this.menu = menu;
        this.product = product;
        this.quantity = Quantity.of(quantity);
    }

    public static MenuProduct of(Product product, long quantity) {
        return new MenuProduct(null, null, product, quantity);
    }

    public static MenuProduct of(long quantity) {
        return new MenuProduct(null, null, null, quantity);
    }

    public Long getSeq() {
        return seq;
    }

    public Menu getMenu() {
        return menu;
    }

    public Product getProduct() {
        return product;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void registerMenu(Menu menu) {
        this.menu = menu;
    }

    public BigDecimal getCalculatedPrice() {
        return product.getPrice().calculateByQuantity(quantity);
    }
}
