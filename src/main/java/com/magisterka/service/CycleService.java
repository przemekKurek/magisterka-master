package com.magisterka.service;

import com.magisterka.model.CardDTO;
import com.magisterka.model.Player;
import com.magisterka.model.StatisticsDTO;
import com.magisterka.model.PlayersStrategyDTO;
import com.magisterka.model.RoundInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CycleService {

    private final GameService gameService;

    private final int GAME_AMOUNT = 100;
    private final Integer ROUND_LIMIT = 1000000;


    public StatisticsDTO getStatisticsWithCyclesForTwoPlayers(PlayersStrategyDTO playersStrategyDTO) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int roundsCounter = 0;
        int warCounter = 0;
        StatisticsDTO stats = new StatisticsDTO();
        for (int i = 0; i < GAME_AMOUNT; i++) {
            RoundInfo roundInfo = gameWithCyclesFinder(playersStrategyDTO);
            Integer result = roundInfo.getRoundResult();
            if (result == 1) {
                player1WinsCounter++;
            } else if (result == 2) {
                player2WinsCounter++;
            } else if (result == 0) {
                drawCounter++;
            }
            log.info("Game number " + i);
            roundsCounter += roundInfo.getRoundLength();
            warCounter += roundInfo.getWarCounter();
            if (roundInfo.getCycle() != null) {
                stats.getDetectedCycles().add(roundInfo.getCycle());
            }
        }
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setDraws(drawCounter * 100.0 / GAME_AMOUNT);
        stats.setPlayersStrategyDTO(playersStrategyDTO);
        stats.setAverageAmountOfRounds(roundsCounter / GAME_AMOUNT);
        stats.setAverageAmountOfWars(warCounter / GAME_AMOUNT);
        return stats;
    }


    public StatisticsDTO getStatisticsWithBreakingCyclesForTwoPlayers(PlayersStrategyDTO playersStrategyDTO) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int roundsCounter = 0;
        int warCounter = 0;
        StatisticsDTO stats = new StatisticsDTO();
        for (int i = 0; i < GAME_AMOUNT; i++) {
            RoundInfo roundInfo = gameWithBreakingCycles(playersStrategyDTO);
            Integer result = roundInfo.getRoundResult();
            if (result == 1) {
                player1WinsCounter++;
            } else if (result == 2) {
                player2WinsCounter++;
            } else if (result == 0) {
                drawCounter++;
            }
            log.info("Game number " + i);
            roundsCounter += roundInfo.getRoundLength();
            warCounter += roundInfo.getWarCounter();
            if (roundInfo.getCycle() != null) {
                stats.getDetectedCycles().add(roundInfo.getCycle());
            }
        }
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setDraws(drawCounter * 100.0 / GAME_AMOUNT);
        stats.setPlayersStrategyDTO(playersStrategyDTO);
        stats.setAverageAmountOfRounds(roundsCounter / GAME_AMOUNT);
        stats.setAverageAmountOfWars(warCounter / GAME_AMOUNT);
        return stats;
    }

    private RoundInfo gameWithCyclesFinder(PlayersStrategyDTO playersStrategyDTO) {
        RoundInfo roundInfo = new RoundInfo();
        List<CardDTO> cards = GameUtils.initializeDeck();
        Player player1 = new Player();
        Player player2 = new Player();
        List<Integer> register = new ArrayList<>();
        player1.setStrategySequence(playersStrategyDTO.getFisrtPlayerStrategySequence());
        player2.setStrategySequence(playersStrategyDTO.getSecondPlayerStrategySequence());
        player1.setWarStrategySequence(playersStrategyDTO.getFisrtPlayerWarStrategySequence());
        player2.setWarStrategySequence(playersStrategyDTO.getSecondPlayerWarStrategySequence());
        GameUtils.assignCardsToPlayers(cards, player1, player2);
        boolean playerCannotPlayWar = false;

        int counter = 0;
        int warCounter = 0;
        while (GameUtils.playerHasCards(player1) && GameUtils.playerHasCards(player2) && counter < ROUND_LIMIT) {
            if (GameUtils.getPlayerCard(player1).getRank() > GameUtils.getPlayerCard(player2).getRank()) {
                gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, true, register);
            } else if (Objects.equals(GameUtils.getPlayerCard(player1).getRank(), GameUtils.getPlayerCard(player2).getRank())) {
                warCounter++;
                if (player1.getCards().size() == 1 || player2.getCards().size() == 1) {
                    playerCannotPlayWar = true;
                    break;
                }
                gameService.handleWarAndSetRegister(player1, player2, true, new ArrayList<>(), register);
            } else {
                gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, false, register);
            }
            counter++;
            if (register.size() > 0 && register.size() % 2000 == 0) {
                if (hasRepeatingSubsequence(register).length() > 0) {
                    roundInfo.setRoundResult(0);
                    roundInfo.setCycle(register.toString());
                    roundInfo.setRoundLength(counter);
                    roundInfo.setWarCounter(warCounter);
                    log.info(register.toString());
                    return roundInfo;
                }
            }
        }
        return GameUtils.getRoundInfo(roundInfo, player1, player2, playerCannotPlayWar, counter, warCounter);
    }


    private RoundInfo gameWithBreakingCycles(PlayersStrategyDTO playersStrategyDTO) {
        RoundInfo roundInfo = new RoundInfo();
        List<CardDTO> cards = GameUtils.initializeDeck();
        Player player1 = new Player();
        Player player2 = new Player();
        List<Integer> register = new ArrayList<>();
        player1.setStrategySequence(playersStrategyDTO.getFisrtPlayerStrategySequence());
        player2.setStrategySequence(playersStrategyDTO.getSecondPlayerStrategySequence());
        player1.setWarStrategySequence(playersStrategyDTO.getFisrtPlayerWarStrategySequence());
        player2.setWarStrategySequence(playersStrategyDTO.getSecondPlayerWarStrategySequence());
        GameUtils.assignCardsToPlayers(cards, player1, player2);
        boolean playerCannotPlayWar = false;
        int counter = 0;
        int warCounter = 0;
        while (GameUtils.playerHasCards(player1) && GameUtils.playerHasCards(player2) && counter < ROUND_LIMIT) {
            if (GameUtils.getPlayerCard(player1).getRank() > GameUtils.getPlayerCard(player2).getRank()) {
                gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, true, register);
            } else if (Objects.equals(GameUtils.getPlayerCard(player1).getRank(), GameUtils.getPlayerCard(player2).getRank())) {
                warCounter++;
                if (player1.getCards().size() == 1 || player2.getCards().size() == 1) {
                    playerCannotPlayWar = true;
                    break;
                }
                gameService.handleWarAndSetRegister(player1, player2, true, new ArrayList<>(), register);
            } else {
                gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, false, register);
            }
            counter++;
            if (register.size() % 2000 == 0) {
                if (hasRepeatingSubsequence(register).length() > 0) {
                    register.clear();
                    player1.setStrategySequence("R");
                    player2.setStrategySequence("R");
                    for (int i = 0; i < 10; i++) {
                        if (!GameUtils.playerHasCards(player1) || !GameUtils.playerHasCards(player2)) {
                            break;
                        }
                        if (GameUtils.getPlayerCard(player1).getRank() > GameUtils.getPlayerCard(player2).getRank()) {
                            gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, true, register);
                        } else if (Objects.equals(GameUtils.getPlayerCard(player1).getRank(), GameUtils.getPlayerCard(player2).getRank())) {
                            warCounter++;
                            gameService.handleWarAndSetRegister(player1, player2, true, new ArrayList<>(), register);
                        } else {
                            gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, false, register);
                        }
                        counter++;
                    }
                    player1.setStrategySequence(playersStrategyDTO.getFisrtPlayerStrategySequence());
                    player2.setStrategySequence(playersStrategyDTO.getSecondPlayerStrategySequence());
                }
            }
            if (register.size() > 0 && register.size() % 4000 == 0) {
                register.subList(0, 2000).clear();
            }
        }
        return GameUtils.getRoundInfo(roundInfo, player1, player2, playerCannotPlayWar, counter, warCounter);
    }


    private String hasRepeatingSubsequence(List<Integer> register) {
        HashSet<String> seenSubsequences = new HashSet<>();
        Integer subsequenceLength = 52*8;
        for (int i = 0; i <= register.size() - subsequenceLength; i++) {
            StringBuilder subsequenceBuilder = new StringBuilder();
            for (int j = i; j < i + subsequenceLength; j++) {
                subsequenceBuilder.append(register.get(j));
            }
            String subsequence = subsequenceBuilder.toString();

            if (seenSubsequences.contains(subsequence)) {
                return subsequence;

            }
            seenSubsequences.add(subsequence);
        }
        return "";
    }
}
