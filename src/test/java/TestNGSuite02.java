import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;


public class TestNGSuite02 {


    @Test
    public void testGetObject3Test() {
        //https://api.restful-api.dev/objects/3

        RestAssured.baseURI  = "https://api.restful-api.dev"; // Example public API

        Response response = given().when().get("/objects/3").thenReturn();

        Assert.assertEquals(response.getStatusCode(),200);
        JsonPath jsonPath = new JsonPath(response.asString());
        String id = jsonPath.getString("id");
        Assert.assertEquals(id,"3","error recuperando id");
        String name = jsonPath.get("name");
        Assert.assertEquals(name,"Apple iPhone 12 Pro Max","error recuperando name");
        String color = jsonPath.getString("data.color");
        Assert.assertEquals(color,"Cloudy White","error recuperando color");
        String capacity = jsonPath.getString("data.'capacity GB'");
        Assert.assertEquals(capacity,"512","error recuperando capacity");

    }

    @Test
    public void testGetObject7Test() {
        //https://api.restful-api.dev/objects/7

        RestAssured.baseURI  = "https://api.restful-api.dev"; // Example public API

        Response response = given().when().get("/objects/7").thenReturn();

        Assert.assertEquals(response.getStatusCode(),200);
        JsonPath jsonPath = new JsonPath(response.asString());
        String id = jsonPath.getString("id");
        Assert.assertEquals(id,"7","error recuperando id");
        String name = jsonPath.get("name");
        Assert.assertEquals(name,"Apple MacBook Pro 16","error recuperando name");
        String year = jsonPath.getString("data.year");
        Assert.assertEquals(year,"2019","error recuperando year");
        String price = jsonPath.getString("data.price");
        Assert.assertEquals(price,"1849.99","error recuperando price");
        String model = jsonPath.getString("data.'CPU model'");
        Assert.assertEquals(model,"Intel Core i9","error recuperando model");
        String hdSize = jsonPath.getString("data.'Hard disk size'");
        Assert.assertEquals(hdSize,"1 TB","error recuperando hdSize");

    }


}
