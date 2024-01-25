package com.magisterka.web;

import com.magisterka.model.StatisticsDTO;
import com.magisterka.model.PlayersStrategyDTO;
import com.magisterka.model.StrengthDTO;
import com.magisterka.service.CycleService;
import com.magisterka.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class GameController {

    private final GameService gameService;
    private final CycleService cycleService;


    @PostMapping(value = "/statistics")
    public StatisticsDTO gameWithStrategyForStatistics(@RequestBody PlayersStrategyDTO playersStrategyDTO) {
        return gameService.getStatisticsForTwoPlayers(playersStrategyDTO);
    }

    @PostMapping(value = "/strength-comparison")
    public StatisticsDTO compareStrength(@RequestBody StrengthDTO strengthDTO) {
        return gameService.compareStrength(strengthDTO);
    }

    @PostMapping(value = "/compare")
    public List<StatisticsDTO> compareStrategyWithBasicStrategies(@RequestBody PlayersStrategyDTO playersStrategyDTO) {
        return gameService.compareStrategyWithBasicStrategies(playersStrategyDTO);
    }

    @PostMapping(value = "/statistics-with-cycles")
    public StatisticsDTO getStatisticsAndDetectCycles(@RequestBody PlayersStrategyDTO playersStrategyDTO) {
        return cycleService.getStatisticsWithCyclesForTwoPlayers(playersStrategyDTO);
    }

    @PostMapping(value = "/statistics-with-breaking-cycles")
    public StatisticsDTO getStatisticsWithBreakingCycles(@RequestBody PlayersStrategyDTO playersStrategyDTO) {
        return cycleService.getStatisticsWithBreakingCyclesForTwoPlayers(playersStrategyDTO);
    }

}
