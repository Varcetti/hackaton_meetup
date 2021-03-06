package pl.jug.torun.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.jug.torun.domain.Draw;
import pl.jug.torun.domain.Event;
import pl.jug.torun.domain.Participant;
import pl.jug.torun.domain.PrizeDefinition;
import pl.jug.torun.repository.EventRepository;
import pl.jug.torun.repository.ParticipantRepository;
import pl.jug.torun.repository.PrizeDefinitionRepository;
import pl.jug.torun.service.DrawCreationService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DrawController {

    private static Gson gson = new Gson();

    @Autowired
    private DrawCreationService drawCreationService;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private PrizeDefinitionRepository prizeDefinitionRepository;

    @Autowired
    private EventRepository eventRepository;

    @RequestMapping(value = "/draw/start", produces = "application/json;charset=UTF-8")
    public String startDraw(@RequestBody String json) {

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();

        String eventId = jsonObject.get("event_id").getAsString();
        JsonArray prizesJsonArray = jsonObject.get("prizes").getAsJsonArray();
        JsonArray participantsJsonArray = jsonObject.get("participants").getAsJsonArray();

        Event event = eventRepository.findByEventId(eventId);
        if (event == null) {
            return "{\"error\":\"Nie znaleziono eventu o podanym event id\"}";
        }

        List<PrizeDefinition> prizes = jsonArrayToPrizes(prizesJsonArray);
        List<Participant> participants = jsonArrayToParticipants(participantsJsonArray);

        Draw draw = drawCreationService.createDraw(event, prizes, participants);

        JsonObject drawJson = new JsonObject();
        drawJson.addProperty("id", Long.toString(draw.getId()));

        return drawJson.toString();
    }

    private List<Participant> jsonArrayToParticipants(JsonArray jsonArray) {
        List<Participant> participants = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            Participant participant = gson.fromJson(jsonArray.get(i), Participant.class);

            Participant fromDb = participantRepository.findByMemberId(participant.getMemberId());

            participants.add(fromDb);
        }

        return participants;
    }

    private List<PrizeDefinition> jsonArrayToPrizes(JsonArray jsonArray) {
        List<PrizeDefinition> prizes = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            PrizeDefinition prize = gson.fromJson(jsonArray.get(i), PrizeDefinition.class);

            PrizeDefinition fromDb = prizeDefinitionRepository.findOne(prize.getId());

            prizes.add(fromDb);
        }

        return prizes;
    }
}
