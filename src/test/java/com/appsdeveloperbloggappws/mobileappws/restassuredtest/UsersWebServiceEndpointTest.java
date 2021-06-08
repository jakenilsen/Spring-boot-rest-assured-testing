package com.appsdeveloperbloggappws.mobileappws.restassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.internal.MethodSorter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UsersWebServiceEndpointTest {

    private final String CONTEXT_PATH = "/mobile-app-ws";
    private final String EMAIL_ADDRESS = "test@test.com";
    private final String PASSWORD = "123";
    private final String JSON = "application/json";
    private static String authorizationHeader;
    private static String userId;
    private static List<Map<String, String>> addresses;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    /*
        testUserLogin()
     */
    @Test
    final void a() {

        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("email", EMAIL_ADDRESS);
        loginDetails.put("password", PASSWORD);

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(loginDetails)
                .when()
                .post(CONTEXT_PATH + "/users/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        authorizationHeader = response.header("Authorization");
        userId = response.header("UserID");

        assertNotNull(authorizationHeader);
        assertNotNull(userId);
    }

    /*
         testGetUserDetails()
      */
    @Test
    final void b() {

        Response response = given()
                .pathParam("userId", userId)
                .accept(JSON)
                .header("Authorization", authorizationHeader)
                .when()
                .get(CONTEXT_PATH + "/users/{userId}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String userPublicId = response.jsonPath().getString("userId");
        String userEmail = response.jsonPath().getString("email");
        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");

        addresses = response.jsonPath().getList("addresses");
        String addressId = addresses.get(0).get("addressId");

        assertNotNull(userPublicId);
        assertNotNull(userEmail);
        assertNotNull(firstName);
        assertNotNull(lastName);
        assertEquals(EMAIL_ADDRESS, userEmail);

        assertTrue(addresses.size() == 2);
        assertTrue(addressId.length() == 30);
    }

    /*
         testUpdateUserDetails()
      */
    @Test
    final void c() {

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Jake");
        userDetails.put("lastName", "Nilsen");

        Response response = given()
                .pathParam("userId", userId)
                .contentType(JSON)
                .accept(JSON)
                .header("Authorization", authorizationHeader)
                .body(userDetails)
                .when()
                .put(CONTEXT_PATH + "/users/{userId}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");

        List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");

        assertEquals(firstName, userDetails.get("firstName"));
        assertEquals(lastName, userDetails.get("lastName"));
        assertNotNull(storedAddresses);
        assertTrue(addresses.size() == storedAddresses.size());
        assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
    }

    /*
         testDeleteUserDetails()
      */
    @Test
    @Disabled("Dont wanna delete every time")
    final void d() {
        Response response = given()
                .pathParam("userId", userId)
                .header("Authorization", authorizationHeader)
                .accept(JSON)
                .when()
                .delete(CONTEXT_PATH + "/users/{userId}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String operationResult = response.jsonPath().getString("operationResult");

        assertEquals("SUCCESS", operationResult);
    }
}