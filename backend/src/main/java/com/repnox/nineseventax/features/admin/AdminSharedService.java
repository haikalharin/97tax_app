package com.repnox.nineseventax.features.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalRefundService;
import com.repnox.nineseventax.features.mailroom.model.Batch;
import com.repnox.nineseventax.features.mailroom.model.BatchRepo;
import com.repnox.nineseventax.features.models.AddressValidateRequest;
import com.repnox.nineseventax.features.models.AddressValidateResponse;
import com.repnox.nineseventax.features.models.LabelEnvelopeRequest;
import com.repnox.nineseventax.features.models.LabelEnvelopeShipment;
import com.repnox.nineseventax.features.models.LabelPackageEnvelopeResponse;
import com.repnox.nineseventax.features.models.LabelPackageRequest;
import com.repnox.nineseventax.features.models.LabelPackageShipment;
import com.repnox.nineseventax.features.models.LabelPackages;
import com.repnox.nineseventax.features.models.LabelShipToAndFrom;
import com.repnox.nineseventax.features.models.LabelWeight;
import com.repnox.nineseventax.features.order.interfaces.MailRoomOrderProcessService;
import com.repnox.nineseventax.features.order.repo.FulfillmentOrder;
import com.repnox.nineseventax.features.payment.response.RefundResponse;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import com.repnox.nineseventax.features.processordersschedule.ProcessOrdersSchedule;
import com.repnox.nineseventax.features.processordersschedule.ProcessOrdersScheduleRepo;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabels;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabelsRepo;
import com.repnox.nineseventax.features.utils.AddressUtil;
import com.repnox.nineseventax.features.utils.AdminUtil;
import com.repnox.nineseventax.features.utils.StringUtility;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.TimerTask;


@Component
@Slf4j
public class AdminSharedService {
    private static final Logger LOG = LoggerFactory.getLogger(AdminSharedService.class);

    public static final long INTERVAL_30MINS = 30 * 60 * 1000;

    @Value("${shipengine.api.key}")
    private String shipEngineApiKey;

    @Value("${shipengine.label.api.uri}")
    private String shipEngineLabelUri;

    @Value("${shipengine.address.api.uri}")
    private String shipEngineAddressUri;

    @Value("${shipengine.label.carrier.id}")
    private String shipEngineLabelCarrierId;

    @Value("${shipengine.label.service.code}")
    private String shipEngineServiceCode;

    @Value("${shipengine.label.serviceDeluxe.code}")
    private String shipEngineServiceDeluxeCode;

    @Autowired
    private AdminUtil adminUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ShippingLabelsRepo shippingLabelsRepo;

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    private PenaltyRecordRepo penaltyRecordRepo;

    private HttpHeaders shipEngineHeaders = new HttpHeaders();

    @Autowired
    private OrbitalRefundService refundService;

    @Autowired
    private ObjectMapper mapper;
    
    private Timer timer;

    @Autowired
    private ProcessOrdersScheduleRepo processOrdersScheduleRepo;

    @Autowired
    private MailRoomOrderProcessService mailRoomOrderProcessService;
    
    @Autowired
    private BatchRepo batchRepo;

    @PostConstruct
    void init() {
        this.shipEngineHeaders.setContentType(MediaType.APPLICATION_JSON);
        this.shipEngineHeaders.set("API-Key", shipEngineApiKey);
//        ScheduledExecutorService scheduledServiceProcessingHoldOrder = Executors.newSingleThreadScheduledExecutor();
//        AdminSharedService selfService = this;

//        scheduledServiceProcessingHoldOrder.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    selfService.processHoldOrders();
//                } catch (Exception e) {
//                    log.error("ERROR: failed when processed hold order", e);
//                }
//            }
//        }, 0, 2, TimeUnit.HOURS);

        Thread runningThread = new Thread(() -> {
            while(true) {
                try {

                    Calendar newCal = Calendar.getInstance();
                    int min = newCal.get(Calendar.MINUTE);
                    int sec = newCal.get(Calendar.SECOND);

                    if(min == 0 || min == 30) {
                        log.debug("[Running Thread]");
                        Thread schedulerThread = new Thread(() -> {
                            try {
                                runScheduler();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        schedulerThread.start();
                    }

                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        runningThread.start();

        //runAutomaticScheduler();
    }

    public void runScheduler() throws Exception {
        SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("HH:mm");
        ProcessOrdersSchedule scheduleInfo = processOrdersScheduleRepo.findById(1);
        if(scheduleInfo == null)
            return;

        Date automatedTime = scheduleInfo.getAutomatedTime();
        Boolean isAutomatic = scheduleInfo.getIsAutomatic();

        if (isAutomatic == null || !isAutomatic) {
            return;
        }

        String[] excludedHolidayArr = scheduleInfo.getExcludedHolidays().split(",");
        List<String> excludedHolidays = ArrayUtils.isNotEmpty(excludedHolidayArr) ? Arrays.asList(excludedHolidayArr) : Collections.EMPTY_LIST;

        Calendar innerCal = Calendar.getInstance();
        Date innerCurrentDate = innerCal.getTime();
        SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
        String innerCurrentDateStr = dateOnlyFormat.format(innerCurrentDate);
        if (excludedHolidays.contains(innerCurrentDateStr)) {
            return;
        }

        if (innerCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || innerCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return;
        }

        String strTimeNow = timeOnlyFormat.format(innerCurrentDate);
        String strAutomatedTime = timeOnlyFormat.format(automatedTime);

        log.debug("Scheduler Task: " + strAutomatedTime + " , " + strTimeNow);

        if(!strTimeNow.equals(strAutomatedTime))
            return;

        ArrayList<Batch> batches = AdminUtil.convertBatch(batchRepo.findAllBatch());
        for (Batch batchItem : batches) {
            Date createdAt = batchItem.getCreatedAt();
            String createdAtStr = dateOnlyFormat.format(createdAt);
            if (createdAtStr != null && createdAtStr.equals(innerCurrentDateStr)) {
                log.debug("Found same batch: " + createdAtStr);
                return;
            }
        }

        mailRoomOrderProcessService.process();
    }

    private void runAutomaticScheduler() {
       
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
        }

        
        Date automatedTime = null;
        Calendar newCal = Calendar.getInstance();
        if(newCal.get(Calendar.MINUTE) < 30) {
            newCal.set(Calendar.MINUTE, 30);
            newCal.set(Calendar.SECOND, 0);
            automatedTime = newCal.getTime();
        } else {
            newCal.add(Calendar.HOUR, 1);
            newCal.set(Calendar.MINUTE, 0);
            newCal.set(Calendar.SECOND, 0);
            automatedTime = newCal.getTime();
        }

        this.timer = new Timer();
        TimerTask scheduledTask = new TimerTask() {
            @Override
            public void run() {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            ProcessOrdersSchedule scheduleInfo = processOrdersScheduleRepo.findById(1);
                            if(scheduleInfo == null)
                                return;

                            Date automatedTime = scheduleInfo.getAutomatedTime();

                            Boolean isAutomatic = scheduleInfo.getIsAutomatic();

                            if (isAutomatic == null || !isAutomatic) {
                                return;
                            }

                            String[] excludedHolidayArr = scheduleInfo.getExcludedHolidays().split(",");
                            List<String> excludedHolidays = ArrayUtils.isNotEmpty(excludedHolidayArr) ? Arrays.asList(excludedHolidayArr) : Collections.EMPTY_LIST;

                            Calendar innerCal = Calendar.getInstance();
                            Date innerCurrentDate = innerCal.getTime();
                            SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String innerCurrentDateStr = dateOnlyFormat.format(innerCurrentDate);
                            if (excludedHolidays.contains(innerCurrentDateStr)) {
                                return;
                            }

                            if (innerCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                                    || innerCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                                return;
                            }

                            SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("HH:mm");

                            String strTimeNow = timeOnlyFormat.format(innerCurrentDate);
                            String strAutomatedTime = timeOnlyFormat.format(automatedTime);

                            log.debug("Scheduler Task: " + strAutomatedTime + " , " + strTimeNow);

                            if(!strTimeNow.equals(strAutomatedTime))
                                return;

                            ArrayList<Batch> batches = AdminUtil.convertBatch(batchRepo.findAllBatch());
                            for (Batch batchItem : batches) {
                                Date createdAt = batchItem.getCreatedAt();
                                String createdAtStr = dateOnlyFormat.format(createdAt);
                                if (createdAtStr != null && createdAtStr.equals(innerCurrentDateStr)) {
                                    return;
                                }
                            }

                            mailRoomOrderProcessService.process();
                        } catch (Exception e) {
                            log.error("ERROR: occurred while running automated fulfillment scheduler", e);
                        }
                    }
                };

                thread.start();
            }
        };
        this.timer.schedule(scheduledTask, automatedTime, INTERVAL_30MINS); // every day interval
    }

    public List<AddressValidateRequest> createAddressValidateData(List<FulfillmentOrder> orders) {

        List<AddressValidateRequest> addressValidateArrayList = new ArrayList<AddressValidateRequest>();
        StringUtility stringUtility = new StringUtility();

        for (int i = 0; i < orders.size(); i++) {
            AddressValidateRequest addressValidate = new AddressValidateRequest();

            String address_line1 = StringUtils.isBlank(orders.get(i).getShippingAddress1()) ? " " : orders.get(i).getShippingAddress1();
            String address_line2 = StringUtils.isBlank(orders.get(i).getShippingAddress2()) ? " " : orders.get(i).getShippingAddress2();

            String city_locality = StringUtils.isBlank(orders.get(i).getShippingCity()) ? " " : orders.get(i).getShippingCity();
            String state_province = StringUtils.isBlank(orders.get(i).getShippingState()) ? " " : orders.get(i).getShippingState();
            String postal_code = StringUtils.isBlank(orders.get(i).getShippingZip()) ? " " : orders.get(i).getShippingZip();
            String phone = StringUtils.isBlank(orders.get(i).getPhone()) ? " " : orders.get(i).getPhone();

            addressValidate.setName(orders.get(i).getId());
            addressValidate.setAddress_line1(stringUtility.ReplaceSpecialCharactersToCharacters(address_line1));
            addressValidate.setAddress_line2(stringUtility.ReplaceSpecialCharactersToCharacters(address_line2));
            addressValidate.setCity_locality(stringUtility.ReplaceSpecialCharactersToCharacters(city_locality));
            addressValidate.setState_province(stringUtility.ReplaceSpecialCharactersToCharacters(state_province));
            addressValidate.setPostal_code(stringUtility.ReplaceSpecialCharactersToCharacters(postal_code));
            addressValidate.setPhone(stringUtility.ReplaceSpecialCharactersToCharacters(phone));
            addressValidate.setCountry_code("US");
            //Gson gson = new Gson();
            //String jsonString = gson.toJson(addressValidate);
            //JSONObject jsonObject = new JSONObject(jsonString);
            addressValidateArrayList.add(addressValidate);
        }

        return addressValidateArrayList;

    }

    public JSONObject createLabelEnvelopeRequestData(FulfillmentOrder order) {

        StateAddress stateAddress = this.adminUtil.getStateAddress(order);

        LabelShipToAndFrom shipTo = new LabelShipToAndFrom();
        shipTo.setName(stateAddress.getCompany());
        shipTo.setCompanyName(stateAddress.getLine());
        shipTo.setAddressLine1(stateAddress.getAddress());
        shipTo.setCityLocality(stateAddress.getCity());
        shipTo.setStateProvience(stateAddress.getState());
        shipTo.setPostalCode(stateAddress.getZip());

        StringUtility stringUtility = new StringUtility();
        LabelShipToAndFrom shipFrom = new LabelShipToAndFrom();

        String firstName = StringUtils.isBlank(order.getFirstName()) ? " " : order.getFirstName();
        firstName = stringUtility.ReplaceSpecialCharactersToCharacters(firstName);
        String lastName = StringUtils.isBlank(order.getLastName()) ? " " : order.getLastName();
        lastName = stringUtility.ReplaceSpecialCharactersToCharacters(lastName);

        String address_line1 = StringUtils.isBlank(order.getShippingAddress1()) ? " " : order.getShippingAddress1();
        String address_line2 = StringUtils.isBlank(order.getShippingAddress2()) ? " " : order.getShippingAddress2();
        String city_locality = StringUtils.isBlank(order.getShippingCity()) ? " " : order.getShippingCity();
        String state_province = StringUtils.isBlank(order.getShippingState()) ? " " : order.getShippingState();
        String postal_code = StringUtils.isBlank(order.getShippingZip()) ? " " : order.getShippingZip();
        String phone = StringUtils.isBlank(order.getPhone()) ? " " : order.getPhone();
        String processingSpeed = StringUtils.isBlank(order.getProcessingSpeed()) ? " " : order.getProcessingSpeed();

        shipFrom.setName(firstName.concat(" ").concat(lastName));
        shipFrom.setAddressLine1(stringUtility.ReplaceSpecialCharactersToCharacters(address_line1));
        shipFrom.setAddressLine2(stringUtility.ReplaceSpecialCharactersToCharacters(address_line2));
        shipFrom.setCityLocality(stringUtility.ReplaceSpecialCharactersToCharacters(city_locality));
        shipFrom.setStateProvience(stringUtility.ReplaceSpecialCharactersToCharacters(state_province));
        shipFrom.setPostalCode(stringUtility.ReplaceSpecialCharactersToCharacters(postal_code));
        shipFrom.setPhone(stringUtility.ReplaceSpecialCharactersToCharacters(phone));

        LabelWeight weight = new LabelWeight();
        weight.setUnit("ounce");
        weight.setValue(1);
        LabelPackages pack = new LabelPackages();
        pack.setPackageCode("large_envelope_or_flat");
        pack.setWeight(weight);

        LabelEnvelopeShipment shipment = new LabelEnvelopeShipment();
        shipment.setCarrierId(shipEngineLabelCarrierId);
        shipment.setExternalOrderId(order.getId());
        shipment.setShipTo(shipTo);
        shipment.setShipFrom(shipFrom);
        shipment.setServiceCode(shipEngineServiceCode);
        if (processingSpeed.equals("Deluxe")) {
            LabelWeight weight_ = new LabelWeight();
            weight_.setUnit("ounce");
            weight_.setValue(3);
            LabelPackages pack_ = new LabelPackages();
            pack_.setPackageCode("package");
            pack_.setWeight(weight_);
            shipment.setPackages(pack_);
        } else {
            shipment.setPackages(pack);
        }
        LabelEnvelopeRequest labelEnvelopRequest = new LabelEnvelopeRequest();
        labelEnvelopRequest.setShipment(shipment);
        Gson gson = new Gson();
        String jsonString = gson.toJson(labelEnvelopRequest);
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject;
    }

    public String createLabelPackageRequestData(FulfillmentOrder order) {

        LabelShipToAndFrom shipFrom = new LabelShipToAndFrom();
        shipFrom.setName("97tax.com");
        shipFrom.setPhone("813-853-0140");
        shipFrom.setAddressLine1("1044 E Brandon Blvd");
        shipFrom.setCityLocality("Brandon");
        shipFrom.setStateProvience("FL");
        shipFrom.setPostalCode("33511");

        StringUtility stringUtility = new StringUtility();
        LabelShipToAndFrom shipTo = new LabelShipToAndFrom();

        String firstName = StringUtils.isBlank(order.getFirstName()) ? " " : order.getFirstName();
        firstName = stringUtility.ReplaceSpecialCharactersToCharacters(firstName);
        String lastName = StringUtils.isBlank(order.getLastName()) ? " " : order.getLastName();
        lastName = stringUtility.ReplaceSpecialCharactersToCharacters(lastName);

        String address_line1 = StringUtils.isBlank(order.getShippingAddress1()) ? " " : order.getShippingAddress1();
        String address_line2 = StringUtils.isBlank(order.getShippingAddress2()) ? " " : order.getShippingAddress2();
        String city_locality = StringUtils.isBlank(order.getShippingCity()) ? " " : order.getShippingCity();
        String state_province = StringUtils.isBlank(order.getShippingState()) ? " " : order.getShippingState();
        String postal_code = StringUtils.isBlank(order.getShippingZip()) ? " " : order.getShippingZip();
        String phone = StringUtils.isBlank(order.getPhone()) ? " " : order.getPhone();
        String processingSpeed = StringUtils.isBlank(order.getProcessingSpeed()) ? " " : order.getProcessingSpeed();

        shipTo.setName(firstName.concat(" ").concat(lastName));
        shipTo.setPhone(stringUtility.ReplaceSpecialCharactersToCharacters(phone));
        shipTo.setAddressLine1(stringUtility.ReplaceSpecialCharactersToCharacters(address_line1));
        shipTo.setAddressLine2(stringUtility.ReplaceSpecialCharactersToCharacters(address_line2));
        shipTo.setCityLocality(stringUtility.ReplaceSpecialCharactersToCharacters(city_locality));
        shipTo.setStateProvience(stringUtility.ReplaceSpecialCharactersToCharacters(state_province));
        shipTo.setPostalCode(stringUtility.ReplaceSpecialCharactersToCharacters(postal_code));

        LabelWeight weight = new LabelWeight();
        weight.setUnit("ounce");
        weight.setValue(3);
        LabelPackages pack = new LabelPackages();
        pack.setPackageCode("package");
        pack.setWeight(weight);

        LabelPackageShipment shipment = new LabelPackageShipment();
        shipment.setCarrierId(shipEngineLabelCarrierId);
        shipment.setExternalOrderId(order.getId());
        shipment.setShipFrom(shipFrom);
        shipment.setShipTo(shipTo);

        if (processingSpeed.equals("Deluxe")) {
            LabelWeight weight_ = new LabelWeight();
            weight_.setUnit("ounce");
            weight_.setValue(1);
            LabelPackages pack_ = new LabelPackages();
            pack_.setPackageCode("large_envelope_or_flat");
            pack_.setWeight(weight_);
            shipment.setPackages(pack_);
            shipment.setServiceCode(shipEngineServiceDeluxeCode);
        } else {
            shipment.setPackages(pack);
            shipment.setServiceCode(shipEngineServiceCode);
        }

        LabelPackageRequest labelPackageRequest = new LabelPackageRequest();
        labelPackageRequest.setShipment(shipment);
        Gson gson = new Gson();
        String jsonString = gson.toJson(labelPackageRequest);
//        JSONObject jsonObject = new JSONObject(jsonString);
//        return jsonObject;

        return jsonString;
    }

    private HttpRequestWithBody postUnirestHandler(String uri) {
        return Unirest.post(uri)
                .header("Content-Type", "application/json")
                .header("API-Key", shipEngineApiKey);
    }

    public AddressValidateResponse[] postAddressValidateRequest(List<FulfillmentOrder> orders) throws Exception {
        AddressValidateResponse[] responseAddressValidateEntity = null;
        try {
//            this.shipEngineHeaders.setContentType(MediaType.APPLICATION_JSON);
//            List<JSONObject> addressValidateRequest = this.createAddressValidateData(orders);
//            HttpEntity<String> addressValidateRequestEntity = new HttpEntity<>(addressValidateRequest.toString(), this.shipEngineHeaders);
//            responseAddressValidateEntity = restTemplate.postForObject(shipEngineAddressUri, addressValidateRequestEntity, AddressValidateResponse[].class);
//            return responseAddressValidateEntity;

            List<AddressValidateRequest> addressValidateRequest = this.createAddressValidateData(orders);
            log.debug("address validate request = " + mapper.writeValueAsString(addressValidateRequest));
            HttpResponse<String> response = postUnirestHandler(shipEngineAddressUri).body(mapper.writeValueAsString(addressValidateRequest))
                    .asString();
            //log.debug("address validate response = " + response.getBody());

            Gson gson = new Gson();
            AddressValidateResponse[] result = gson.fromJson(response.getBody(), AddressValidateResponse[].class);
            return result;

        } catch (Exception e) {
            LOG.error("ERROR: PostAddressValidateRequestError " + mapper.writeValueAsString(responseAddressValidateEntity), e);
            throw e;
        }
    }

    public LabelPackageEnvelopeResponse postLabelEnvelopeRequest(FulfillmentOrder order) throws Exception {
        LabelPackageEnvelopeResponse responselabelEnvelopRequest = null;
        try {
//            this.shipEngineHeaders.setContentType(MediaType.APPLICATION_JSON);
//            JSONObject labelEnvelopRequest = this.createLabelEnvelopeRequestData(order);
//            HttpEntity<String> labelEnvelopRequestEntity = new HttpEntity<String>(labelEnvelopRequest.toString(), this.shipEngineHeaders);
//            responselabelEnvelopRequest = restTemplate.postForObject(shipEngineLabelUri, labelEnvelopRequestEntity, LabelPackageEnvelopeResponse.class);
//            return responselabelEnvelopRequest;

            JSONObject labelEnvelopRequest = this.createLabelEnvelopeRequestData(order);
            log.debug("label envelop request = " + labelEnvelopRequest.toString());
            HttpResponse<String> response = postUnirestHandler(shipEngineLabelUri).body(labelEnvelopRequest.toString())
                    .asString();
            //log.debug("label envelop response = " + response.getBody());

            Gson gson = new Gson();
            LabelPackageEnvelopeResponse result = gson.fromJson(response.getBody(), LabelPackageEnvelopeResponse.class);
            return result;

        } catch (Exception e) {
            LOG.error("ERROR: PostLabelEnvelopeError " + mapper.writeValueAsString(responselabelEnvelopRequest), e);
            throw e;
        }
    }

    public LabelPackageEnvelopeResponse postLabelPackageRequest(FulfillmentOrder order) {
//        this.shipEngineHeaders.setContentType(MediaType.APPLICATION_JSON);
//        JSONObject labelPackageRequest = this.createLabelPackageRequestData(order);
//        HttpEntity<String> labelPackageRequestEntity = new HttpEntity<String>(labelPackageRequest.toString(), this.shipEngineHeaders);
//        LabelPackageEnvelopeResponse responselabelPackageRequest = restTemplate.postForObject(shipEngineLabelUri, labelPackageRequestEntity, LabelPackageEnvelopeResponse.class);
//        return responselabelPackageRequest;

        String labelPackageRequest = this.createLabelPackageRequestData(order);
        log.debug("label package request = " + labelPackageRequest);
        HttpResponse<String> response = postUnirestHandler(shipEngineLabelUri).body(labelPackageRequest)
                .asString();
        //log.debug("label package response = " + response.getBody());

        Gson gson = new Gson();
        LabelPackageEnvelopeResponse result = gson.fromJson(response.getBody(), LabelPackageEnvelopeResponse.class);
        return result;
    }

    public void mapLabelResponseToShippinLabel(Long orderId, LabelPackageEnvelopeResponse LabelResponse, ShippingLabels shippingLabels) {

        shippingLabels.setOrderId(orderId);
        shippingLabels.setLabelId(LabelResponse.getLabel_id());
        shippingLabels.setStatus(LabelResponse.getStatus());
        shippingLabels.setShipmentId(LabelResponse.getShipment_id());
        shippingLabels.setShipDate(LabelResponse.getShip_date());
        shippingLabels.setCreatedAt(LabelResponse.getCreated_at());
        shippingLabels.setTrackingNumber(LabelResponse.getTracking_number());
        shippingLabels.setCarrierId(LabelResponse.getCarrier_id());
        shippingLabels.setServiceCode(LabelResponse.getService_code());
        shippingLabels.setPackageCode(LabelResponse.getPackage_code());
        shippingLabels.setLabelFormat(LabelResponse.getLabel_format());
        shippingLabels.setLabelLayout(LabelResponse.getLabel_layout());
        shippingLabels.setTrackingStatus(LabelResponse.getTracking_status());

    }

    public void postShippingLabelsVoid(String labelId) {
//        this.shipEngineHeaders.setContentType(MediaType.TEXT_PLAIN);
//        HttpEntity<Void> request = new HttpEntity<Void>(null, this.shipEngineHeaders);
//        String shipEngineVoidUri = shipEngineLabelUri.concat("/").concat(labelId).concat("/").concat("void");
//        restTemplate.put(shipEngineVoidUri, request);

        String shipEngineVoidUri = shipEngineLabelUri.concat("/").concat(labelId).concat("/").concat("void");

        log.debug("shipping labels void uri = " + shipEngineVoidUri);
        HttpResponse<String> response = Unirest.post(shipEngineVoidUri)
            .header("Content-Type", "text/plain")
            .header("API-Key", shipEngineApiKey).asString();

        log.debug("shipping labels void response = " + response.getBody());
    }

    public void setShipingLabelAsVoid(ShippingLabels label) {
        if (label.getLabelId() != null && !label.getLabelId().isEmpty()) {
            try {
                postShippingLabelsVoid(label.getLabelId());
                label.setVoidStatus("success");
            } catch (Exception e) {
                label.setVoidStatus("failure");
            }
            shippingLabelsRepo.save(label);
        }
        if (label.getTrackingNumber() != null && label.getOrderId() != null) {
            orderRecordRepo.updateShippingTrackingByOrderId("", "", label.getOrderId());
        }
    }

    private void processHoldOrders() throws Exception {
        List<OrderRecord> staleOnHoldOrders = orderRecordRepo.findStaleOnHoldOrders();
        if (staleOnHoldOrders != null && staleOnHoldOrders.size() > 0) {
            for (int i = 0; i < staleOnHoldOrders.size(); i++) {
                Long currentOrderNum = staleOnHoldOrders.get(i).getOrderNum();
                Optional<OrderRecord> optRecord = orderRecordRepo.findById(currentOrderNum);
                if (optRecord.isPresent()) {
                    OrderRecord order = optRecord.get();
                    refundService.refundPayment(order.getAuthorizeTransactionId(), order.getAmount(), OrderRecord.STATUS_CANCELLED, true);
                }
            }
        }

        List<PenaltyOrder> staleOnHoldPenaltyOrders = penaltyRecordRepo.findStaleOnHoldOrders();
        if (staleOnHoldPenaltyOrders != null && staleOnHoldPenaltyOrders.size() > 0) {
            for (int i = 0; i < staleOnHoldPenaltyOrders.size(); i++) {
                Long currentOrderNum = staleOnHoldPenaltyOrders.get(i).getId();
                Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(currentOrderNum);
                if (optRecord.isPresent()) {
                    PenaltyOrder order = optRecord.get();
                    refundService.refundPayment(order.getAuthorizeTransactionId(), order.getAmount(), OrderRecord.STATUS_CANCELLED, true);
                }
            }
        }
    }
}