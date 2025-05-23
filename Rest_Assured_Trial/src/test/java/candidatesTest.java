import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class candidatesTest {

	static String sessionCookie;
	static int candidateId;

	@Test
	@Order(1)
	public void testLogin() {
		Response loginResponse = RestAssured.given().contentType("application/json")
				.formParam("username", "Admin").formParam("password", "admin123")
				.post("https://opensource-demo.orangehrmlive.com/web/index.php/auth/validate");
		String redirectUrl = loginResponse.getHeader("Location");
		Response redirectedResponse = RestAssured.given().cookie("orangehrm", sessionCookie).get(redirectUrl);
		sessionCookie = redirectedResponse.getCookie("orangehrm");
		assertEquals(200, redirectedResponse.statusCode(), "Expected 200 OK after redirect");
	}

	@Test
	@Order(2)
	public void testAddCandidate() {
		Response addCandidateResponse = RestAssured.given()
			    .contentType("application/json")
			    .header("Cookie", "orangehrm=" + sessionCookie)
			    .formParam("firstName", "John")
			    .formParam("lastName", "Doe")
			    .formParam("email", "john.doe@example.com")
			    .formParam("vacancyId", "1")
			    .post("https://opensource-demo.orangehrmlive.com/web/index.php/api/v2/recruitment/candidates");
		System.out.println("Status Code: " + addCandidateResponse.statusCode());
		System.out.println("Response Body: " + addCandidateResponse.getBody().asString());
		assertEquals(200, addCandidateResponse.statusCode(), "Candidate creation should return 200 OK");
		candidateId = addCandidateResponse.jsonPath().getInt("data.id");
	}

	@Test
	@Order(3)
	public void testDeleteCandidate() {
		assertTrue(candidateId > 0, "Candidate must be added before deletion");
		Response deleteResponse = RestAssured.given().header("Cookie", "orangehrm=" + sessionCookie).delete(
				"https://opensource-demo.orangehrmlive.com/web/index.php/api/v2/recruitment/candidates/{id}",
				candidateId);
		assertEquals(200, deleteResponse.statusCode(), "Candidate deletion should return 200 OK");
		System.out.println("Delete Response: " + deleteResponse.getBody().asString());
	}
}
