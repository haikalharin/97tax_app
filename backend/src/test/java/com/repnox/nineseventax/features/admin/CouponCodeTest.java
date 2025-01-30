package com.repnox.nineseventax.features.admin;

import com.repnox.nineseventax.BaseTest;
import com.repnox.nineseventax.features.couponcode.model.CouponCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CouponCodeTest extends BaseTest {

    @Test
    public void couponCodeCRUDTest() {
        // Create CouponCode
        String sessionCookie = loginAdmin();
        String code = "coupon" + System.currentTimeMillis();
        CouponCode toCreate = CouponCode.builder()
                .code(code).notes("notes here")
                .percentageOff(5f).commissionTypeId(1).commission(5)
                .partnerName("jane5off").contactName("Jane")
                .streetAddress("13th Street").city("New York").state("NY").zip("10001")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<CouponCode> response =
                restTemplate.exchange(baseURI + "/admin/couponcodes", HttpMethod.POST, new HttpEntity<>(toCreate, headers), CouponCode.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        CouponCode created = response.getBody();
        CouponCode expected = SerializationUtils.clone(toCreate);
        Long codeNum = created.getCodeNum();
        expected.setCodeNum(codeNum);
        assertThat(created).isEqualToComparingFieldByFieldRecursively(expected);

        // Read CouponCode
        CouponCode theCode = getCouponCode(sessionCookie, code);
        assertNotNull(theCode);
        assertThat(theCode).isEqualToComparingFieldByFieldRecursively(expected);

        // Update CouponCode
        String newCode = code + "_new";
        CouponCode toUpdate = CouponCode.builder()
                .code(newCode).notes("updated notes here")
                .percentageOff(10f).commissionTypeId(2).commission(10)
                .partnerName("john10off").contactName("John")
                .streetAddress("17th Street").city("Los Angeles").state("CA").zip("90001")
                .build();

        ResponseEntity<CouponCode> updateResponse =
                restTemplate.exchange(baseURI + "/admin/couponcodes/" + codeNum, HttpMethod.PUT, new HttpEntity<>(toUpdate, headers), CouponCode.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        CouponCode updated = updateResponse.getBody();
        expected = SerializationUtils.clone(toUpdate);
        expected.setCodeNum(codeNum);
        assertThat(updated).isEqualToComparingFieldByFieldRecursively(expected);

        // Read updated CouponCode
        theCode = getCouponCode(sessionCookie, newCode);
        assertNotNull(theCode);
        assertThat(theCode).isEqualToComparingFieldByFieldRecursively(expected);

        // Delete CouponCode
        ResponseEntity<Void> deleteResponse =
                restTemplate.exchange(baseURI + "/admin/couponcodes/" + codeNum, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // Read deleted CouponCode
        theCode = getCouponCode(sessionCookie, newCode);
        assertNull(theCode);
    }

    private CouponCode getCouponCode(String sessionCookie, String theCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<List<CouponCode>> response =
                restTemplate.exchange(baseURI + "/admin/couponcodes", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<CouponCode>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<CouponCode> codes = response.getBody();
        return codes.stream()
                .filter(next -> theCode.equals(next.getCode()))
                .findAny().orElse(null);
    }

}
