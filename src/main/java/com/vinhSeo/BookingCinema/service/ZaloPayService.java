package com.vinhSeo.BookingCinema.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vinhSeo.BookingCinema.dto.request.TicketDetailRequest;
import com.vinhSeo.BookingCinema.dto.request.ZaloPayCallbackRequest;
import com.vinhSeo.BookingCinema.dto.response.ZaloPayResponse;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.RedisTicket;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.repository.RedisTicketRepository;
import com.vinhSeo.BookingCinema.repository.UserRepository;
import com.vinhSeo.BookingCinema.utils.zalopay.HMACUtil;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j(topic = "ZALO_PAY_SERVICE")
@RequiredArgsConstructor
public class ZaloPayService {

    @Value("${zalo-pay.app_id}")
    private String APP_ID;

    @Value("${zalo-pay.key1}")
    private String KEY1;

    @Value("${zalo-pay.key2}")
    private String KEY2;

    @Value("${zalo-pay.endpoint}")
    private String ENDPOINT;

    @Value("${zalo-pay.redirect_url}")
    private String REDIRECT_URL;

    @Value("${zalo-pay.callback_url}")
    private String CALLBACK_URL;

    private final RedisTicketRepository redisTicketRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, JsonNode> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisShowTimeSeatService redisShowTimeSeatService;

    private RedisTicket findRedisTicket(Integer userId) {
        log.info("Find Redis Ticket by userId: {}", userId);

        return redisTicketRepository.findById(userId).orElseThrow(() -> new AppException(ErrorApp.REDIS_TICKET_NOT_FOUND));
    }

    public ZaloPayResponse createPayment(Integer userId) throws IOException {

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorApp.USER_NOT_FOUND));
        String username = user.getUsername();
        String phone = user.getPhone();

        // get info previous ticket is held seats
        RedisTicket  redisTicket = findRedisTicket(userId);

        String orderCode = redisTicket.getTransCode();
        Integer amount = redisTicket.getPrice();
        Integer showTimeId = redisTicket.getShowTimeId();
        List<TicketDetailRequest> ticketDetailRequests = redisTicket.getTicketDetailRequests();

        JSONArray items = new JSONArray();
        items.put(showTimeId);
        for(TicketDetailRequest ticketDetailRequest : ticketDetailRequests) {
            JSONObject item = new JSONObject();

            item.put("showTimeSeat", ticketDetailRequest.getShowTimeSeatId());

            items.put(item);
        }

        JSONObject embed_data = new JSONObject();
        embed_data.put("preferred_payment_method", new ArrayList<>());
        embed_data.put("redirecturl", REDIRECT_URL); // url return after payment // test with ...

        Map<String, Object> order = new HashMap<>() {{
            put("app_id", APP_ID);
            put("app_user", username); // put username or userId
            put("app_trans_id", orderCode);
            put("app_time", System.currentTimeMillis());
            put("expire_duration_seconds", 900);
            put("amount", amount); // price
            put("title", "Dat ve xem phim online");
            put("description", "Booking Cinema - Thanh toan don hang #" + orderCode);
            put("embed_data", embed_data);
            put("callback_url", CALLBACK_URL);
            put("bank_code", "");
            put("phone", phone); // phone of user
            put("address", ""); // address of user
            put("item", items);
        }};

        String data = order.get("app_id") +"|"+ order.get("app_trans_id") +"|"+
                order.get("app_user") +"|"+ order.get("amount") +"|"+
                order.get("app_time") +"|"+ order.get("embed_data") +"|"+ order.get("item");
        order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, KEY1, data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ENDPOINT + "/create");

        List<NameValuePair> params = new ArrayList<>();
        for(Map.Entry<String, Object> entry:order.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }

        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder responseData = new StringBuilder();

        String line;
        while((line = rd.readLine()) != null) responseData.append(line);

        ObjectMapper mapper = new ObjectMapper();
        ZaloPayResponse zaloPayResponse = mapper.readValue(responseData.toString(), ZaloPayResponse.class);

        // handle redis ticket: refresh time (5p) for payment
        if(zaloPayResponse.getReturnCode() == 1) {
            String key = "redis_ticket:" + redisTicket.getUserId();
            redisTemplate.expire(key, 300, TimeUnit.SECONDS);
            redisTicketRepository.save(redisTicket);
        }

        return zaloPayResponse;
    }

    public JSONObject callBack(ZaloPayCallbackRequest request) {
        log.info("Call Back to zalopay server to merchant server");

        JSONObject jsonObject = new JSONObject();

        try {
            ObjectMapper mapper = new ObjectMapper();

            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            hmacSHA256.init(new SecretKeySpec(KEY2.getBytes(), "HmacSHA256"));

            ZaloPayCallbackRequest.ZaloPayCallbackData data = mapper.readValue(
                    request.getData(), ZaloPayCallbackRequest.ZaloPayCallbackData.class);

            String dataStr = request.getData();
            String reqmac = request.getMac();
            byte[] hashBytes = hmacSHA256.doFinal(dataStr.getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();


            // check callback
            if(!reqmac.equals(mac)) {
                // callback invalid
                jsonObject.put("return_code", -1);
                jsonObject.put("return_message", "mac not equal");
            } else {
                // payment success

                String username = data.getAppUser();
                User user = userRepository.findByUsername(username);

                RedisTicket redisTicket = redisTicketRepository.findById(user.getId()).orElseThrow(() ->
                        new AppException(ErrorApp.REDIS_TICKET_NOT_FOUND));

                ObjectNode node = mapper.createObjectNode();
                node.put("userId", redisTicket.getUserId());
                node.put("transitionId", redisTicket.getTransCode());
                node.put("price", redisTicket.getPrice());
                node.put("showTimeId", redisTicket.getShowTimeId());
                node.put("paymentStatus", 1);

                ArrayNode arrayNode = mapper.createArrayNode();
                for (TicketDetailRequest ticketDetailRequest:redisTicket.getTicketDetailRequests()) {
                    arrayNode.add(ticketDetailRequest.getShowTimeSeatId());
                }

                node.put("ticketDetailRequests", arrayNode);
                kafkaTemplate.send("PAYMENT_SUCCESS_TOPIC", node);

                log.info("Payment successfully by username: {}, with amount: {}", data.getAppUser(), data.getAmount());

                jsonObject.put("return_code", 1);
                jsonObject.put("return_message", "success");
            }
        } catch (Exception ex) {
            jsonObject.put("return_code", 0); // callback again (up to 3 times)
            jsonObject.put("return_message", ex.getMessage());
        }

        return jsonObject;
    }

    public Boolean checkRedirect(Map<String, String> data) throws InvalidKeyException, NoSuchAlgorithmException, URISyntaxException, IOException {
        log.info("Check redirect Zalopay");
        String checksumData = data.get("appid") +"|"+ data.get("apptransid") +"|"+ data.get("pmcid") +"|"+
                data.get("bankcode") +"|"+ data.get("amount") +"|"+ data.get("discountamount") +"|"+ data.get("status");

        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        hmacSHA256.init(new SecretKeySpec(KEY2.getBytes(), "HmacSHA256"));

        byte[] checksumBytes = hmacSHA256.doFinal(checksumData.getBytes());
        String checksum = DatatypeConverter.printHexBinary(checksumBytes).toLowerCase();

        if(checksum.equals(data.get("checksum"))) {
            String appTransId = data.get("apptransid");
            RedisTicket redisTicket = redisTicketRepository.findByBookingId(appTransId);

            if(redisTicket.getPaymentStatus() != 1) {
                log.info("Payment status not success");

                JsonNode result = orderStatus(appTransId);

                Integer returnCode = result.get("return_code").asInt();

                if(returnCode == 1) {
                    redisTicket.setPaymentStatus(0);

                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode node = mapper.createObjectNode();
                    node.put("userId", redisTicket.getUserId());
                    node.put("transitionId", redisTicket.getTransCode());
                    node.put("price", redisTicket.getPrice());
                    node.put("showTimeId", redisTicket.getShowTimeId());
                    node.put("paymentStatus", 1);

                    ArrayNode arrayNode = mapper.createArrayNode();
                    for (TicketDetailRequest ticketDetailRequest:redisTicket.getTicketDetailRequests()) {
                        arrayNode.add(ticketDetailRequest.getShowTimeSeatId());
                    }

                    node.put("ticketDetailRequests", arrayNode);
                    kafkaTemplate.send("PAYMENT_SUCCESS_TOPIC", node);
                } else {
                    log.info("Payment successfully");

                    // delete redis ticket
                    redisTicketRepository.deleteById(redisTicket.getUserId());

                    // delete all show time seat
                    redisShowTimeSeatService.deleteAllHeldSeatsByUser(redisTicket.getUserId());
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public JsonNode orderStatus(String appTransId) throws URISyntaxException, IOException {
        String data = APP_ID + "|" + appTransId + "|" + KEY1;
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, KEY1, data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("app_id", APP_ID));
        params.add(new BasicNameValuePair("app_trans_id", appTransId));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(ENDPOINT + "/query");
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri.build());
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        return new ObjectMapper().readTree(resultJsonStr.toString());
    }
}
