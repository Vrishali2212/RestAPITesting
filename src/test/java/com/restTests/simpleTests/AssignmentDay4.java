package com.restTests.simpleTests;

import java.util.ArrayList;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

public class AssignmentDay4 {
	
	String extractedToken = null ; 
	@Test
	public void loginToTekarchAPI () {
		 Response response = RestAssured.given()
		.contentType(ContentType.JSON)
		.body("{\"username\":\"vrishali.bodhale@gmail.com\",\"password\":\"Tekarch@123\"}")
		.when()
		.post("https://us-central1-qa01-tekarch-accmanager.cloudfunctions.net/login") ; 
		
		response
		.then()
		.statusCode(201)
		.contentType(ContentType.JSON) 
		.time(Matchers.lessThan(3000L)) ; 
		
		extractedToken = response.body().jsonPath().get("[0].token") ; 
		System.out.println("Token = " + extractedToken);
	}
	
	@Test(dependsOnMethods = "loginToTekarchAPI")
	public void getUsers() {
		Header reqheader = new Header("token" , extractedToken) ; 
		Response response = RestAssured
		.given()
		.header(reqheader )
		.when()
		.get("https://us-central1-qa01-tekarch-accmanager.cloudfunctions.net/getdata");
		
		response.then()
		.contentType(ContentType.JSON)
		.statusCode(200) ;
		
		// find the total number of records
		int noOfRecords = response.body().jsonPath().get("size()") ; 
		System.out.println("No. of Records = " + noOfRecords);
		
		// validate total records are less than 10000
		response.then()
		.body("size()",Matchers.lessThan(10000));
		
		//find the minimum and maximum salary of all records(note: salary is in string in response)
		
		ArrayList<Integer> salaries = response.body().jsonPath().get("salary");
	//	ArrayList<Integer> salarynumbers = new ArrayList() ; 
		for (Integer s : salaries) {
			//salarynumbers.add(Integer.parseInt(s)) ; 
			System.out.print(s+" | ");
		}
		
		//System.out.println(salarynumbers.size());
		
		
		
		//System.out.println("Minimum Salary = " + minSalary);
		
	}

}
