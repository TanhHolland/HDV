package com.intentionservice.controller;


import com.intentionservice.api.PositionAPI;
import com.intentionservice.api.UserAPI;
import com.intentionservice.controller.bean.MyIntention;
import com.intentionservice.domain.repository.IntentionRepository;
import com.intentionservice.domain.root.Intention;
import com.intentionservice.domain.service.IntentionService;
import com.intentionservice.domain.vo.Customer;
import com.intentionservice.domain.vo.DriverStatusVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController()
@RequestMapping("/api/intentions")
public class IntentionController
{
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private IntentionService intentionService;
    @Autowired
    private PositionAPI positionApi;
    @Autowired
    private IntentionRepository intentionRepository;

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

    @PostMapping("/place")
    public String place(@RequestBody MyIntention myIntention)
    {
        intentionService.placeIntention(myIntention.getUserId(), myIntention.getStartLongitude(),
                myIntention.getStartLatitude(),
                myIntention.getDestLongitude(), myIntention.getDestLatitude());
        return "user " + myIntention.getUserId() + " place intention successfully with start longitude: "
                + myIntention.getStartLongitude() + " and start latitude: " + myIntention.getStartLatitude()
                + " and dest longitude: " + myIntention.getDestLongitude() + " and dest latitude: "
                + myIntention.getDestLatitude();
    }

    @PostMapping("/confirm")
    public boolean confirm(int driverId, int intentionId) throws Exception
    {
        return intentionService.confirmIntention(driverId, intentionId);
    }

    @GetMapping("/all")
    public List<Intention> getAllIntentions() {
        return (List<Intention>) intentionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Intention getIntentionById(@PathVariable int id) {
        return intentionRepository.findById(id).orElse(null);
    }
}