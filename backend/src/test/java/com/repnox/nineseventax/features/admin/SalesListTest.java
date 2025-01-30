package com.repnox.nineseventax.features.admin;

import com.repnox.nineseventax.BaseTest;
import com.repnox.nineseventax.features.models.SalesList;
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
public class SalesListTest extends BaseTest {

    private static final String PRODUCT = "Georgia Payment Plan";

    @Test
    public void salesListAPITest() {
        // Read SalesList
        String sessionCookie = loginAdmin();
        SalesList original = getSalesList(sessionCookie);
        assertNotNull(original);

        // Update SalesList
        SalesList toUpdate1 = SerializationUtils.clone(original);
        toUpdate1.setPhoneNumber("111-222-3333");
        toUpdate1.setOnOff(true);
        DateTime startOfDay = DateTime.now().withTimeAtStartOfDay();
        toUpdate1.setStartTime(startOfDay.minusYears(1).toDate());
        toUpdate1.setEndTime(startOfDay.plusYears(1).toDate());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<Void> update1Response =
                restTemplate.exchange(baseURI + "/admin/updateAllSales", HttpMethod.POST, new HttpEntity<>(Lists.newArrayList(toUpdate1), headers), Void.class);

        assertEquals(HttpStatus.OK, update1Response.getStatusCode());
        SalesList updated1 = getSalesList(sessionCookie);
        assertNotNull(updated1);
        assertThat(updated1).isEqualToComparingFieldByFieldRecursively(toUpdate1);

        // Update SalesList to the original values
        SalesList toUpdate2 = SerializationUtils.clone(original);
        toUpdate2.setPhoneNumber(null);
        toUpdate2.setOnOff(false);
        toUpdate2.setStartTime(null);
        toUpdate2.setEndTime(null);

        ResponseEntity<Void> update2Response =
                restTemplate.exchange(baseURI + "/admin/updateAllSales", HttpMethod.POST, new HttpEntity<>(Lists.newArrayList(toUpdate2), headers), Void.class);

        assertEquals(HttpStatus.OK, update2Response.getStatusCode());
        SalesList updated2 = getSalesList(sessionCookie);
        assertNotNull(updated2);
        assertThat(updated2).isEqualToComparingFieldByFieldRecursively(toUpdate2);
    }

    private SalesList getSalesList(String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<List<SalesList>> response =
                restTemplate.exchange(baseURI + "/admin/salesList", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<SalesList>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<SalesList> list = response.getBody();
        return list.stream()
                .filter(next -> PRODUCT.equals(next.getProduct()))
                .findAny().orElse(null);
    }

}
