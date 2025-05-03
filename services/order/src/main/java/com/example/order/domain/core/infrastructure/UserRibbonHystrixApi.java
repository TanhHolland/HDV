package com.example.order.domain.core.infrastructure;

import com.example.order.domain.core.vo.CustomerVo;
import com.example.order.domain.core.vo.DriverVo;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class UserRibbonHystrixApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRibbonHystrixApi.class);

    private RestTemplate restTemplate;

    /**
     * Use @HystrixCommand annotation to specify the method to call when this method throws an exception
     *
     * @param id customerId
     * @return User found by id
     */
    @HystrixCommand()
    public CustomerVo findCustomerById(Integer id) {
        Map ret = restTemplate.getForObject("http://QBIKE-UC/users/" + id, Map.class);
        CustomerVo customerVo = new CustomerVo();
        customerVo.setCustomerId(id);
        customerVo.setCustomerMobile(String.valueOf(ret.get("mobile")));
        customerVo.setCustomerName(String.valueOf(ret.get("userName")));
        return customerVo;
    }

    @HystrixCommand()
    public DriverVo findDriverById(Integer id) {
        Map ret = restTemplate.getForObject("http://QBIKE-UC/users/" + id, Map.class);
        DriverVo driverVo = new DriverVo();
        driverVo.setDriverId(id);
        driverVo.setDriverMobile(String.valueOf(ret.get("mobile")));
        driverVo.setDriverName(String.valueOf(ret.get("userName")));
        return driverVo;
    }

    /**
     * Hystrix fallback method
     *
     * @param id customerId
     * @return Default user
     */
    public CustomerVo fallback(Integer id) {
        LOGGER.info("Exception occurred, entering fallback method, received parameter: customerId = {}", id);
        CustomerVo customer = new CustomerVo();
        customer.setCustomerId(-1);
        customer.setCustomerName("default username");
        customer.setCustomerMobile("0000");
        return customer;
    }

    public DriverVo fallbackDriver(Integer id) {
        LOGGER.info("Exception occurred, entering fallback method, received parameter: customerId = {}", id);
        DriverVo driverVo = new DriverVo();
        driverVo.setDriverId(-1);
        driverVo.setDriverName("default");
        driverVo.setDriverMobile("0000");
        return driverVo;
    }
}