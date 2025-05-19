package clients;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";
    private static final Gson gson = new Gson();

    @Step("Отправка запроса на создание заказа")
    public Response sendCreateOrderRequest(Order order) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(gson.toJson(order)) // Сериализация в JSON
                .when()
                .post("/api/v1/orders");
    }
    @Step("Проверка создания заказа: код ответа 201 и непустой track")
    static void validateCreateOrderResponse(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
    @Step("Проверка получения списка заказов: код ответа 200 и непустой список")
    static void validateGetOrderList(Response response) {
        response.then()
                .statusCode(200)
                .body("orders", not(empty()));
    }


    @Step("Получить список заказов")
    public Response getOrderList() {
        return given()
                .baseUri(BASE_URL)
                .when()
                .get("/api/v1/orders");
    }

}