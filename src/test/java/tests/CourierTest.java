package tests;

import clients.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.Courier;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class CourierTest {
    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setup() {
        courierClient = new CourierClient();
        courier = new Courier("test_" + System.currentTimeMillis(), "password", "name");
    }

    @After
    public void setUp() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка, что курьера можно создать")
    public void testCreateCourierSuccess() {
        Response response = courierClient.createCourierRequest(courier);
        response.then()
                .statusCode(201)
                .body("ok", is(true))
                .body("$", hasKey("ok")); // Есть ли "ok" в ответе

        Response loginResponse = courierClient.loginCourierRequest(courier);
        loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("$", hasKey("id")); // Есть ли "id"

        courierId = loginResponse.then().extract().path("id");
    }


    @Test
    @DisplayName("Запрос с повторяющимся логином")
    @Description("Проверка, что нельзя создать двух одинаковых курьеров")
    public void testCreateDuplicateCourier() {
        courierClient.createCourierRequest(courier);
        Response loginResponse = courierClient.loginCourierRequest(courier);
        courierId = loginResponse.then().extract().path("id");

        Response response = courierClient.createCourierRequest(courier);
        response.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .body("$", hasKey("message")); // Проверка, что возвращается ошибка
    }

    @Test
    @DisplayName("Запрос без логина или пароля")
    @Description("Проверка создания курьера без обязательных полей")
    public void testCreateCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier("", "password", "name");
        Response response = courierClient.createCourierRequest(courierWithoutLogin);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .body("$", hasKey("message")); // Проверка, что возвращается ошибка
    }

}