package com.repnox.nineseventax.features.admin;

import com.repnox.nineseventax.BaseTest;
import com.repnox.nineseventax.common.PaginationResponse;
import com.repnox.nineseventax.features.admin.request.VoidRequest;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.mailroom.model.Batch;
import com.repnox.nineseventax.features.utils.DefaultResponse;
import com.repnox.nineseventax.model.Address;
import com.repnox.nineseventax.model.OrderData;
import com.repnox.nineseventax.util.OrderCreator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderFulfilmentTest extends BaseTest {

    private OrderCreator orderCreator;
    private String sessionCookie;

    @PostConstruct
    private void initialize() {
        orderCreator = OrderCreator.instantiateFor(this);
        sessionCookie = loginAdmin();
    }

    @Test
    public void validOrderOnHoldTest() throws IOException {
        OrderData validOrder = orderCreator.prepareNext()
                .fullyValid()
                .create();
        assertEquals(OrderRecord.STATUS_PROCESSING, validOrder.getStatus());
        long orderNum = Long.parseLong(validOrder.getId());

        // Put On Hold
        onHoldRequest(orderNum, "PUT ON HOLD");
        OrderRecord theOrder = getOrder(orderNum);
        assertEquals(OrderRecord.STATUS_ON_HOLD, theOrder.getStatus());

        // Remove Hold
        onHoldRequest(orderNum, "REMOVE HOLD");
        theOrder = getOrder(orderNum);
        assertEquals(OrderRecord.STATUS_PROCESSING, theOrder.getStatus());
    }

    @Test
    public void invalidOrderOnHoldTest() throws IOException {
        OrderData invalidOrder = orderCreator.prepareNext()
                .fullyValid().withValidPayment(false)
                .create();
        assertEquals(OrderRecord.STATUS_FAILED, invalidOrder.getStatus());
        long orderNum = Long.parseLong(invalidOrder.getId());

        // Put On Hold
        onHoldRequest(orderNum, "PUT ON HOLD");
        OrderRecord theOrder = getOrder(orderNum);
        assertEquals(OrderRecord.STATUS_ON_HOLD, theOrder.getStatus());

        // Remove Hold
        onHoldRequest(orderNum, "REMOVE HOLD");
        theOrder = getOrder(orderNum);
        assertEquals(OrderRecord.STATUS_PROCESSING, theOrder.getStatus());
    }

    @Test
    public void deleteOrdersTest() throws IOException {
        OrderData validOrder = orderCreator.prepareNext()
                .fullyValid()
                .create();
        assertEquals(OrderRecord.STATUS_PROCESSING, validOrder.getStatus());
        long orderNum1 = Long.parseLong(validOrder.getId());

        OrderData invalidOrder = orderCreator.prepareNext()
                .fullyValid().withValidPayment(false)
                .create();
        assertEquals(OrderRecord.STATUS_FAILED, invalidOrder.getStatus());
        long orderNum2 = Long.parseLong(invalidOrder.getId());

        // Delete orders
        DeleteListRequest toDelete = new DeleteListRequest();
        toDelete.setIds(Arrays.asList(orderNum1, orderNum2));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<Void> deleteResponse =
                restTemplate.exchange(baseURI + "/admin/orders/delete", HttpMethod.POST, new HttpEntity<>(toDelete, headers), Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        OrderRecord theOrder1 = getOrder(orderNum1);
        assertEquals(OrderRecord.STATUS_DELETED, theOrder1.getStatus());
        OrderRecord theOrder2 = getOrder(orderNum2);
        assertEquals(OrderRecord.STATUS_DELETED, theOrder2.getStatus());
    }

    @Test
    public void cancelOrderTest() throws IOException {
        OrderData validOrder = orderCreator.prepareNext()
                .fullyValid()
                .create();
        assertEquals(OrderRecord.STATUS_PROCESSING, validOrder.getStatus());
        long orderNum = Long.parseLong(validOrder.getId());

        // Cancel transaction and refund
        OrderRecord theOrder = getOrder(orderNum);
        cancelRequest(orderNum, theOrder.getAuthorizeTransactionId());

        theOrder = getOrder(orderNum);
        assertEquals(OrderRecord.STATUS_CANCELLED, theOrder.getStatus());
    }

    @Test
    public void searchOrdersBasicTest() throws IOException {
        OrderData validOrder = orderCreator.prepareNext()
                .fullyValid()
                .create();
        assertEquals(OrderRecord.STATUS_PROCESSING, validOrder.getStatus());
        String email1 = validOrder.getEmail();
        long orderNum1 = Long.parseLong(validOrder.getId());

        OrderData invalidOrder = orderCreator.prepareNext()
                .fullyValid().withValidPayment(false)
                .create();
        assertEquals(OrderRecord.STATUS_FAILED, invalidOrder.getStatus());
        String email2 = invalidOrder.getEmail();
        long orderNum2 = Long.parseLong(invalidOrder.getId());

        // Searching by email
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<PaginationResponse<OrderData>> emailSearchResponse =
                restTemplate.exchange(baseURI + "/admin/orders?" +
                        "pageNumber=0&pageSize=20&orderBy=createdDate&orderDirection=DESC" +
                        "&query.isCalifornia=false&query.isNewJersey=false&query.isGeorgia=false" +
                        "&query.isIllinois=false&query.isMichigan=false&query.status.any=true" +
                        "&query.email=" + email1, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<PaginationResponse<OrderData>>() {
                });

        assertEquals(HttpStatus.OK, emailSearchResponse.getStatusCode());
        PaginationResponse<OrderData> pageResult1 = emailSearchResponse.getBody();
        OrderData emailOrder = pageResult1.getRows().get(0); // Only one order will be presented in the results
        assertEquals(orderNum1, Long.parseLong(emailOrder.getId()));

        // Searching by status
        ResponseEntity<PaginationResponse<OrderData>> statusSearchResponse =
                restTemplate.exchange(baseURI + "/admin/orders?" +
                        "pageNumber=0&pageSize=20&orderBy=createdDate&orderDirection=DESC" +
                        "&query.isCalifornia=false&query.isNewJersey=false&query.isGeorgia=false" +
                        "&query.isIllinois=false&query.isMichigan=false&query.status.failed=true", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<PaginationResponse<OrderData>>() {
                });

        assertEquals(HttpStatus.OK, statusSearchResponse.getStatusCode());
        PaginationResponse<OrderData> pageResult2 = statusSearchResponse.getBody();
        List<OrderData> statusOrders = pageResult2.getRows(); // Multiple orders will be returned, from other tests
        OrderData statusOrder = statusOrders.stream().filter(next -> Long.parseLong(next.getId()) == orderNum2)
                .findFirst().get();
        assertNotNull(statusOrder);
        assertEquals(orderNum2, Long.parseLong(statusOrder.getId()));
    }

    @Test
    public void ordersFulfilmentTest() throws IOException {
        // Valid order
        OrderData order1 = orderCreator.prepareNext()
                .fullyValid()
                .create();
        assertEquals(OrderRecord.STATUS_PROCESSING, order1.getStatus());
        long orderNum1 = Long.parseLong(order1.getId());

        // Order with invalid address
        Address invalidAddress = Address.builder()
                .address1("Definitely Invalid Blvd")
                .city("San Jose").state("CA").postalCode("78756")
                .build();
        OrderData order2 = orderCreator.prepareNext()
                .withAddress(invalidAddress).withValidPayment(true)
                .create();
        assertEquals(OrderRecord.STATUS_PROCESSING, order2.getStatus());
        long orderNum2 = Long.parseLong(order2.getId());

        // Doing fulfilment: order1's address will be validated and will be added to the batch, order2 - not
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<Map<String, Object>> processOrdersResponse =
                restTemplate.exchange(baseURI + "/v2/admin/processorders", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<Map<String, Object>>() {
                });
        assertEquals(HttpStatus.OK, processOrdersResponse.getStatusCode());
        List<Map<String, Object>> processedOrders = (List<Map<String, Object>>) processOrdersResponse.getBody().get("processedOrders");
        String versionId = (String) processedOrders.get(0).get("versionId");

        // Fetch invalid order and check it is On Hold
        OrderRecord theOrder2 = getOrder(orderNum2);
        assertEquals(OrderRecord.STATUS_ON_HOLD, theOrder2.getStatus());

        // Sent to Mail Room
        ResponseEntity<Void> sentResponse =
                restTemplate.exchange(baseURI + "/admin/uploadingmailroom/" + versionId, HttpMethod.POST, new HttpEntity<>(headers), Void.class);
        assertEquals(HttpStatus.OK, sentResponse.getStatusCode());

        // Fetch batches
        String mailRoomSessionCookie = loginMailRoom();
        HttpHeaders mailRoomHeaders = new HttpHeaders();
        mailRoomHeaders.add("Cookie", mailRoomSessionCookie);

        ResponseEntity<List<Batch>> batchesResponse =
                restTemplate.exchange(baseURI + "/mailroom/mailRoomBatches", HttpMethod.GET, new HttpEntity<>(mailRoomHeaders), new ParameterizedTypeReference<List<Batch>>() {
                });
        assertEquals(HttpStatus.OK, batchesResponse.getStatusCode());
        List<Batch> batches = batchesResponse.getBody();
        assertFalse(batches.isEmpty());
        long latestId = batches.stream()
                .map(batch -> batch.getId())
                .max(Long::compare).get();

        // Download the PDF files for the latest batch
        ResponseEntity labelsResponse =
                restTemplate.exchange(baseURI + "/mailroom/downloadLabels/" + latestId, HttpMethod.GET, new HttpEntity<>(mailRoomHeaders), String.class);
        assertEquals(HttpStatus.OK, labelsResponse.getStatusCode());
        ResponseEntity pdfsResponse =
                restTemplate.exchange(baseURI + "/mailroom/downloadPDF/" + latestId, HttpMethod.GET, new HttpEntity<>(mailRoomHeaders), String.class);
        assertEquals(HttpStatus.OK, pdfsResponse.getStatusCode());

        // Fetch valid order and validate
        // ShipEngine sandbox has limitation of 20 requests/minute - so it is not returning labels from time to time.
        // TODO: Need to mock ShipEngine calls to "generate" labels when needed.
        // OrderRecord theOrder1 = getOrder(orderNum1);
        // assertEquals(OrderRecord.STATUS_COMPLETE, theOrder1.getStatus());
    }

    private void onHoldRequest(long orderNum, String content) {
        OnHoldRequest onHoldRequest = OnHoldRequest.builder()
                .orderNum(orderNum).sendNotification(false)
                .content(content).type("SYSTEM").user("admin")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<Void> onHoldResponse =
                restTemplate.exchange(baseURI + "/admin/order/id/" + orderNum + "/onhold", HttpMethod.PUT, new HttpEntity<>(onHoldRequest, headers), Void.class);

        assertEquals(HttpStatus.OK, onHoldResponse.getStatusCode());
    }

    private DefaultResponse cancelRequest(long orderNum, String authTransactionId) {
        VoidRequest voidRequest = VoidRequest.builder()
                .orderNum(orderNum).sendNotification(false)
                .content("CANCEL AND REFUND").type("SYSTEM").user("admin")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<DefaultResponse> onHoldResponse =
                restTemplate.exchange(baseURI + "/v2/admin/transaction/" + authTransactionId + "/void", HttpMethod.PUT, new HttpEntity<>(voidRequest, headers), DefaultResponse.class);

        assertEquals(HttpStatus.OK, onHoldResponse.getStatusCode());
        DefaultResponse response = onHoldResponse.getBody();
        assertEquals("S001", response.getResponseCode());
        return response;
    }

    private OrderRecord getOrder(long orderNum) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<OrderRecord> getResponse =
                restTemplate.exchange(baseURI + "/admin/order/id/" + orderNum, HttpMethod.GET, new HttpEntity<>(headers), OrderRecord.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        return getResponse.getBody();
    }

}
