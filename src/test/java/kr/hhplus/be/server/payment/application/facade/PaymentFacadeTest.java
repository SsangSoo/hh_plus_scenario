package kr.hhplus.be.server.payment.application.facade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PaymentFacadeTest {

    @Test
    @DisplayName("String subString Test")
    void subStringTest() {
        String str = "결제완료:1:2";
        String substring = str.substring(5);
        String[] split = substring.split(":");

        String split1 = split[0];
        String split2 = split[1];

        assertThat(split1).isEqualTo("1");
        assertThat(split2).isEqualTo("2");
    }

}