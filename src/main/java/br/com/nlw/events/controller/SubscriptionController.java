package br.com.nlw.events.controller;

import br.com.nlw.events.dto.ErrorMessage;
import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.IndicatorUserNotFoundException;
import br.com.nlw.events.exception.NoReferralSignupsException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.model.User;
import br.com.nlw.events.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping({"/subscription/{prettyName}", "/subscription/{prettyName}/{indicatorUserId}"})
    public ResponseEntity<?> createSubscription(@PathVariable String prettyName, @RequestBody User subscriber, @PathVariable(required = false) Integer indicatorUserId) {
        try {
            SubscriptionResponse subscription = subscriptionService.createSubscription(prettyName, subscriber, indicatorUserId);

            return ResponseEntity.ok(subscription);
        } catch (EventNotFoundException | IndicatorUserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(ex.getMessage()));
        } catch (SubscriptionConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage(ex.getMessage()));
        }
    }

    @GetMapping("/subscription/{prettyName}/ranking")
    public ResponseEntity<?> getCompleteRanking(@PathVariable String prettyName) {
        try {
            List<SubscriptionRankingItem> completeRanking = subscriptionService.getCompleteRanking(prettyName).subList(0, 3);
            return ResponseEntity.ok(completeRanking);
        } catch (EventNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(ex.getMessage()));
        }
    }

    @GetMapping("/subscription/{prettyName}/ranking/{userId}")
    public ResponseEntity<?> generateRankingByEventAndUser(@PathVariable String prettyName, @PathVariable Integer userId) {
        try{
            SubscriptionRankingByUser rankingByUser = subscriptionService.getRankingByUser(prettyName, userId);

            return ResponseEntity.ok(rankingByUser);
        }catch(NoReferralSignupsException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(ex.getMessage()));
        }
    }
}

