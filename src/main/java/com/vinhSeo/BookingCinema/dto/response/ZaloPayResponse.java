package com.vinhSeo.BookingCinema.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZaloPayResponse implements Serializable {

    @JsonProperty("return_code")
    Integer returnCode;

    @JsonProperty("return_message")
    String returnMessage;

    @JsonProperty("sub_return_code")
    Integer subReturnCode;

    @JsonProperty("sub_return_message")
    String subReturnMessage;

    @JsonProperty("zp_trans_token")
    String zpTransToken;

    @JsonProperty("order_url")
    String orderUrl;

    @JsonProperty("order_token")
    String orderToken;
}
