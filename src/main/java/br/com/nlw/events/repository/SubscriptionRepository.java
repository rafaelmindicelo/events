package br.com.nlw.events.repository;

import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Integer> {
    Subscription findByEventAndSubscriber(Event event, User subscriber);

    @Query(
            value = "select u.user_id, u.user_name, count(*) qtd "
                    + "from tbl_subscription s "
                    + "inner join tbl_user u on s.indication_user_id = u.user_id "
                    + "where s.indication_user_id is not null and s.event_id = :eventId "
                    + "group by u.user_id, u.user_name "
                    + "order by qtd desc", nativeQuery = true
    )
    List<SubscriptionRankingItem> generateRanking(@Param("eventId") Integer eventId);
}
