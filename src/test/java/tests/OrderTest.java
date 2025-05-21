package tests;

import clients.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.Order;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderTest {
    private final List<String> colors;

    public OrderTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][]{
                {Arrays.asList("BLACK")},// цвет BLACK
                {Arrays.asList("GREY")},// цвет GREY
                {Arrays.asList("BLACK", "GREY")},// оба цвета
                {null}// без цвета
        };
    }

    @Test
    @DisplayName("Создание заказа с разными комбинациями цветов")
    @Description("Проверка, что заказ можно создать с разными комбинациями цветов: BLACK, GREY, оба цвета, без цвета")
    public void testCreateOrderWithDifferentColors() {
        Order order = new Order(colors); // создаем объект заказа
        Response response = new OrderClient().sendCreateOrderRequest(order);
        response.then()
                .statusCode(201) //проверка ответа на 201
                .body("track", notNullValue())
                .body("$", hasKey("track"))
                .body("track", is(greaterThan(0))); // Проверка на не пустой track

    }
}