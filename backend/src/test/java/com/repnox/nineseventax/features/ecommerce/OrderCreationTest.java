package com.repnox.nineseventax.features.ecommerce;

import com.repnox.nineseventax.BaseTest;
import com.repnox.nineseventax.model.Address;
import com.repnox.nineseventax.model.OrderData;
import com.repnox.nineseventax.util.OrderCreator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class OrderCreationTest extends BaseTest {

    private OrderCreator orderCreator;

    @PostConstruct
    private void initialize() {
        orderCreator = OrderCreator.instantiateFor(this);
    }

    @Test
    public void successPaymentTest() throws IOException {
        OrderData validOrder = orderCreator.prepareNext()
                .fullyValid()
                .create();

        assertEquals(OrderRecord.STATUS_PROCESSING, validOrder.getStatus());
    }

    @Test
    public void invalidPaymentTest() throws IOException {
        OrderData invalidOrder = orderCreator.prepareNext()
                .fullyValid().withValidPayment(false)
                .create();

        assertEquals(OrderRecord.STATUS_FAILED, invalidOrder.getStatus());
    }

    @Test
    public void invalidAddressTest() throws IOException {
        Address address = Address.builder()
                .address1("Definitely Invalid Blvd")
                .city("San Jose").state("CA").postalCode("78756")
                .build();
        OrderData invalidOrder = orderCreator.prepareNext()
                .withAddress(address).withValidPayment(true)
                .create();

        assertEquals(OrderRecord.STATUS_PROCESSING, invalidOrder.getStatus());
    }

}
