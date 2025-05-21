package clients;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import static org.hamcrest.Matchers.notNullValue;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue; // Импорт для notNullValue()

public class OrderClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";
    private static final Gson gson = new Gson();

    @Step("Отправка запроса на создание заказа")
    public Response sendCreateOrderRequest(Order order) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(gson.toJson(order))
                .when()
                .post("/api/v1/orders");
    }


    @Step("Получить список заказов")
    public Response getOrderList() {
        return given()
                .baseUri(BASE_URL)
                .when()
                .get("/api/v1/orders");
    }

    public Response getOrderListRequest() {
        return getOrderList();
    }
}