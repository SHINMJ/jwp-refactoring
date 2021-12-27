package kitchenpos.product.domain;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import kitchenpos.common.domain.BaseEntity;
import kitchenpos.common.domain.MustHaveName;
import kitchenpos.common.domain.Price;
import kitchenpos.common.domain.Quantity;

@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private MustHaveName name;

    @Embedded
    private Price price;

    public Product() {
    }

    private Product(String name, Integer price) {
        this.name = MustHaveName.valueOf(name);
        this.price = Price.fromInteger(price);
    }

    public static Product of(String name, Integer price) {
        return new Product(name, price);
    }

    public Price multiplyQuantity(Quantity quantity) {
        return price.multiply(quantity.toLong());
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name.toString();
    }

    public BigDecimal getPrice() {
        return price.toBigDecimal();
    }

    public boolean equalName(String name) {
        return this.name.equals(MustHaveName.valueOf(name));
    }

    public boolean equalPrice(BigDecimal price) {
        return this.price.equals(Price.valueOf(price));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
