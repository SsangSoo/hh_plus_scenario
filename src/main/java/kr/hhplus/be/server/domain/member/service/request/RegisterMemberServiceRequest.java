package kr.hhplus.be.server.domain.member.service.request;


public record RegisterMemberServiceRequest(
        String name,
        String birthDate,
        String address) {

}

