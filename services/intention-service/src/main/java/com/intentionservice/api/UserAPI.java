package com.intentionservice.api;

import com.intentionservice.domain.vo.Customer;
import com.intentionservice.domain.vo.DriverVo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class UserAPI
{
    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "userCircuitBreaker", fallbackMethod = "customerFallback")
    public Customer findById(Integer id)
    {
        Map ret = restTemplate.getForObject("http://UC-SERVICE/users/" + id, Map.class);
        Customer cus = new Customer();
        cus.setCustomerId(id);
        cus.setCustomerMobile(String.valueOf(ret.get("mobile")));
        cus.setCustomerName(String.valueOf(ret.get("userName")));
        cus.setUserType(String.valueOf(ret.get("type")));
        return cus;
    }

    @CircuitBreaker(name = "driverCircuitBreaker", fallbackMethod = "driverFallback")
    public DriverVo findDriverById(Integer id)
    {
        Map ret = restTemplate.getForObject("http://UC-SERVICE/users/" + id, Map.class);
        DriverVo driVo = new DriverVo();
        driVo.setId(id);
        driVo.setMobile(String.valueOf(ret.get("mobile")));
        driVo.setUserName(String.valueOf(ret.get("userName")));
        driVo.setType(String.valueOf(ret.get("type")));
        return driVo;
    }

    public Customer customerFallback(Integer id, Throwable throwable)
    {
        Customer customer = new Customer();
        customer.setCustomerId(-1);
        customer.setCustomerName("default username");
        customer.setCustomerMobile("0000");
        return customer;
    }

    public DriverVo driverFallback(Integer id, Throwable throwable)
    {
        DriverVo driverVo = new DriverVo();
        driverVo.setId(-1);
        driverVo.setMobile("0000");
        driverVo.setUserName("default driver");
        driverVo.setType("default");
        return driverVo;
    }
}