package com.intentionservice.domain.service;
import com.intentionservice.domain.vo.Lock;

public interface LockService {
    Lock create(String name) throws Exception;
    boolean release(String name, String value) throws Exception;
}