package programs;

import java.util.Map;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ReusableUtils {
	
	public boolean statuscodeValid(int actualcode, int expectedcode, boolean flag) {
		if(actualcode==expectedcode) {
			flag=true;	
		}
		return flag;	
	}
	public boolean fieldsValidation(String actualField, String expectedFiled,boolean flag) {
		if(actualField.equals(expectedFiled)) {
			flag=true;
		}
		return flag;
	}
	public void validateSchema(Response response, String schemaPath) {
        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath(schemaPath));
    }
	
	
	public Response doPost(String token, String Url, String path, Map<String, Object> requestBody) {
		RequestSpecification res=RestAssured.given();
		res.baseUri(Url)
		.basePath(path)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.headers("Authorization", "Bearer " + token)
		.body(requestBody)
		.log().all();
		Response response= res.post();
		response.prettyPrint();
		return response;
		
	}
	public Response doGet(String token, String Url, String path) {
		RequestSpecification res=RestAssured.given();
		res.baseUri(Url)
		.basePath(path)
		.contentType(ContentType.JSON)   // ✅ needed
	    .accept(ContentType.JSON)
		.headers("Authorization", "Bearer " + token)
		.log().all();
		Response response= res.get();
		response.prettyPrint();
		return response;
		
	}
	public Response doPut(String token, String Url, String path, Map<String, Object> requestBody) {
		RequestSpecification res=RestAssured.given();
		res.baseUri(Url)
		.basePath(path)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.headers("Authorization", "Bearer " + token)
		.body(requestBody)
		.log().all();
		Response response= res.put();
		response.prettyPrint();
		return response;
		
	}
	public Response doDel(String token, String Url, String path) {
		RequestSpecification res=RestAssured.given();
		res.baseUri(Url)
		.basePath(path)
		.contentType(ContentType.JSON)
	    .accept(ContentType.JSON)
		.headers("Authorization", "Bearer " + token)
		.log().all();
		Response response= res.delete();
		response.prettyPrint();
		return response;
		
	}
	

}
