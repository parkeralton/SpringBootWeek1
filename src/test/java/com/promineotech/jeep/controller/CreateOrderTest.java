package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import com.promineotech.jeep.controller.support.CreateOrderTestSupport;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Order;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
    "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, config = @SqlConfig(encoding = "utf-8"))

class CreateOrderTest extends CreateOrderTestSupport {

  @LocalServerPort
  private int serverPort;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testCreateOrderReturnsSuccess201() {
    String body = createOrderBody();
    String uri = String.format("http://localhost:%d/orders", serverPort);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

    ResponseEntity<Order> response =
        restTemplate.exchange(uri, HttpMethod.POST, bodyEntity, Order.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    Order order = response.getBody();

    assertThat(order.getCustomer().getCustomerId()).isEqualTo("ROTH_GARTH");
    assertThat(order.getModel().getModelID()).isEqualTo(JeepModel.WRANGLER);
    assertThat(order.getModel().getTrimLevel()).isEqualTo("Sport");
    assertThat(order.getModel().getNumDoors()).isEqualTo(4);
    assertThat(order.getColor().getColorId()).isEqualTo("EXT_DIAMOND_BLACK");
    assertThat(order.getEngine().getEngineId()).isEqualTo("1_3_TURBO");
    assertThat(order.getTire().getTireId()).isEqualTo("37_YOKOHAMA");
    assertThat(order.getOptions()).hasSize(3);


  }

  private String createOrderBody() {
    // @formatter:off
    String body = "{\n"
        + " \"customer\":\"ROTH_GARTH\",\n"
        + " \"model\":\"WRANGLER\",\n"
        + " \"trim\":\"Sport\",\n"
        + " \"doors\":4,\n"
        + " \"color\":\"EXT_DIAMOND_BLACK\",\n"
        + " \"engine\":\"1_3_TURBO\",\n"
        + " \"tire\":\"37_YOKOHAMA\",\n"
        + " \"options\":[\n"
        + "     \"DOOR_MOPAR_REINFORCE\",\n"
        + "     \"EXT_TACTIK_FRONT\",\n"
        + "     \"EXT_MOPAR_CAMERA\"\n"
        + " ]\n"
        + "}";
    // @formatter:on
    return body;
  }

}
