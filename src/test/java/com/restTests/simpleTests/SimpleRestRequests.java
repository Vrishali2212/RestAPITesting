package com.restTests.simpleTests;

import java.util.ArrayList;

import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class SimpleRestRequests {
	@Test
	
	public void TestFakeRestAPI() {
		
	RequestSpecification request =	RestAssured.given();
	Response response = request.when().get("https://fakerestapi.azurewebsites.net//api/v1/Activities/") ;
	response.then().contentType(ContentType.JSON) ; 
	response.then().statusCode(200);
	response.then().time(Matchers.lessThanOrEqualTo(3000L) ) ;
	System.out.println(response.prettyPrint());
	//System.out.println(response.asString());
	//System.out.println(response.body().prettyPrint());
	//System.out.println(response.statusCode());
	//System.out.println(response.contentType());
	
//	String title= response.body().jsonPath().getString("[1].title") ; 
//	System.out.println("title = " + title);
	//Assert.assertEquals(title, "Activity 2");
	//response.then().body("[1].title", Matchers.is("Activity 2"));
	System.out.println(response.body().jsonPath().get("[2].title") );
	System.out.println(response.body().jsonPath().get("size()"));
	ArrayList<Boolean>  completed = response.body().jsonPath().get("completed") ; 
	//System.out.println("No. of records = " + numberofIds);
	for (boolean i:completed ) {
		System.out.println(i);
	}
	
	int minid = response.body().jsonPath().get("id.min()") ; 
	int maxid = response.body().jsonPath().get("id.max()") ; 
	System.out.println("Minimum id = " + minid);
	System.out.println("Maximum id = " + maxid );
	}
	@Test
	
	public void TestFakeRestAPI2() {
		
		RestAssured.given()
		.when()
		.get("https://fakerestapi.azurewebsites.net//api/v1/Activities/")
		.then()
		.statusCode(200)
		.contentType(ContentType.JSON)
		.time(Matchers.lessThan(3000L))
		.body("[1].title" , Matchers.is("Activity 2")) ; 
		
		
		
		
		
		
		
		
		
		
		
	}
}
