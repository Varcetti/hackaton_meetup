package pl.jug.torun.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.jug.torun.domain.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByEventId(String eventId);
}
