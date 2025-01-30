package com.repnox.nineseventax.shipengine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.repnox.nineseventax.features.models.AddressValidateRequest;
import com.repnox.nineseventax.features.models.AddressValidateResponse;
import com.repnox.nineseventax.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class AddressValidationTest extends BaseTest {

    @Value("${shipengine.address.api.uri}")
    private String shipEngineAddressUri;

    @Value("${shipengine.api.key}")
    private String shipEngineApiKey;

    protected final String ADDRESS_VALIDATION_REQUEST = TEST_RESOURCES + "/json/shipengine/addressValidation.json";

    @Test
    public void addressValidationTest() throws IOException {
        // Just a pair of addresses to be used within tests, valid and error one
        // Documentation (with some typos) available here: https://www.shipengine.com/docs/addresses/validation/
        File addressesJson = new File(ADDRESS_VALIDATION_REQUEST);
        List<AddressValidateRequest> addresses = mapper.readValue(addressesJson, new TypeReference<List<AddressValidateRequest>>() {
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("API-Key", shipEngineApiKey);
        HttpEntity request = new HttpEntity<>(addresses, headers);
        ResponseEntity<List<AddressValidateResponse>> response =
                restTemplate.exchange(shipEngineAddressUri, HttpMethod.POST, request, new ParameterizedTypeReference<List<AddressValidateResponse>>() {
                });

        List<AddressValidateResponse> result = response.getBody();
        AddressValidateResponse verified = result.stream()
                .filter(next -> next.getOriginal_address().getName().equals("1_verified"))
                .findAny().orElse(null);
        assertNotNull(verified);
        assertEquals("verified", verified.getStatus());

        AddressValidateResponse error = result.stream()
                .filter(next -> next.getOriginal_address().getName().equals("2_error"))
                .findAny().orElse(null);
        assertNotNull(error);
        assertEquals("error", error.getStatus());
    }

}
