package com.repnox.nineseventax.features.admin;

import com.repnox.nineseventax.BaseTest;
import com.repnox.nineseventax.features.models.CurrentProductPrices;
import com.repnox.nineseventax.features.models.ProductsList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class ProductsListTest extends BaseTest {

    private static final String PRODUCT = "Georgia Payment Plan";
    private static final String SUB_OPTIONS = "express";

    @Test
    public void productsListAPITest() {
        // Read ProductsList
        String sessionCookie = loginAdmin();
        ProductsList original = getProductsList(sessionCookie);
        assertNotNull(original);

        // Update ProductsList
        ProductsList toUpdate1 = SerializationUtils.clone(original);
        toUpdate1.setCurrentPrice(25);
        toUpdate1.setChangePrice(26);
        DateTime startOfDay = DateTime.now().withTimeAtStartOfDay();
        toUpdate1.setTimePeriodsStart(startOfDay.minusYears(1).toDate());
        toUpdate1.setTimePeriodsEnd(startOfDay.plusYears(1).toDate());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<Void> update1Response =
                restTemplate.exchange(baseURI + "/admin/updateAllPrice", HttpMethod.POST, new HttpEntity<>(Lists.newArrayList(toUpdate1), headers), Void.class);

        assertEquals(HttpStatus.OK, update1Response.getStatusCode());
        ProductsList updated1 = getProductsList(sessionCookie);
        assertNotNull(updated1);
        assertThat(updated1).isEqualToComparingFieldByFieldRecursively(toUpdate1);

        // Update ProductsList prices to the original values
        ProductsList toUpdate2 = SerializationUtils.clone(original);
        toUpdate1.setCurrentPrice(15);
        toUpdate1.setChangePrice(16);

        ResponseEntity<Void> update2Response =
                restTemplate.exchange(baseURI + "/admin/updateAllPrice", HttpMethod.POST, new HttpEntity<>(Lists.newArrayList(toUpdate2), headers), Void.class);

        assertEquals(HttpStatus.OK, update2Response.getStatusCode());
        ProductsList updated2 = getProductsList(sessionCookie);
        assertNotNull(updated2);
        assertThat(updated2).isEqualToComparingFieldByFieldRecursively(toUpdate2);
    }

    @Test
    public void currentProductPricesTest() {
        String name = "IRS Payment Plan";
        ResponseEntity<CurrentProductPrices> response =
                restTemplate.exchange(baseURI + "/admin/productPrice?product_name=" + name, HttpMethod.GET, null, CurrentProductPrices.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        CurrentProductPrices prices = response.getBody();
        assertNotNull(prices);
        // all prices are presented for the IRS Payment Plan
        assertThat(prices.getCustomEnrollmentFee()).isGreaterThan(0f);
        assertThat(prices.getEnrollmentFee()).isGreaterThan(0f);
        assertThat(prices.getCustomChangeOfAddressFee()).isGreaterThan(0f);
        assertThat(prices.getCustomExpressOptionFee()).isGreaterThan(0f);
        assertThat(prices.getCustomPayrollFee()).isGreaterThan(0f);
    }

    private ProductsList getProductsList(String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<List<ProductsList>> response =
                restTemplate.exchange(baseURI + "/admin/productPrices", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductsList>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ProductsList> list = response.getBody();
        return list.stream()
                .filter(next -> PRODUCT.equals(next.getProduct()) && SUB_OPTIONS.equals(next.getSubOptions()))
                .findAny().orElse(null);
    }

}
