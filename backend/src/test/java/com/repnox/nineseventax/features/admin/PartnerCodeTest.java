package com.repnox.nineseventax.features.admin;

import com.repnox.nineseventax.BaseTest;
import com.repnox.nineseventax.features.partnercode.model.PartnerCode;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class PartnerCodeTest extends BaseTest {

    @Test
    public void partnerCodeCRUDTest() {
        // Create PartnerCode
        String sessionCookie = loginAdmin();
        String code = "partner" + System.currentTimeMillis();
        PartnerCode toCreate = PartnerCode.builder()
                .code(code).notes("notes here")
                .percentageOff(5f).commissionTypeId(1).commission(5)
                .partnerName("jane5off").contactName("Jane")
                .streetAddress("13th Street").city("New York").state("NY").zip("10001")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<PartnerCode> response =
                restTemplate.exchange(baseURI + "/admin/partnercodes", HttpMethod.POST, new HttpEntity<>(toCreate, headers), PartnerCode.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PartnerCode created = response.getBody();
        PartnerCode expected = SerializationUtils.clone(toCreate);
        Long codeNum = created.getCodeNum();
        expected.setCodeNum(codeNum);
        assertThat(created).isEqualToComparingFieldByFieldRecursively(expected);

        // Read PartnerCode
        PartnerCode theCode = getPartnerCode(sessionCookie, code);
        assertNotNull(theCode);
        assertThat(theCode).isEqualToComparingFieldByFieldRecursively(expected);

        // Update PartnerCode
        String newCode = code + "_new";
        PartnerCode toUpdate = PartnerCode.builder()
                .code(newCode).notes("updated notes here")
                .percentageOff(10f).commissionTypeId(2).commission(10)
                .partnerName("john10off").contactName("John")
                .streetAddress("17th Street").city("Los Angeles").state("CA").zip("90001")
                .build();

        ResponseEntity<PartnerCode> updateResponse =
                restTemplate.exchange(baseURI + "/admin/partnercodes/" + codeNum, HttpMethod.PUT, new HttpEntity<>(toUpdate, headers), PartnerCode.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        PartnerCode updated = updateResponse.getBody();
        expected = SerializationUtils.clone(toUpdate);
        expected.setCodeNum(codeNum);
        assertThat(updated).isEqualToComparingFieldByFieldRecursively(expected);

        // Read updated PartnerCode
        theCode = getPartnerCode(sessionCookie, newCode);
        assertNotNull(theCode);
        assertThat(theCode).isEqualToComparingFieldByFieldRecursively(expected);

        // Delete PartnerCode
        ResponseEntity<Void> deleteResponse =
                restTemplate.exchange(baseURI + "/admin/partnercodes/" + codeNum, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // Read deleted PartnerCode
        theCode = getPartnerCode(sessionCookie, newCode);
        assertNull(theCode);
    }

    private PartnerCode getPartnerCode(String sessionCookie, String theCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        ResponseEntity<List<PartnerCode>> response =
                restTemplate.exchange(baseURI + "/admin/partnercodes", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<PartnerCode>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PartnerCode> codes = response.getBody();
        return codes.stream()
                .filter(next -> theCode.equals(next.getCode()))
                .findAny().orElse(null);
    }

}
