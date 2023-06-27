package com.restTests.simpleTests;

import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.restTests.POJOs.LoginDataPOJO;
import com.restTests.POJOs.LoginResponsePOJO;
import com.restTests.POJOs.createUserPOJO;
import com.restTests.POJOs.deleteUserPOJO;
import com.restTests.POJOs.getUsersResponsePOJO;
import com.restTests.POJOs.updateUserPOJO;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

public class TekarchAPIEndToEnd {

	String extractedToken = null ; 
	String extractedUserId =  null ; 
	getUsersResponsePOJO newuser = null  ; 
	
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
		System.out.println("Login API successful");
		
	}
	
	
	
	
	
	
	@Test(dependsOnMethods = "loginToTekarchAPI")
	public void createUser() {
		createUserPOJO newuser = new createUserPOJO(); 
		newuser.setAccountno("TA-POST25") ; 
		newuser.setDepartmentno("1");
		newuser.setSalary("1000001");
		newuser.setPincode("415000");
		
		System.out.println("Creating user with account no. : "+newuser.getAccountno());
		
		Header reqheader = new Header("token" ,extractedToken ) ; 
		Response response = RestAssured.given()
				.header(reqheader)
				.contentType(ContentType.JSON)
				.body(newuser)
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
	public void getUsers() {
		Header reqheader = new Header("token" , extractedToken) ; 
		Response response = RestAssured
		.given()
		.header(reqheader )
		.when()
		.get("getdata");
		
		response.then().contentType(ContentType.JSON).statusCode(200) ;
	
		 int noOfRecords = response.body().jsonPath().get("size()") ; 
		 System.out.println("No. of Records = " + noOfRecords);
		 
		 getUsersResponsePOJO[] getusersresponselist = response.as(getUsersResponsePOJO[].class) ;
		 System.out.println("Latest user created as below:");
		 System.out.println("Account no. = " +getusersresponselist[0].getAccountno());
		 System.out.println("Dept no. = " +getusersresponselist[0].getDepartmentno());
		 System.out.println("salary = " +getusersresponselist[0].getSalary());
		 System.out.println("pincode  = " +getusersresponselist[0].getPincode());
		 
		 
		 newuser = getusersresponselist[0];
		
		 System.out.println("Data Retrieved  ");
		
	}
	
	
	@Test(dependsOnMethods = "getUsers")
	public void updateUser() {
		updateUserPOJO updateuser = new updateUserPOJO();
		updateuser.setAccountno(newuser.getAccountno());
		updateuser.setDepartmentno(newuser.getDepartmentno());
		updateuser.setPincode(newuser.getPincode());
		updateuser.setSalary(newuser.getSalary());
		updateuser.setId(newuser.getId());
		updateuser.setUserid(newuser.getUserid());
		//Updating pincode for the newly added user.
		
		System.out.println("Updating pincode for user with account no. " + updateuser.getAccountno());
		System.out.println("Old pincode = " + updateuser.getPincode());
		String newpincode = "415111" ; 
		updateuser.setPincode(newpincode);
		System.out.println("Setting New pincode to -> " + updateuser.getPincode());
	
		Header reqheader = new Header("token" ,extractedToken ) ; 
		Response response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body(updateuser)
				.header(reqheader)
				.when()
				.put("updateData");
		
		response.then()
		.statusCode(200)
		.body("status",Matchers.is("success"));
		
		System.out.println("Data updated ");
		
	}
	
	
	@Test(dependsOnMethods = "getUsers")
	public void deleteUser() {
		
		deleteUserPOJO deleteuser = new deleteUserPOJO();
		deleteuser.setId(newuser.getId());
		deleteuser.setUserid(newuser.getUserid());
		
		System.out.println("Deleting user with userid : "+deleteuser.getUserid() +" AND with account No = " +newuser.getAccountno());
		
		Header reqheader = new Header("token" ,extractedToken ) ; 
		Response response = RestAssured.given()
		.contentType(ContentType.JSON)
		.body(deleteuser)
		.header(reqheader)
		.when()
		.delete("deleteData") ; 
		
		response.then()
		.statusCode(200) ; 
		
		response.then()
		.body("status",Matchers.is("success"));
		
		response.prettyPrint();
		System.out.println("Data Deleted for user : " + newuser.getAccountno());
	}
	
	
	
	
	
}
