package com.intentionservice.controller;


import com.intentionservice.api.PositionAPI;
import com.intentionservice.api.UserAPI;
import com.intentionservice.controller.bean.MyIntention;
import com.intentionservice.domain.service.IntentionService;
import com.intentionservice.domain.vo.Customer;
import com.intentionservice.domain.vo.DriverStatusVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class IntentionController
{
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private IntentionService intentionService;
    @Autowired
    private PositionAPI positionApi;

    @GetMapping("/user/{id}")
    public Customer findById(@PathVariable Integer id)
    {
        return this.userAPI.findById(id);
    }

    @GetMapping("/position/match")
    public Collection<DriverStatusVo> match(double longitude, double latitude)
    {
        return this.positionApi.match(longitude, latitude);
    }

    @PostMapping("/intentions/place")
    public void place(@RequestBody MyIntention myIntention)
    {
        intentionService.placeIntention(myIntention.getUserId(), myIntention.getStartLongitude(),
                myIntention.getStartLatitude(),
                myIntention.getDestLongitude(), myIntention.getDestLatitude());
    }

    @PostMapping("/intention/confirm")
    public boolean confirm(int driverId, int intentionId) throws Exception
    {
        return intentionService.confirmIntention(driverId, intentionId);
    }
}
