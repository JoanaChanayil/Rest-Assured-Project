package demo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import pojoGoogleMap.AddPlace;
import pojoGoogleMap.GetBody;
import pojoGoogleMap.Location;

public class AddPlaceRestAssured {

	public static void main(String[] args) {
		//we need to create or set the json body so we r calling the object of the addPlace
		
		AddPlace ap= new AddPlace();
		
		//we need location object as well since we have location values inside this class
		Location l=new Location();
		l.setLng(-38.383494);
		l.setLng(33.427362);
		ap.setLocation(l);
		//using addPlace object call the other variables
		
		ap.setAccuracy(50);
		ap.setName("Frontline house");
		ap.setPhone_number("(+91) 983 893 3937");
		ap.setAddress("29, side layout, cohen 09");
		
		//type is a list of String to add items we need to get the string 
		List<String> listType= new ArrayList<String>();
		listType.add("shoe park");
		listType.add("shop");
		ap.setTypes(listType);
		ap.setWebsite("http://google.com");
		ap.setLanguage("French-IN");
	
		//RestAssured.baseURI="https://rahulshettyacademy.com";
		
		//creating builder for the request section
		//since we use many of the request common for most of the api function we can create a build function to call common for all
		
		RequestSpecification req=new RequestSpecBuilder().setContentType(ContentType.JSON)
		.setBaseUri("https://rahulshettyacademy.com")
		.addQueryParam("key", "qaclick123")
		.build();
		//some response are common those are created as response specification
		ResponseSpecification resp=new ResponseSpecBuilder().expectStatusCode(200).expectContentType(ContentType.JSON).build();
		
		
		//AddPlace--Post
		Response responsePost=given().spec(req)//Common RequestSpec
		.body(ap)
		.when().post("/maps/api/place/add/json")
		.then().spec(resp)//Common ResponseSpec
		.extract().response();
		 
		 String responseString= responsePost.asString();
		 System.out.println(responseString);
		 JsonPath js= new JsonPath(responseString);//for parsing json
			String placeID=js.getString("place_id");
			System.out.println(placeID);
		 
		//AddPlace--Get
		 String responseGet=given().queryParam("place_id", placeID).spec(req)
		 .when().get("maps/api/place/get/json")
		 .then().spec(resp)
		 .extract().response().asString();
		 
		 System.out.println(responseGet);
		 
		 //AddPlace--update---PUT
		 GetBody gb= new GetBody();
		 String newAddress="Summer Walk,Africa";
		 //ap.setAddress(newAddress);
		 gb.setPlace_id(placeID);
		 gb.setAddress(newAddress);
		 gb.setKey("qaclick123");
		 
		 
		 given().log().all().spec(req).queryParam("place_id", placeID)
		 .body(gb)
//		 .body("{\r\n"
//		 		+ "\"place_id\":\""+placeID+"\",\r\n"
//		 		+ "\"address\":\""+newAddress+"\",\r\n"
//		 		+ "\"key\":\"qaclick123\"\r\n"
//		 		+ "}\r\n"
//		 		+ "")
		.when().put("maps/api/place/update/json")
		.then().spec(resp).assertThat().log().all().body("msg", equalTo("Address successfully updated"))
		.extract().response();
			
		 
		 //Delete AddedPlace---DELETE
		 
		 given().log().all().spec(req)
		 .body("\"place_id\":\""+placeID+"\"")
		 .when().delete("/maps/api/place/delete/json")
		 .then().spec(resp).log().all().extract().response();
		
		
		
	}

}
