package com.dianping.swallow.web.util;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author mingdongli
 *         <p/>
 *         2015年6月12日下午5:44:19
 */
@JsonSerialize(using = ResponseStatusSerializer.class)
public enum ResponseStatus {

    SWITCHOFF("switch off", -24), EMPTYARGU("empty argument", -23), NODEFAULT("no default config", -22), INVALIDLENGTH("invalid length", -21), INVALIDTYPE(
            "invalid mongo type", -20), NOTEXIST("not exist", -19), NOCONSUMERSERVER("not find consumer server", -18), LIONEXCEPTION(
            "config lion error", -17), INVALIDIP("invalid ip mapping", -16), HTTPEXCEPTION("http request error", -15), TOOLARGEQPS(
            "exceed max qps", -14), NODISKSPACE("no extra disk space", -13), TOOLARGEQUOTA("exceed quota", -12), INVALIDTOPICNAME(
            "invalid topic name", -11), INVALIDTOPIC("no such topic", -10), TOPICBLANK("topic blank", -9), IOEXCEPTION(
            "io exception", -8), RUNTIMEEXCEPTION("runtime exception", -7), INTERRUPTEDEXCEPTION(
            "interrupted exception", -6), PARSEEXCEPTION("parse error", -5), EMPTYCONTENT("empty content", -4), NOAUTHENTICATION(
            "no authenticaton", -3), UNAUTHENTICATION("unauthorized, please promote authority through workflow", -2), MONGOWRITE(
            "write mongo error", -1), SUCCESS("success", 0), TRY_MONGOWRITE("read time out", 1);

    private String message;

    private int status;

    private ResponseStatus(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public ResponseStatus setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ResponseStatus setStatus(int status) {
        this.status = status;
        return this;
    }

    public static ResponseStatus findByStatus(int status) {
        for (ResponseStatus code : values()) {
            if (status == code.getStatus()) {
                return code;
            }
        }
        throw new RuntimeException("Error status : " + status);
    }

    @Override
    public String toString() {
        return this.message + "_" + this.status;
    }

}
