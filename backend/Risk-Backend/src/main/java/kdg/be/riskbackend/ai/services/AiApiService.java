package kdg.be.riskbackend.ai.services;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.dtos.game.GameDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;

/**
 * This class is used for the communication with the AI.
 */
@Service
@Transactional
@Slf4j
public class AiApiService {
    ModelMapper modelMapper;
    @Value("${ai.url}")
    private String aiUrl;

    /**
     * Constructor for the AiApiService.
     *
     * @param modelMapper the model mapper to use
     */
    public AiApiService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Get a move of an AI player
     *
     * @param game the entire game
     */
    @Async
    public void getMove(Game game) {
        try {
            final String uri = aiUrl + "/makeMove";
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<GameDto> request = new HttpEntity<>(modelMapper.map(game, GameDto.class));
            restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    request,
                    String.class);
            log.info("AI move requested");
        } catch (Exception e) {
            log.error("Ai could not give a move: " + e.getMessage());
        }
    }
}
