package br.com.nlw.events.service;

import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.IndicatorUserNotFoundException;
import br.com.nlw.events.exception.NoReferralSignupsException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repository.EventRepository;
import br.com.nlw.events.repository.SubscriptionRepository;
import br.com.nlw.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class SubscriptionService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EventRepository eventRepository;

    public SubscriptionResponse createSubscription(String eventName, User user, Integer indicatorUserId) {
        Subscription subscription = new Subscription();

        Event event = eventRepository.findByPrettyName(eventName);

        if (event == null) {
            throw new EventNotFoundException("Event " + eventName + " does not exist.");
        }

        if (indicatorUserId != null) {
            User indicatorUser = userRepository.findById(indicatorUserId).orElse(null);

            if (indicatorUser == null) {
                throw new IndicatorUserNotFoundException("Indicator user " + indicatorUserId + " does not exist.");
            }
            subscription.setIndication(indicatorUser);
        }

        User foundUser = userRepository.findByEmail(user.getEmail());

        if (foundUser == null) {
            foundUser = userRepository.save(user);
        }

        Subscription subscriptionByEventAndSubscriber = subscriptionRepository.findByEventAndSubscriber(event, foundUser);

        if (subscriptionByEventAndSubscriber != null) {
            throw new SubscriptionConflictException("User " + foundUser.getName() + " is already registered for event " + eventName);
        }

        subscription.setEvent(event);
        subscription.setSubscriber(foundUser);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return new SubscriptionResponse(savedSubscription.getSubscriptionNumber(), "https://platform.com/subscription/" + savedSubscription.getEvent().getPrettyName() + "/" + savedSubscription.getSubscriber().getUserId());

    }

    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
        Event event = eventRepository.findByPrettyName(prettyName);
        if (event == null) {
            throw new EventNotFoundException("Event " + prettyName + " does not exist.");
        }

        return subscriptionRepository.generateRanking(event.getEventId());
    }

    public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
        List<SubscriptionRankingItem> completeRanking = getCompleteRanking(prettyName);

        SubscriptionRankingItem subscriptionRankingItem = completeRanking.stream().filter(item -> item.userId().equals(userId)).findFirst().orElse(null);

        if (subscriptionRankingItem == null) {
            throw new NoReferralSignupsException("No user signed up via referral to user " + userId);
        }

        int rankingPosition = IntStream.range(0, completeRanking.size())
                .filter(position -> completeRanking.get(position).userId().equals(userId))
                .findFirst().orElse(-1);

        return new SubscriptionRankingByUser(subscriptionRankingItem, rankingPosition + 1);
    }
}
