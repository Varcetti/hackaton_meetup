package pl.jug.torun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jug.torun.domain.Draw;
import pl.jug.torun.domain.Participant;
import pl.jug.torun.domain.PrizeDefinition;
import pl.jug.torun.domain.ReceivedPrize;
import pl.jug.torun.repository.ReceivedPrizeRepository;

import java.util.List;
import java.util.Random;

@Service
@Transactional
public class RandomizeService {

    private Random random = new Random();

    @Autowired
    private ReceivedPrizeRepository receivedPrizeRepository;

    public Participant randomParticipant(Draw draw, PrizeDefinition prizeDefinition) {
        return getParticipant(draw, prizeDefinition);
    }

    private Participant getParticipant(Draw draw, PrizeDefinition prizeDefinition) {
        Participant participant;
        do {
            int pos = random.nextInt(draw.getRemainingParticipants().size());
            participant = draw.getRemainingParticipants().get(pos);
        } while (userAlreadyHaveThisPrize(participant, prizeDefinition));

        return participant;
    }

    private boolean userAlreadyHaveThisPrize(Participant participant, PrizeDefinition prizeDefinition) {
        List<ReceivedPrize> prizes = receivedPrizeRepository.findByParticipant(participant);

        return prizes.stream().anyMatch((prize) -> prize.getPrizeDefinition().equals(prizeDefinition));
    }
}
