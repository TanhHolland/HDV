package com.intentionservice.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intentionservice.api.PositionAPI;
import com.intentionservice.api.UserAPI;
import com.intentionservice.controller.bean.IntentionVo;
import com.intentionservice.domain.repository.CandidateRepository;
import com.intentionservice.domain.repository.IntentionRepository;
import com.intentionservice.domain.root.Intention;
import com.intentionservice.domain.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@Service
public class IntentionService
{
    @Autowired
    IntentionRepository intentionRepository;
    @Autowired
    UserAPI userApi;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    PositionAPI positionApi;
    @Autowired
    CandidateRepository candidateRepository;
    @Autowired
    LockService lockService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RabbitTemplate rabbitTemplate;


    private DelayQueue<IntentionTask> intentions = new DelayQueue<>();

    private static Candidate fromDriverStatus(DriverStatusVo driverStatusVo, Intention intention)
    {
        Candidate candidate = new Candidate();
        candidate.setDriverId(driverStatusVo.getDId());
        candidate.setCreated(new Date());
        candidate.setDriverMobile(driverStatusVo.getDriver().getMobile());
        candidate.setDriverName(driverStatusVo.getDriver().getUserName());
        candidate.setLongitude(driverStatusVo.getCurrentLongitude());
        candidate.setLatitude(driverStatusVo.getCurrentLatitude());
        candidate.setIntention(intention);
        return candidate;
    }

    @Transactional
    public void placeIntention(int userId, Double startLongitude, Double startLatitude,
                               Double destLongitude, Double destLatitude)
    {
        Customer customer = userApi.findById(userId);
        Intention intention = new Intention()
                .setStartLongitude(startLongitude)
                .setStartLatitude(startLatitude)
                .setDestLongitude(destLongitude)
                .setDestLatitude(destLatitude)
                .setCustomer(customer)
                .setStatus(IntentionStatus.Inited);
        intentionRepository.save(intention);
        IntentionTask task = new IntentionTask(intention.getMid(), 2L, TimeUnit.SECONDS, 0);

        intentions.put(task);
    }

    @Transactional
    public void sendNotification(Collection<DriverStatusVo> result, Intention intention)
    {
        result.stream()
                .map(vo -> fromDriverStatus(vo, intention))
                .forEach(candidate -> candidateRepository.save(candidate));
        intention.waitingConfirm();
        intentionRepository.save(intention);
    }

    @Transactional
    public boolean confirmIntention(int driverId, int intentionId) throws Exception
    {
        String lockName = "intention" + intentionId;
        Lock lock = null;
        try
        {
            lock = lockService.create(lockName);
            Intention intention = intentionRepository.findById(intentionId).orElse(null);
            DriverVo driverVo = userApi.findDriverById(driverId);
            int ret = intention.confirmIntention(driverVo);
            if (ret == 0)
            {
                intentionRepository.save(intention);
                IntentionVo intentionVo = new IntentionVo().setIntentionId(intention.getMid())
                        .setCustomerId(intention.getCustomer().getCustomerId())
                        .setDestLat(intention.getDestLatitude())
                        .setDestLong(intention.getDestLongitude())
                        .setStartLong(intention.getStartLongitude())
                        .setStartLat(intention.getStartLatitude())
                        .setDriverId(intention.getSelectedDriver().getId());
                try
                {
                    rabbitTemplate.convertAndSend("intention", objectMapper.writeValueAsString(intentionVo));
                }
                catch (JsonProcessingException e)
                {
                    System.out.println("IntentionService: Error");
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception e)
        {
            System.out.println("IntentionService: Error");
            return false;
        }
        finally
        {
            if (lock != null)
            {
                lockService.release(lockName, lock.getValue());
            }
        }
    }

    @Transactional
    public void matchFail(Intention intention)
    {
        intention.fail();
        intentionRepository.save(intention);
    }

    @Async
    public void handleTask()
    {
        for (; ; )
        {
            try
            {
                IntentionTask task = intentions.take();
                if (task != null)
                {
                    Intention intention = intentionRepository.findById(task.getIntenionId()).orElse(null);
                    if (intention.canMatchDriver())
                    {
                        Collection<DriverStatusVo> result = positionApi.match(intention.getStartLongitude(), intention.getStartLatitude());
                        if (result.size() > 0)
                        {
                            List<String> names = result.stream().map(s -> s.getDriver().getUserName()).collect(toList());
                            sendNotification(result, intention);
                        }
                        else
                        {
                            retryMatch(task, intention);
                        }
                    }
                    else
                    {
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean retryMatch(IntentionTask task, Intention intention)
    {
        int times = task.getRepeatTimes() + 1;
        if (times > 5)
        {
            matchFail(intention);
            return true;
        }
        IntentionTask newTask = new IntentionTask(intention.getMid(), 2L * times,
                TimeUnit.SECONDS, times);
        this.intentions.put(newTask);
        return false;
    }
}