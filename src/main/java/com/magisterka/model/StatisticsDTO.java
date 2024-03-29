package com.magisterka.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class StatisticsDTO {
    private double firstPlayerWonGames;
    private double secondPlayerWonGames;
    private double draws;
    private PlayersStrategyDTO playersStrategyDTO;
    private double averageAmountOfRounds;
    private double averageAmountOfWars;
    private double roundsWithCycles;
    private List<String> detectedCycles = new ArrayList<>();
}
