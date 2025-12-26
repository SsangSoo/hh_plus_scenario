package kr.hhplus.be.server.member.application.dto;


public record RegisterMemberCommand(
        String name,
        String birthDate,
        String address) {

}

