package com.intentionservice.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Lock implements Comparable<Lock> {
    private final String name;
    private final String value;
    private final Date expires;

    @Override
    public int compareTo(Lock other) {
        return expires.compareTo(other.expires);
    }
}