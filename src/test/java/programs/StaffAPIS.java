package programs;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class StaffAPIS {
    private static final Logger logger = LoggerFactory.getLogger(StaffAPIS.class);

    public ReusableUtils utils;
    public DBUtils dbutils;

    public static String id;

    public String token;
    public static Properties prop = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties file!", e);
        }
    }
    public String baseUrl = prop.getProperty("host");
    public String client_id = prop.getProperty("client_id");
    public String client_secret = prop.getProperty("client_secret");
    public String grant_type = prop.getProperty("grant_type");
    public String tokenUrl = prop.getProperty("tokenUrl");

    public String getQuery="select * from staff";

    @BeforeTest
    public String gettingToken() {
        RequestSpecification res = RestAssured.given();
        logger.info("Fetching access token from Keycloak...");
        res.baseUri(tokenUrl).contentType("application/x-www-form-urlencoded").formParam("client_id", client_id)
                .formParam("client_secret", client_secret).formParam("grant_type", grant_type).log().all();
        Response response = res.post();
        response.prettyPrint();
        token = response.jsonPath().getString("access_token");
        if (token != null) {
            logger.info("Token successfully generated: {}", token.substring(0, 15) + "...");
        } else {
            logger.error("Failed to generate token! Response: {}", response.asString());
        }
        return token;

    }
    @Test(priority = 1)
    public void getStaff() throws SQLException {
        utils = new ReusableUtils();
        Response response = utils.doGet(token, baseUrl, "api/staff");
        int statuscode = response.getStatusCode();
        // String statusMessage=response.getStatusLine();
        // boolean flag=statuscodeValid(statuscode, 200, false);
        Assert.assertTrue(utils.statuscodeValid(statuscode, 200, false), "status code missmatch");
        logger.info("Staff list retrieved successfully with status: {}", statuscode);
        System.out.println("-----------------------Staff list Printed----------------------");
        ResultSet rs = dbutils.executeQuery(getQuery);
        while (rs.next()) {
          // System.out.println(rs.getString("staff_name"));
            String name=rs.getString("staff_name");
            logger.info("Employee list retrieved successfully with status: {}", name);
        }
        rs.close();
    }
    @Test(priority = 2)
    public void createStaffRecord() throws SQLException {
        logger.info("Creating new staff record...");
        utils = new ReusableUtils();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("staffName", "suman");
        requestBody.put("email", "suman@example.com");
        requestBody.put("role","Junior Lectoror");
        String staff_name11=(String) requestBody.get("staffName");
        String role11=(String) requestBody.get("role");
        String email11=(String) requestBody.get("email");
        Response response = utils.doPost(token, baseUrl, "/api/staff", requestBody);
        int statuscode = response.getStatusCode();
        Assert.assertTrue(utils.statuscodeValid(statuscode, 201, false), "status code missmatch");
        String nameValidation = response.jsonPath().getString("staffName");
        String emailValidation = response.jsonPath().getString("email");
        String roleValidation=response.jsonPath().getString("role");
        id = response.jsonPath().getString("id");
        Assert.assertTrue(utils.fieldsValidation(nameValidation, staff_name11, false), "user name miss match");
        Assert.assertTrue(utils.fieldsValidation(emailValidation, email11, false), "email miss match");
        Assert.assertTrue(utils.fieldsValidation(roleValidation,role11,false),"role miss match");
        logger.info("Staff created with ID: {}", id);
        String getQuery1="select * from staff where id='"+id+"'";
        ResultSet rs = dbutils.executeQuery(getQuery1);
        while (rs.next()) {
            String name=rs.getString("staff_name");
            String role=rs.getString("role");
            String email=rs.getString("email");
            Assert.assertTrue(utils.fieldsValidation(staff_name11, name, false),"db name is mismatch");
            Assert.assertTrue(utils.fieldsValidation(role11, role, false),"db department is mismatch");
            Assert.assertTrue(utils.fieldsValidation(email11, email, false),"db email is mismatch");
        }
        rs.close();
        System.out.println("-----------------------Staff Created----------------------");
    }
    @Test(priority = 3)
    public void gettingCreatedStaffDetails() {
        logger.info("Fetching created Staff with ID: {}", id);
        utils = new ReusableUtils();
        Response response = utils.doGet(token, baseUrl, "/api/staff" + "/" + id);
        int statuscode = response.getStatusCode();
        Assert.assertTrue(utils.statuscodeValid(statuscode, 200, false), "status code missmatch");
        logger.info("Staff details retrieved successfully.");
        System.out.println("-----------------------Created Staff Printed----------------------");
    }
    @Test(priority = 4)
    public void updateStaff() {
        logger.info("Updating staff with ID: {}", id);
        utils = new ReusableUtils();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("staffName", "SumanTV");
        requestBody.put("email", "SumanTV@gmail.com");
        requestBody.put("role","Junior Lectoror");
        String staffName=(String) requestBody.get("staffName");
        String email=(String) requestBody.get("email");
        Response response = utils.doPut(token, baseUrl, "/api/staff" + "/" + id , requestBody);
        int statuscode = response.getStatusCode();
        Assert.assertTrue(utils.statuscodeValid(statuscode, 200, false), "status code missmatch");
        String nameValidation = response.jsonPath().getString("staffName");
        String emailValidation = response.jsonPath().getString("email");
        Assert.assertTrue(utils.fieldsValidation(nameValidation, staffName, false), "user name miss match");
        Assert.assertTrue(utils.fieldsValidation(emailValidation, email, false), "email miss match");
        logger.info("Employee updated successfully with new name: {}", nameValidation);
        System.out.println("-----------------------Created Employee updated----------------------");
    }
    @Test(priority = 5)
    public void gettingUpdatedStaffDetails() {
        logger.info("Fetching updated staff details for ID: {}", id);
        utils = new ReusableUtils();
        Response response = utils.doGet(token, baseUrl, "/api/staff" + "/" + id);
        int statuscode = response.getStatusCode();
        Assert.assertTrue(utils.statuscodeValid(statuscode, 200, false), "status code missmatch");
        utils.validateSchema(response, "schemas/staffschema.json");
        logger.info("Updated staff details validated against schema.");
        System.out.println("-----------------------Updateded staff Printed----------------------");

    }
    @Test(priority = 6)
    public void deleteUpdatedStaff() {
        logger.info("Deleting staff record with ID: {}", id);
        utils = new ReusableUtils();
        Response response = utils.doDel(token, baseUrl, "/api/staff" + "/" + id);
        int statuscode = response.getStatusCode();
        Assert.assertTrue(utils.statuscodeValid(statuscode, 204, false), "status code missmatch");
        logger.info("Staff deleted successfully.");
        System.out.println("-----------------------Deleted staff Record----------------------");
        //working of te API's

    }

}
