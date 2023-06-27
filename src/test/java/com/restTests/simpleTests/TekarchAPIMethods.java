package com.restTests.simpleTests;

import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.restTests.POJOs.LoginDataPOJO;
import com.restTests.POJOs.LoginResponsePOJO;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

public class TekarchAPIMethods {
	String extractedToken = null ; 
	String extractedUserId =  null ; 
	
	@BeforeClass
	public void beforeclass() {
		RestAssured.baseURI="https://us-central1-qa01-tekarch-accmanager.cloudfunctions.net/";
	}
	@Test
	public void loginToTekarchAPI () {
		LoginDataPOJO logindata = new LoginDataPOJO();
		logindata.setUsername("vrishali.bodhale@gmail.com");
		logindata.setPassword("Tekarch@123");
		
		 Response response = RestAssured.given()
		.contentType(ContentType.JSON)
	//	.body("{\"username\":\"vrishali.bodhale@gmail.com\",\"password\":\"Tekarch@123\"}")
		.body(logindata) // jackson --> serialization 
		.when()
		.post("login") ; 
		
		response
		.then()
		.statusCode(201)
		.contentType(ContentType.JSON) 
		.time(Matchers.lessThan(3000L)) ; 
		
		response.prettyPrint();
		
		LoginResponsePOJO[] loginresponsepojolist = response.as(LoginResponsePOJO[].class);
		extractedToken = loginresponsepojolist[0].getToken();
		extractedUserId =loginresponsepojolist[0].getUserid();
		System.out.println("extractedToken = " + extractedToken);
		System.out.println("extractedUserId = " + extractedUserId);
		
	}
	
	public void loginToTekarchAPIUsingString () {
		 Response response = RestAssured.given()
		.contentType(ContentType.JSON)
		.body("{\"username\":\"vrishali.bodhale@gmail.com\",\"password\":\"Tekarch@123\"}") 
		.when()
		.post("login") ; 
		
		response
		.then()
		.statusCode(201)
		.contentType(ContentType.JSON) 
		.time(Matchers.lessThan(3000L)) ; 
	
		//response.prettyPrint();
		//response.prettyPeek();
		
		extractedToken = response.body().jsonPath().get("[0].token") ; 
		extractedUserId = response.body().jsonPath().get("[0].userid");
		System.out.println("extractedToken = " + extractedToken);
		System.out.println("extractedUserId = " + extractedUserId);
		System.out.println("Login Successful ");
		
	}
	@Test(dependsOnMethods = "loginToTekarchAPI")
	public void getUsers() {
		Header reqheader = new Header("token" , extractedToken) ; 
		Response response = RestAssured
		.given()
		.header(reqheader )
		.when()
		.get("getdata")
		;
		
		response.then()
		.contentType(ContentType.JSON)
		.statusCode(200) ;
		
		
	 int noOfRecords = response.body().jsonPath().get("size()") ; 
	 System.out.println("No. of Records = " + noOfRecords);
		//response.prettyPrint();
		//System.out.println(response.prettyPrint());
	 
	 System.out.println("Data Retrieved  ");
		
	}
	@Test(dependsOnMethods = "loginToTekarchAPI")
	public void createUser() {
		Header reqheader = new Header("token" ,extractedToken ) ; 
		Response response = RestAssured.given()
				.header(reqheader)
				.contentType(ContentType.JSON)
				.body("{\"accountno\":\"TA-POST14\",\"departmentno\":\"1\",\"salary\":\"100001\",\"pincode\":\"415000\"}")
		.when().post("addData") ; 
		
		response.then()
		.statusCode(201) 
		.body("status", Matchers.is("success"))  ; 
		response.prettyPrint();
		String status = response.body().jsonPath().get("status") ; 
		System.out.println("Status = "+status);
		
		System.out.println("User Created ");
				
	}
	@Test(dependsOnMethods = "loginToTekarchAPI")
	public void deleteUser() {
		Header reqheader = new Header("token" ,extractedToken ) ; 
		Response response = RestAssured.given()
		.contentType(ContentType.JSON)
		.body("{\"id\": \"n0VbVUEASaJ1xdMcKz7g\", \"userid\": \""+extractedUserId+"\"}")
		.header(reqheader)
		.when()
		.delete("deleteData") ; 
		
		response.then()
		.statusCode(200) ; 
		
		response.then()
		.body("status",Matchers.is("success"));
		
		response.prettyPrint();
		System.out.println("Data Deleted ");
	}
	@Test(dependsOnMethods = "loginToTekarchAPI")
	public void updateUser() {
		Header reqheader = new Header("token" ,extractedToken ) ; 
		Response response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body("{\"accountno\":\"TA-post001\",\"departmentno\":5,\"salary\":100001,\"pincode\":415000,\"userid\":\"EukwyDBLmnUYk0Eabe5B\",\"id\":\"4Fp8LmD3lBCww9OTHAfK\"}")
				.header(reqheader)
				.when()
				.put("updateData");
		
		response.then()
		.statusCode(200)
		.body("status",Matchers.is("success"));
		
		System.out.println("Data updated ");
		
	}
	
	

}
