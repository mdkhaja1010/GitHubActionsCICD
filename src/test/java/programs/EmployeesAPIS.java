package programs;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeesAPIS {

	private static final Logger logger = LoggerFactory.getLogger(EmployeesAPIS.class);

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
	
	public String getQuery="select * from employee";

	
	

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
	public void getEmploees() throws SQLException {
		utils = new ReusableUtils();
		Response response = utils.doGet(token, baseUrl, "/api/employees");
		int statuscode = response.getStatusCode();
		// String statusMessage=response.getStatusLine();
		// boolean flag=statuscodeValid(statuscode, 200, false);
		Assert.assertTrue(utils.statuscodeValid(statuscode, 200, false), "status code missmatch");
		logger.info("Employee list retrieved successfully with status: {}", statuscode);
		System.out.println("-----------------------Employees list Printed----------------------");
		ResultSet rs = dbutils.executeQuery(getQuery);
		while (rs.next()) {
		    System.out.println(rs.getString("name"));
		    String name=rs.getString("name");
		    logger.info("Employee list retrieved successfully with status: {}", name);
		}
		rs.close();
		
	}

	@Test(priority = 2)
	public void createEmployee() throws SQLException {
		logger.info("Creating new employee record...");
		utils = new ReusableUtils();
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("name", "john");
		requestBody.put("department", "IT");
		requestBody.put("email", "john@example.com");
		String name11=(String) requestBody.get("name");
		String department11=(String) requestBody.get("department");
		String email11=(String) requestBody.get("email");
		
		Response response = utils.doPost(token, baseUrl, "/api/employees", requestBody);
		int statuscode = response.getStatusCode();
		Assert.assertTrue(utils.statuscodeValid(statuscode, 201, false), "status code missmatch");
		String nameValidation = response.jsonPath().getString("name");
		String emailValidation = response.jsonPath().getString("email");
		id = response.jsonPath().getString("id");
		Assert.assertTrue(utils.fieldsValidation(nameValidation, name11, false), "user name miss match");
		Assert.assertTrue(utils.fieldsValidation(emailValidation, email11, false), "email miss match");
		logger.info("Employee created with ID: {}", id);
		String getQuery1="select * from employee where id='"+id+"'";
		ResultSet rs = dbutils.executeQuery(getQuery1);
		while (rs.next()) {
		    String name=rs.getString("name");
		    String department=rs.getString("department");
		    String email=rs.getString("email");
		    Assert.assertTrue(utils.fieldsValidation(name11, name, false),"db name is mismatch");
		    Assert.assertTrue(utils.fieldsValidation(department11, department, false),"db department is mismatch");
		    Assert.assertTrue(utils.fieldsValidation(email11, email, false),"db email is mismatch");
		}
		rs.close();
		System.out.println("-----------------------Employee Created----------------------");
	}

	@Test(priority = 3)
	public void gettingCreatedEmployeeDetails() {
		logger.info("Fetching created employee with ID: {}", id);
		utils = new ReusableUtils();
		Response response = utils.doGet(token, baseUrl, "/api/employees" + "/" + id);
		int statuscode = response.getStatusCode();
		Assert.assertTrue(utils.statuscodeValid(statuscode, 200, false), "status code missmatch");
		logger.info("Employee details retrieved successfully.");
		System.out.println("-----------------------Created Employee Printed----------------------");
	}

	@Test(priority = 4)
	public void updateEmployee() {
		logger.info("Updating employee with ID: {}", id);
		utils = new ReusableUtils();
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("name", "johnson");
		requestBody.put("department", "IT");
		requestBody.put("email", "johnson@example.com");
		Response response = utils.doPut(token, baseUrl, "/api/employees" + "/" + id, requestBody);
		int statuscode = response.getStatusCode();
		Assert.assertTrue(utils.statuscodeValid(statuscode, 200, false), "status code missmatch");
		String nameValidation = response.jsonPath().getString("name");
		String emailValidation = response.jsonPath().getString("email");
		Assert.assertTrue(utils.fieldsValidation(nameValidation, "johnson", false), "user name miss match");
		Assert.assertTrue(utils.fieldsValidation(emailValidation, "johnson@example.com", false), "email miss match");
		logger.info("Employee updated successfully with new name: {}", nameValidation);
		System.out.println("-----------------------Created Employee updated----------------------");
	}

	@Test(priority = 5)
	public void gettingUpdatedEmployeeDetails() {
		logger.info("Fetching updated employee details for ID: {}", id);
		utils = new ReusableUtils();
		Response response = utils.doGet(token, baseUrl, "/api/employees" + "/" + id);
		int statuscode = response.getStatusCode();
		Assert.assertTrue(utils.statuscodeValid(statuscode, 200, false), "status code missmatch");
		utils.validateSchema(response, "schemas/employeeschema.json");
		logger.info("Updated employee details validated against schema.");
		System.out.println("-----------------------Updateded Employee Printed----------------------");

	}

	@Test(priority = 6)
	public void deleteUpdatedEmployee() {
		logger.info("Deleting employee record with ID: {}", id);
		utils = new ReusableUtils();
		Response response = utils.doDel(token, baseUrl, "/api/employees" + "/" + id);
		int statuscode = response.getStatusCode();
		Assert.assertTrue(utils.statuscodeValid(statuscode, 204, false), "status code missmatch");
		logger.info("Employee deleted successfully.");
		System.out.println("-----------------------Deleted Employee Record----------------------");
		//working of te API's
		
	}

}
