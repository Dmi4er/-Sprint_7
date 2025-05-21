package clients;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Courier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";
    private static final String ENDPOINT_CREATE_COURIER = "/api/v1/courier";
    private static final String ENDPOINT_LOGIN_COURIER = "/api/v1/courier/login";
    private static final String ENDPOINT_DELETE_COURIER = "/api/v1/courier/";


    @Step("Создать курьера")
    public Response createCourierRequest(Courier courier) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(ENDPOINT_CREATE_COURIER);
    }

    @Step("Авторизовать курьера")
    public Response loginCourierRequest(Courier courier) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(ENDPOINT_LOGIN_COURIER);
    }

    @Step("Удалить курьера")
    public void delete(int courierId) {
        given()
                .baseUri(BASE_URL)
                .when()
                .delete(ENDPOINT_DELETE_COURIER + courierId);
    }
}