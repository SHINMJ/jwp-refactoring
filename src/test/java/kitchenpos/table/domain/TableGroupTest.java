package kitchenpos.table.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import kitchenpos.exception.CannotUpdatedException;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuGroup;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
@DisplayName("테이블 그룹 도메인 테스트")
class TableGroupTest {

    final TableGroup tableGroup = TableGroup.create();
    final OrderTable orderTable_1 = OrderTable.of(0, true);
    final OrderTable orderTable_2 = OrderTable.of(0, true);

    @Test
    @DisplayName("단체 지정")
    void addOrderTables() {
        ReflectionTestUtils.setField(tableGroup, "id", 1L);
        ReflectionTestUtils.setField(orderTable_1, "id", 1L);
        ReflectionTestUtils.setField(orderTable_2, "id", 2L);

        tableGroup.addOrderTables(Arrays.asList(orderTable_1, orderTable_2));

        assertAll(
            () -> assertNotNull(tableGroup.getCreatedDate()),
            () -> assertTrue(orderTable_1.equalTableGroup(tableGroup)),
            () -> assertTrue(orderTable_2.equalTableGroup(tableGroup))
        );

    }

    @Test
    @DisplayName("단체 지정 해제")
    void clearOrderTable() {
        addOrderTables();

        tableGroup.clearOrderTable();

        assertAll(
            () -> assertThat(tableGroup.getOrderTables().size()).isEqualTo(0),
            () -> assertFalse(orderTable_1.equalTableGroup(tableGroup)),
            () -> assertFalse(orderTable_2.equalTableGroup(tableGroup))
        );
    }

    @Test
    @DisplayName("주문이 진행중인 테이블이 있는 경우 단체 지정 해제 불가")
    void clearOrderTableOnGoingOrder() {
        addOrderTables();

        final Menu menu = Menu.of("후라이드치킨", 10000, MenuGroup.from("치킨"));
        final OrderLineItem orderLineItem = OrderLineItem.of(menu, 2L);

        Order.of(orderTable_1, OrderStatus.MEAL, Arrays.asList(orderLineItem));
        Order.of(orderTable_2, OrderStatus.COMPLETION, Arrays.asList(orderLineItem));

        assertThatThrownBy(() -> tableGroup.clearOrderTable())
            .isInstanceOf(CannotUpdatedException.class)
            .hasMessage("주문이 완료되지 않은 테이블이 있습니다.");
    }
}