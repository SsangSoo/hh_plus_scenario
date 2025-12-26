//package kr.hhplus.be.server.domain.member.componenet;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class PasswordEncoderImpl implements PasswordEncoder {
//
////    private final PasswordEncoder encoder = new BCrypt();
//
//    @Override
//    public String encode(CharSequence rawPassword) {
//        return rawPassword + "bbb";
//    }
//
//    @Override
//    public boolean matches(CharSequence rawPassword, String encodedPassword) {
//        return encode(rawPassword).equals(encodedPassword);
//    }
//}