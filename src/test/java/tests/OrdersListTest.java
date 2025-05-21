package tests;

import clients.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class OrdersListTest {
    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что в теле ответа возвращается список заказов с правильной структурой")
    public void testGetOrderList() {
        Response response = new OrderClient().getOrderListRequest();
        response.then()
                .statusCode(200)
                .body("orders", not(empty()))
                .body("$", hasKey("orders")) // Проверка наличия поля "orders"
                .body("orders[0]", hasKey("id")) // Проверка структуры первого заказа
                .body("orders[0]", hasKey("track"));
    }
}