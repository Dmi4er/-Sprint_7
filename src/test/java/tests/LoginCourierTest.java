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

public class LoginCourierTest {
    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = new Courier("test_" + System.currentTimeMillis(), "password", "name");
        courierClient.createCourierRequest(courier);
        Response loginResponse = courierClient.loginCourierRequest(courier);
        courierId = loginResponse.then().extract().path("id");
    }

    @After
    public void teardown() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка, что курьер может авторизоваться с корректными данными")
    public void testCourierLoginSuccess() {
        Response response = courierClient.loginCourierRequest(courier);
        response.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("$", hasKey("id")) // Проверка наличие поля
                .body("id", is(not(0))); //Проверка, что при авторизации возвращается не пустой id
    }

    @Test
    @DisplayName("Авторизация с неправильным паролем")
    @Description("Проверка, что система возвращает ошибку при неправильном пароле")
    public void testLoginWithWrongPassword() {
        Courier courierWithWrongPassword = new Courier(courier.getLogin(), "wrongPassword", courier.getFirstName());
        Response response = courierClient.loginCourierRequest(courierWithWrongPassword);
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"))
                .body("$", hasKey("message")); // Проверка, что возвращается ошибка
    }

    @Test
    @DisplayName("Авторизация без пароля")
    @Description("Проверка, что система возвращает ошибку при отсутствии пароля")
    public void testCourierLoginWithoutPassword() {
        Courier courierWithoutPassword = new Courier(courier.getLogin(), "", courier.getFirstName());
        Response response = courierClient.loginCourierRequest(courierWithoutPassword);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"))
                .body("$", hasKey("message")); // Проверка, что возвращается ошибка
    }
}