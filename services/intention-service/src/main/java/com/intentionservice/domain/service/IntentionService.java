package com.intentionservice.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intentionservice.api.PositionAPI;
import com.intentionservice.api.UserAPI;
import com.intentionservice.controller.bean.IntentionVo;
import com.intentionservice.domain.exeption.ValidationException;
import com.intentionservice.domain.repository.CandidateRepository;
import com.intentionservice.domain.repository.IntentionRepository;
import com.intentionservice.domain.root.Intention;
import com.intentionservice.domain.vo.*;
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
public class IntentionService {
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

    @Autowired
    ValidationService validationService;

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
        try {
            validationService.validateIntentionRequest(userId, startLongitude, startLatitude, destLongitude, destLatitude);

            Customer customer = userApi.findById(userId);

            if (customer == null || customer.getCustomerId() <= 0) {
                throw new ValidationException("Người dùng không tồn tại");
            }

            Intention intention = new Intention()
                    .setStartLongitude(startLongitude)
                    .setStartLatitude(startLatitude)
                    .setDestLongitude(destLongitude)
                    .setDestLatitude(destLatitude)
                    .setCustomer(customer)
                    .setStatus(IntentionStatus.Inited);

            intentionRepository.save(intention);

            if (intention.getMid() <= 0) {
                throw new ValidationException("Failed to save intention");
            }

            IntentionTask task = new IntentionTask(intention.getMid(), 2L, TimeUnit.SECONDS, 0);
            intentions.put(task);

            System.out.println("Successfully placed intention with ID: " + intention.getMid());
        } catch (ValidationException e) {
            System.out.println("Validation error when placing intention: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Error placing intention: " + e.getMessage());
            throw new RuntimeException("Failed to place intention: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void sendNotification(Collection<DriverStatusVo> result, Intention intention) {
        try {
            if (result == null || intention == null) {
                throw new ValidationException("Invalid notification data: result or intention is null");
            }

            result.stream()
                    .map(vo -> fromDriverStatus(vo, intention))
                    .forEach(candidate -> candidateRepository.save(candidate));

            intention.waitingConfirm();
            intentionRepository.save(intention);

            System.out.println("Sent notification for intention ID: " + intention.getMid() + " to " + result.size() + " drivers");
        } catch (Exception e) {
            System.out.println("Error sending notification: " + e.getMessage());
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }

    @Transactional
    public boolean confirmIntention(int driverId, int intentionId) throws Exception {
        try {
            validationService.validateConfirmationRequest(driverId, intentionId);
        } catch (ValidationException e) {
            System.out.println("Validation error when confirming intention: " + e.getMessage());
            throw e;
        }

        String lockName = "intention" + intentionId;
        Lock lock = null;
        try {
            lock = lockService.create(lockName);

            Intention intention = intentionRepository.findById(intentionId).orElse(null);
            if (intention == null) {
                throw new ValidationException("Intention not found with ID: " + intentionId);
            }

            DriverVo driverVo = userApi.findDriverById(driverId);
            if (driverVo == null || driverVo.getId() <= 0) {
                throw new ValidationException("Driver not found or invalid driver data returned");
            }

            int ret = intention.confirmIntention(driverVo);
            System.out.println("Confirm intention result: " + ret + " for intentionId: " + intentionId + ", driverId: " + driverId);

            if (ret == 0) {
                intentionRepository.save(intention);
                IntentionVo intentionVo = new IntentionVo()
                        .setIntentionId(intention.getMid())
                        .setCustomerId(intention.getCustomer().getCustomerId())
                        .setDestLat(intention.getDestLatitude())
                        .setDestLong(intention.getDestLongitude())
                        .setStartLong(intention.getStartLongitude())
                        .setStartLat(intention.getStartLatitude())
                        .setDriverId(intention.getSelectedDriver().getId());

                try {
                    rabbitTemplate.convertAndSend("intention", objectMapper.writeValueAsString(intentionVo));
                    System.out.println("Successfully sent message to RabbitMQ for intention ID: " + intentionId);
                } catch (JsonProcessingException e) {
                    System.out.println("Error processing JSON: " + e.getMessage());
                }
                return true;
            } else {
                switch (ret) {
                    case -1:
                        System.out.println("Cannot confirm intention: intention is not in Unconfirmed state");
                        break;
                    case -2:
                        System.out.println("Cannot confirm intention: driver is not a candidate");
                        break;
                    case -3:
                        System.out.println("Cannot confirm intention: a driver is already selected");
                        break;
                    default:
                        System.out.println("Cannot confirm intention: unknown reason, code: " + ret);
                }
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error confirming intention: " + e.getMessage());
            throw e;
        } finally {
            if (lock != null) {
                lockService.release(lockName, lock.getValue());
            }
        }
    }

    @Transactional
    public void matchFail(Intention intention) {
        try {
            if (intention == null) {
                throw new ValidationException("Cannot mark match as failed: intention is null");
            }

            intention.fail();
            intentionRepository.save(intention);
            System.out.println("Marked intention ID: " + intention.getMid() + " as failed");
        } catch (Exception e) {
            System.out.println("Error marking match as failed: " + e.getMessage());
            throw new RuntimeException("Failed to mark match as failed: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleTask() {
        System.out.println("Starting intention task handler");
        for (;;) {
            try {
                IntentionTask task = intentions.take();
                if (task != null) {
                    Intention intention = intentionRepository.findById(task.getIntenionId()).orElse(null);
                    if (intention == null) {
                        System.out.println("Intention not found for task: " + task);
                        continue;
                    }

                    if (intention.canMatchDriver()) {
                        if (intention.getStartLongitude() == null || intention.getStartLatitude() == null) {
                            System.out.println("Invalid coordinates for intention ID: " + intention.getMid());
                            matchFail(intention);
                            continue;
                        }

                        Collection<DriverStatusVo> result = positionApi.match(
                                intention.getStartLongitude(), intention.getStartLatitude());

                        if (result != null && result.size() > 0) {
                            List<String> names = result.stream()
                                    .map(s -> s.getDriver().getUserName())
                                    .collect(toList());
                            System.out.println("Found " + result.size() + " drivers for intention ID: " + intention.getMid() + ": " + names);
                            sendNotification(result, intention);
                        } else {
                            System.out.println("No drivers found for intention ID: " + intention.getMid() + ", retrying...");
                            retryMatch(task, intention);
                        }
                    } else {
                        System.out.println("Intention ID: " + intention.getMid() + " is no longer eligible for matching, status: " + intention.getStatus());
                    }
                }
            } catch (Exception e) {
                System.out.println("Error handling intention task: " + e.getMessage());
            }
        }
    }

    private boolean retryMatch(IntentionTask task, Intention intention) {
        int times = task.getRepeatTimes() + 1;
        if (times > 5) {
            System.out.println("Maximum retry attempts reached for intention ID: " + intention.getMid());
            matchFail(intention);
            return true;
        }

        IntentionTask newTask = new IntentionTask(intention.getMid(), 2L * times,
                TimeUnit.SECONDS, times);
        this.intentions.put(newTask);
        System.out.println("Scheduled retry #" + times + " for intention ID: " + intention.getMid() + " in " + (2L * times) + " seconds");
        return false;
    }
}