package com.vinhSeo.BookingCinema.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZaloPayCallbackRequest implements Serializable {

    String data;
    String mac;
    int type; // 1 order; 2 agreement

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ZaloPayCallbackData {

        @JsonProperty("app_id")
        Integer appId;

        @JsonProperty("app_trans_id")
        String appTransId;

        @JsonProperty("app_time")
        Long appTime;

        @JsonProperty("app_user")
        String appUser;

        @JsonProperty("amount")
        Integer amount;

        JsonNode embedData;

        JsonNode item;

        @JsonProperty("zp_trans_id")
        Long zpTransId;

        @JsonProperty("server_time")
        Long serverTime;

        @JsonProperty("channel")
        Integer channel;

        @JsonProperty("merchant_user_id")
        String merchantUserId;

        @JsonProperty("zp_user_id")
        String zpUserId;

        @JsonProperty("user_fee_amount")
        Long userFeeAmount;

        @JsonProperty("discount_amount")
        Long discountAmount;

        @JsonProperty("embed_data")
        public void setEmbedData(String embedData) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();

            this.embedData = mapper.readTree(embedData);
        }

        @JsonProperty("item")
        public void setItem(String item) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();

            this.item = mapper.readTree(item);
        }
    }
}
