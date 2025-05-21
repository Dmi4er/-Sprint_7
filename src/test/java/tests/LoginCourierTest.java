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
        // Создаем курьера и авторизуемся для получения id
        courierClient.createCourierRequest(courier);
        Response loginResponse = courierClient.loginCourierRequest(courier);
        courierId = loginResponse.then().extract().path("id");
    }

    @After
    public void teardown() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка, что курьер может авторизоваться с корректными данными")
    public void testCourierLoginSuccess() {
        Response response = courierClient.loginCourierRequest(courier);
        response.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("$", hasKey("id"))
                .body("id", is(not(0)));
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
                .body("$", hasKey("message"));
    }

    @Test
    @DisplayName("Авторизация без логина")
    @Description("Проверка, что система возвращает ошибку при отсутствии логина")
    public void testCourierLoginWithoutLogin() {
        Courier courierWithoutLogin = new Courier("", "password", courier.getFirstName());
        Response response = courierClient.loginCourierRequest(courierWithoutLogin);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"))
                .body("$", hasKey("message"));
    }

    @Test
    @DisplayName("Авторизация с неправильным логином")
    @Description("Проверка, что система возвращает ошибку при неправильном логине")
    public void testLoginWithWrongLogin() {
        String wrongLogin = "wrong_login_" + System.currentTimeMillis();
        Courier courierWithWrongLogin = new Courier(wrongLogin, "password", "name");

        Response response = courierClient.loginCourierRequest(courierWithWrongLogin);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"))
                .body("$", hasKey("message"));
    }
}