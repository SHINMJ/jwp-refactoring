package kitchenpos.order.acceptance;

import static kitchenpos.menu.acceptance.MenuAcceptanceTest.메뉴등록되어있음;
import static kitchenpos.menu.acceptance.MenuGroupAcceptanceTest.메뉴그룹_등록_되어있음;
import static kitchenpos.product.acceptance.ProductAcceptanceTest.상품_등록_되어있음;
import static kitchenpos.table.acceptance.TableAcceptanceTest.주문테이블_등록되어있음;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kitchenpos.AcceptanceTest;
import kitchenpos.menu.dto.MenuGroupResponse;
import kitchenpos.menu.dto.MenuResponse;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.order.dto.OrderLineItemRequest;
import kitchenpos.order.dto.OrderRequest;
import kitchenpos.order.dto.OrderResponse;
import kitchenpos.product.dto.ProductResponse;
import kitchenpos.table.dto.OrderTableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("주문 인수 테스트")
public class OrderAcceptanceTest extends AcceptanceTest {

    private static final String URL = "/api/orders";


    MenuResponse 세트_1;
    OrderTableResponse orderTable;

    public static ExtractableResponse<Response> 주문_생성_요청(Long tableId,
        List<OrderLineItemRequest> orderLineItems) {
        OrderRequest orderRequest = new OrderRequest(tableId, orderLineItems);

        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(orderRequest)
            .when().post(URL)
            .then().log().all()
            .extract();
    }

    public static void 주문_생성됨(ExtractableResponse<Response> response) {
        OrderResponse order = response.as(OrderResponse.class);

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertNotNull(order.getOrderedTime()),
            () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name())
        );
    }

    public static ExtractableResponse<Response> 주문_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(URL)
            .then().log().all()
            .extract();
    }

    public static void 주문_목록_조회됨(ExtractableResponse<Response> response,
        List<OrderResponse> expected) {
        List<OrderResponse> orders = response.jsonPath().getList(".", OrderResponse.class);
        List<Long> expectedIds = expected.stream()
            .map(OrderResponse::getOrderTableId)
            .collect(Collectors.toList());

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(orders)
                .extracting(OrderResponse::getOrderTableId)
                .containsAll(expectedIds)
        );
    }

    public static ExtractableResponse<Response> 주문_상태변경_요청(Long orderId, String status) {
        Map<String, String> params = new HashMap<>();
        params.put("orderStatus", status);
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().put(URL + "/{orderId}/order-status", orderId)
            .then().log().all()
            .extract();
    }

    public static void 주문_상태_변경됨(ExtractableResponse<Response> response, String expectedStatus) {
        OrderResponse order = response.as(OrderResponse.class);

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(order.getOrderStatus()).isEqualTo(expectedStatus)
        );
    }

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        // 상품 등록 되어 있음
        ProductResponse 후라이드치킨 = 상품_등록_되어있음("후라이드치킨", 10000);
        ProductResponse 양념치킨 = 상품_등록_되어있음("양념치킨", 11000);
        // 메뉴그룹 등록 되어 있음
        MenuGroupResponse 치킨 = 메뉴그룹_등록_되어있음("치킨");
        // 메뉴 등록되어 있음
        세트_1 = 메뉴등록되어있음("세트1", 20000, 치킨, Arrays.asList(후라이드치킨, 양념치킨));
        // 주문 테이블 등록되어 있음
        orderTable = 주문테이블_등록되어있음(3);
    }

    @DisplayName("테이블별 주문을 관리한다.")
    @TestFactory
    Stream<DynamicTest> dynamicTestStreamManageOrder() {
        return Stream.of(
            DynamicTest.dynamicTest("주문을 생성한다.", () -> {
                // 주문 생성 요청
                ExtractableResponse<Response> saveResponse = 주문_생성_요청(orderTable.getId(),
                    Arrays.asList(new OrderLineItemRequest(세트_1.getId(), 2L)));

                // 주문 생성됨
                주문_생성됨(saveResponse);

                // 주문 목록 조회
                ExtractableResponse<Response> listResponse = 주문_목록_조회_요청();

                // 주문 목록 조회됨
                주문_목록_조회됨(listResponse, Arrays.asList(saveResponse.as(OrderResponse.class)));
            }),
            DynamicTest.dynamicTest("주문의 상태를 변경한다.", () -> {
                // Given 주문 목록 조회
                ExtractableResponse<Response> listResponse = 주문_목록_조회_요청();
                List<OrderResponse> orders = listResponse.jsonPath()
                    .getList(".", OrderResponse.class);
                OrderResponse order = orders.get(0);

                // 주문 상태 변경 (조리 -> 식사)
                ExtractableResponse<Response> mealResponse = 주문_상태변경_요청(order.getId(),
                    OrderStatus.MEAL.name());

                // 주문 상태 변경됨
                주문_상태_변경됨(mealResponse, OrderStatus.MEAL.name());

                // 주문 상태 변경 (식사 -> 계산완료)
                ExtractableResponse<Response> completionResponse = 주문_상태변경_요청(order.getId(),
                    OrderStatus.COMPLETION.name());

                // 주문 상태 변경됨
                주문_상태_변경됨(completionResponse, OrderStatus.COMPLETION.name());
            })
        );
    }
}
