package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.ZaloPayCallbackRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.ZaloPayResponse;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.service.ZaloPayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@Validated
@Tag(name = "Payment Controller")
@Slf4j(topic = "PAYMENT_CONTROLLER")
@RequiredArgsConstructor
public class PaymentController {

    private final ZaloPayService zaloPayService;

    @PostMapping("/zalo-pay")
    public ResponseEntity<?> paymentZaloPay(@AuthenticationPrincipal User user) throws IOException {
        log.info("Payment Zalo Pay");

        ZaloPayResponse zaloPayResponse = zaloPayService.createPayment(user.getId());

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Create link payment zalo pay successfully")
                .data(zaloPayResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @PostMapping("/zalo-pay/callback")
    public ResponseEntity<?> paymentZaloPayCallBack(@RequestBody ZaloPayCallbackRequest request) {
        log.info("Payment Zalo Pay CallBack");

        JSONObject jsonObject = zaloPayService.callBack(request);

        return ResponseEntity.ok().body(jsonObject);
    }
    
    @GetMapping("redirect-from-zalopay")
    public ResponseEntity<?> checkRedirectZaloPay(@RequestParam String appid,
                                                  @RequestParam String apptransid,
                                                  @RequestParam(required = false) String pmcid,
                                                  @RequestParam String bankcode,
                                                  @RequestParam String amount,
                                                  @RequestParam String discountamount,
                                                  @RequestParam String status,
                                                  @RequestParam String checksum) throws NoSuchAlgorithmException, URISyntaxException, IOException, InvalidKeyException {
        Map<String, String> data = new HashMap<>();
        data.put("appid", appid);
        data.put("apptransid", apptransid);
        data.put("pmcid", pmcid);
        data.put("bankcode", bankcode);
        data.put("amount", amount);
        data.put("discountamount", discountamount);
        data.put("status", status);
        data.put("checksum", checksum);

        Boolean result = zaloPayService.checkRedirect(data);

        return ResponseEntity.ok().body(result);
    }
}
