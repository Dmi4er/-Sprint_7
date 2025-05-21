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
import static org.apache.http.HttpStatus.*;

public class CourierTest {
    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = new Courier("test_" + System.currentTimeMillis(), "password", "name");
        courierId = 0; // Инициализация переменной
    }

    @After
    public void tearDown() {
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
                .statusCode(SC_CREATED)

                .body("ok", is(true))
                .body("$", hasKey("ok")); // Есть ли "ok" в ответе

        Response loginResponse = courierClient.loginCourierRequest(courier);
        loginResponse.then()
                .statusCode(SC_OK)
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
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .body("$", hasKey("message")); // Проверка, что возвращается ошибка
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description("Проверка создания курьера без обязательных полей")
    public void testCreateCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier("", "password", "name");
        Response response = courierClient.createCourierRequest(courierWithoutLogin);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .body("$", hasKey("message"));
    }

    //создание курьера без пароля
    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Проверка, что создание курьера без пароля возвращает ошибку")
    public void testCreateCourierWithoutPassword() {
        Courier courierWithoutPassword = new Courier("test_" + System.currentTimeMillis(), "", "name");
        Response response = courierClient.createCourierRequest(courierWithoutPassword);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .body("$", hasKey("message"));
    }
}