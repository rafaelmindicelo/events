package br.com.nlw.events.repository;

import br.com.nlw.events.model.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {
    Event findByPrettyName(String prettyName);
}
