package ru.anonymus.simplemessenger.dto;

public class ResponseInviteCodeDto {

    private String code;

    public ResponseInviteCodeDto(String code) {
        this.code = code;
    }

    public ResponseInviteCodeDto() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
