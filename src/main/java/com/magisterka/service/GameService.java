package com.magisterka.service;

import com.magisterka.model.CardDTO;
import com.magisterka.model.Player;
import com.magisterka.model.StatisticsDTO;
import com.magisterka.model.PlayersStrategyDTO;
import com.magisterka.model.RoundInfo;
import com.magisterka.model.StrengthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {


    public void handleWar(Player player1, Player player2, boolean firstWar, List<CardDTO> warCards) {
        Integer[] cardNumbers = new Integer[2];

        setCardsForWar(player1, player2, warCards, firstWar, cardNumbers);

        if (isDeckEmpty(player1) && !isDeckEmpty(player2)) {
            handlePlayerWarWinWithStrategy(player1, player2, false, warCards);
        } else if (!isDeckEmpty(player1) && isDeckEmpty(player2)) {
            handlePlayerWarWinWithStrategy(player1, player2, true, warCards);
        } else if (!isDeckEmpty(player1) && !isDeckEmpty(player2)) {
            if (cardNumbers[0] / 4 > cardNumbers[1] / 4) {
                handlePlayerWarWinWithStrategy(player1, player2, true, warCards);
            } else if (cardNumbers[0] / 4 < cardNumbers[1] / 4) {
                handlePlayerWarWinWithStrategy(player1, player2, false, warCards);
            } else {
                handleWar(player1, player2, false, warCards);
            }
        }
    }

    public void handleWarAndSetRegister(Player player1, Player player2, boolean firstWar, List<CardDTO> warCards, List<Integer> register) {
        Integer[] cardNumbers = new Integer[2];

        setCardsForWarAndSetRegister(player1, player2, warCards, firstWar, cardNumbers, register);

        if (isDeckEmpty(player1) && !isDeckEmpty(player2)) {
            handlePlayerWarWinWithStrategy(player1, player2, false, warCards);
        } else if (!isDeckEmpty(player1) && isDeckEmpty(player2)) {
            handlePlayerWarWinWithStrategy(player1, player2, true, warCards);
        } else if (!isDeckEmpty(player1) && !isDeckEmpty(player2)) {
            if (cardNumbers[0] / 4 > cardNumbers[1] / 4) {
                handlePlayerWarWinWithStrategy(player1, player2, true, warCards);
            } else if (cardNumbers[0] / 4 < cardNumbers[1] / 4) {
                handlePlayerWarWinWithStrategy(player1, player2, false, warCards);
            } else {
                handleWarAndSetRegister(player1, player2, false, warCards, register);
            }
        }
    }


    private void setCardsForWar(Player player1, Player player2, List<CardDTO> warCards, boolean isFirstWar, Integer[] cardNumbers) {
        int cardsToPlay = 2;
        if (isFirstWar) {
            cardsToPlay = 3;
        }
        // Each player puts three cards face down
        for (int i = 1; i <= cardsToPlay; i++) {
            if (!player1.getCards().isEmpty() && !player2.getCards().isEmpty()) {
                warCards.add(player1.getCards().get(0));
                warCards.add(player2.getCards().get(0));
                if (i == cardsToPlay) {
                    cardNumbers[0] = player1.getCards().get(0).getCardNumber();
                    cardNumbers[1] = player2.getCards().get(0).getCardNumber();
                }
                player1.getCards().remove(0);
                player2.getCards().remove(0);
            }
        }
    }

    private void setCardsForWarAndSetRegister(Player player1, Player player2, List<CardDTO> warCards, boolean isFirstWar, Integer[] cardNumbers, List<Integer> register) {
        int cardsToPlay = 2;
        if (isFirstWar) {
            cardsToPlay = 3;
        }
        // Each player puts three cards face down
        for (int i = 1; i <= cardsToPlay; i++) {
            if (!player1.getCards().isEmpty() && !player2.getCards().isEmpty()) {
                warCards.add(player1.getCards().get(0));
                warCards.add(player2.getCards().get(0));
                register.add(player1.getCards().get(0).getCardNumber());
                register.add(player2.getCards().get(0).getCardNumber());
                if (i == cardsToPlay) {
                    cardNumbers[0] = player1.getCards().get(0).getCardNumber();
                    cardNumbers[1] = player2.getCards().get(0).getCardNumber();
                }
                player1.getCards().remove(0);
                player2.getCards().remove(0);
            }
        }
    }

    private boolean isDeckEmpty(Player player) {
        return player.getCards().isEmpty();
    }

    public void handlePlayerWinWithStrategy(Player player1, Player player2, boolean hasPlayer1Won) {
        CardDTO player1Card = player1.getCards().get(0);
        CardDTO player2Card = player2.getCards().get(0);
        List<CardDTO> cardsToGet = new ArrayList<>();
        cardsToGet.add(player1Card);
        cardsToGet.add(player2Card);
        removeCards(player1, player2);
        if (hasPlayer1Won) {
            sortCardsAccordingToStrategy(player1, player2, cardsToGet, true);
            player1.getCards().addAll(cardsToGet);
            player1.winCounterIncrement();
        } else {
            sortCardsAccordingToStrategy(player1, player2, cardsToGet, false);
            player2.getCards().addAll(cardsToGet);
            player2.winCounterIncrement();
        }
    }

    public void handlePlayerWinWithStrategyAndSetRegister(Player player1, Player player2, boolean hasPlayer1Won, List<Integer> register) {
        CardDTO player1Card = player1.getCards().get(0);
        CardDTO player2Card = player2.getCards().get(0);
        register.add(player1Card.getCardNumber());
        register.add(player2Card.getCardNumber());
        List<CardDTO> cardsToGet = new ArrayList<>();
        cardsToGet.add(player1Card);
        cardsToGet.add(player2Card);
        removeCards(player1, player2);
        if (hasPlayer1Won) {
            sortCardsAccordingToStrategy(player1, player2, cardsToGet, true);
            player1.getCards().addAll(cardsToGet);
            player1.winCounterIncrement();
        } else {
            sortCardsAccordingToStrategy(player1, player2, cardsToGet, false);
            player2.getCards().addAll(cardsToGet);
            player2.winCounterIncrement();
        }
    }

    private void handlePlayerWarWinWithStrategy(Player player1, Player player2, boolean hasPlayer1Won, List<CardDTO> cardsToGet) {
        if (hasPlayer1Won) {
            sortCardsAccordingToStrategy(player1, player2, cardsToGet, true);
            player1.getCards().addAll(cardsToGet);
            player1.warWinCounterIncrement();
        } else {
            sortCardsAccordingToStrategy(player1, player2, cardsToGet, false);
            player2.getCards().addAll(cardsToGet);
            player2.warWinCounterIncrement();
        }
    }



    private void sortCardsAccordingToStrategy(Player player1, Player player2, List<CardDTO> cardsToGet, boolean hasPlayer1Won) {
        Player winnerOfTheRound = hasPlayer1Won ? player1 : player2;
        Player loserOfTheRound = hasPlayer1Won ? player2 : player1;
        if (getStrategy(winnerOfTheRound) == 'H') {
            GameUtils.sortCardsDescending(cardsToGet);
        } else if (getStrategy(winnerOfTheRound) == 'L') {
            GameUtils.sortCardsAscending(cardsToGet);
        } else if (getStrategy(winnerOfTheRound) == 'R') {
            GameUtils.shuffleDeck(cardsToGet);
        } else if (getStrategy(winnerOfTheRound) == 'G' || getStrategy(winnerOfTheRound) == 'A' || getStrategy(winnerOfTheRound) == 'N') {
            if (getStrategy(winnerOfTheRound) == 'G') {
                distributeCardsGreedy(cardsToGet, winnerOfTheRound, loserOfTheRound, 'G');
            } else if (getStrategy(winnerOfTheRound) == 'A') {
                distributeCardsGreedy(cardsToGet, winnerOfTheRound, loserOfTheRound, 'A');
            } else {
                distributeCardsGreedy(cardsToGet, winnerOfTheRound, loserOfTheRound, 'N');
            }
        }
        else if (getWarStrategy(winnerOfTheRound) == 'D') {
            GameUtils.sortCardsDescending(cardsToGet);
        } else if (getWarStrategy(winnerOfTheRound) == 'A') {
            GameUtils.sortCardsAscending(cardsToGet);
        }  else if (getWarStrategy(winnerOfTheRound) == 'R') {
            GameUtils.shuffleDeck(cardsToGet);
        }
    }

    private void distributeCardsGreedy(List<CardDTO> cardsToGet, Player player1, Player player2, char greedyOption) {
        if (player1.getCards().size() >= player2.getCards().size()) {
            GameUtils.shuffleDeck(cardsToGet);
        } else {
            int player1DeckSize = player1.getCards().size();
            List<CardDTO> player2CardsToCompare = new ArrayList<>();
            player2CardsToCompare.add(player2.getCards().get(player1DeckSize));
            player2CardsToCompare.add(player2.getCards().get(player1DeckSize + 1));
            GameUtils.sortCardsAscending(cardsToGet);
            Integer c1 = cardsToGet.get(0).getRank();
            Integer c2 = cardsToGet.get(1).getRank();
            Integer p1 = player2CardsToCompare.get(0).getRank();
            Integer p2 = player2CardsToCompare.get(1).getRank();
            long player1aces = player1.getCards().stream().filter(card -> card.getRank() == 12).count();
            long player2aces = player2.getCards().stream().filter(card -> card.getRank() == 12).count();
            boolean moreAces = player1aces > player2aces;
            // b -> better, e -> equal, w - worse
            boolean bb = c1 > p1 && c2 > p2;
            boolean be = c1 > p1 && c2 == p2;
            boolean bw = c1 > p1 && c2 < p2;
            boolean eb = c1 == p1 && c2 > p2;
            boolean ee = c1 == p1 && c2 == p2;
            boolean ew = c1 == p1 && c2 < p2;
            boolean wb = c1 < p1 && c2 > p2;
            boolean we = c1 < p1 && c2 == p2;
            boolean ww = c1 < p1 && c2 < p2;
            if (bb || bw || ww) {
                GameUtils.sortCardsDescending(cardsToGet);
            } else if (be || eb || ee || ew || we) {
                if (greedyOption == 'A') {
                    GameUtils.sortCardsDescending(cardsToGet);
                } else if (greedyOption == 'N') {
                    GameUtils.sortCardsAscending(cardsToGet);
                } else {
                    if (moreAces) {
                        GameUtils.sortCardsDescending(cardsToGet);
                    } else {
                        GameUtils.sortCardsAscending(cardsToGet);
                    }
                }
            } else if (wb) {
                GameUtils.sortCardsAscending(cardsToGet);
            }
        }
    }


    private char getStrategy(Player player) {
        int indexOfCharacter = player.getWinCounter().intValue() % player.getStrategySequence().length();
        return player.getStrategySequence().charAt(indexOfCharacter);
    }

    private char getWarStrategy(Player player) {
        int indexOfCharacter = player.getWinCounter().intValue() % player.getWarStrategySequence().length();
        return player.getWarStrategySequence().charAt(indexOfCharacter);
    }


    private void removeCards(Player player1, Player player2) {
        player2.getCards().remove(0);
        player1.getCards().remove(0);
    }


    public RoundInfo gameWithStrategies(PlayersStrategyDTO playersStrategyDTO) {
        List<CardDTO> cards = GameUtils.initializeDeck();
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setStrategySequence(playersStrategyDTO.getFisrtPlayerStrategySequence());
        player2.setStrategySequence(playersStrategyDTO.getSecondPlayerStrategySequence());
        player1.setWarStrategySequence(playersStrategyDTO.getFisrtPlayerWarStrategySequence());
        player2.setWarStrategySequence(playersStrategyDTO.getSecondPlayerWarStrategySequence());
        GameUtils.assignCardsToPlayers(cards, player1, player2);
        int counter = 0;
        int warCounter = 0;
        while (GameUtils.playerHasCards(player1) && GameUtils.playerHasCards(player2) && counter < 10000) {
            if (GameUtils.getPlayerCard(player1).getRank() > GameUtils.getPlayerCard(player2).getRank()) {
                handlePlayerWinWithStrategy(player1, player2, true);
            } else if (Objects.equals(GameUtils.getPlayerCard(player1).getRank(), GameUtils.getPlayerCard(player2).getRank())) {
                handlePlayerWarWinWithStrategy(player1, player2, true, new ArrayList<>());
                warCounter++;
            } else {
                handlePlayerWinWithStrategy(player1, player2, false);
            }
            counter++;
        }
        log.info("Player1 has " + player1.getCards().size() + " cards.");
        log.info("Player2 has " + player2.getCards().size() + " cards.");
        log.info("War counter " + warCounter);
        log.info("Round counter " + counter);
        RoundInfo roundInfo = new RoundInfo();
        roundInfo.setRoundLength(counter);
        if (player2.getCards().isEmpty()) {
            roundInfo.setRoundResult(1);
        } else if (player1.getCards().isEmpty()) {
            roundInfo.setRoundResult(2);
        } else {
            roundInfo.setRoundResult(0);
        }
        return roundInfo;
    }

    public StatisticsDTO getStatisticsForTwoPlayers(PlayersStrategyDTO playersStrategyDTO) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int gameAmount = 100000;
        int roundsCounter = 0;
        for (int i = 0; i < gameAmount; i++) {
            RoundInfo result = gameWithStrategies(playersStrategyDTO);
            if (result.getRoundResult() == 1) {
                player1WinsCounter++;
            } else if (result.getRoundResult() == 2) {
                player2WinsCounter++;
            } else if (result.getRoundResult() == 0) {
                drawCounter++;
            }
            if (i % 1000 == 0) {
                log.info("Computed " + i / 1000 + "%");
            }
            roundsCounter += result.getRoundLength();
        }
        StatisticsDTO stats = new StatisticsDTO();
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / gameAmount);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / gameAmount);
        stats.setDraws(drawCounter * 100.0 / gameAmount);
        stats.setFirstPlayerStrategy(playersStrategyDTO.getFisrtPlayerStrategySequence());
        stats.setSecondPlayerStrategy(playersStrategyDTO.getSecondPlayerStrategySequence());
        stats.setAverageAmountOfRounds(roundsCounter / gameAmount);

        return stats;
    }

    public List<StatisticsDTO> compareStrategyWithBasicStrategies(String strategy) {
        List<StatisticsDTO> result = new ArrayList<>();
        PlayersStrategyDTO toRandom = new PlayersStrategyDTO(strategy, "R");
        StatisticsDTO compareToRandom = getStatisticsForTwoPlayers(toRandom);

        PlayersStrategyDTO toGetHigher = new PlayersStrategyDTO(strategy, "H");
        StatisticsDTO compareToGetHigher = getStatisticsForTwoPlayers(toGetHigher);

        PlayersStrategyDTO toGetLower = new PlayersStrategyDTO(strategy, "L");
        StatisticsDTO compareToGetLower = getStatisticsForTwoPlayers(toGetLower);

        PlayersStrategyDTO greedy = new PlayersStrategyDTO(strategy, "G");
        StatisticsDTO compareToGreedy = getStatisticsForTwoPlayers(greedy);

        PlayersStrategyDTO greedyA = new PlayersStrategyDTO(strategy, "A");
        StatisticsDTO compareToGreedyA = getStatisticsForTwoPlayers(greedyA);

        PlayersStrategyDTO greedyN = new PlayersStrategyDTO(strategy, "N");
        StatisticsDTO compareToGreedyN = getStatisticsForTwoPlayers(greedyN);

        result.add(compareToRandom);
        result.add(compareToGetHigher);
        result.add(compareToGetLower);
        result.add(compareToGreedy);
        result.add(compareToGreedyA);
        result.add(compareToGreedyN);
        return result;
    }


    //compare strength
    public StatisticsDTO compareStrength(StrengthDTO strengthDTO) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int gameAmount = 1000;
        int roundsCounter = 0;
        for (int i = 0; i < gameAmount; i++) {
            RoundInfo result = gameWithStrategiesForStrenghtComparison(strengthDTO);
            if (result.getRoundResult() == 1) {
                player1WinsCounter++;
            } else if (result.getRoundResult() == 2) {
                player2WinsCounter++;
            } else if (result.getRoundResult() == 0) {
                drawCounter++;
            }
            if (i % 1000 == 0) {
                log.info("Computed " + i / 1000 + "%");
            }
            roundsCounter += result.getRoundLength();

        }
        StatisticsDTO stats = new StatisticsDTO();
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / gameAmount);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / gameAmount);
        stats.setDraws(drawCounter * 100.0 / gameAmount);
        stats.setFirstPlayerStrategy(strengthDTO.getPlayersStrategyDTO().getFisrtPlayerStrategySequence());
        stats.setSecondPlayerStrategy(strengthDTO.getPlayersStrategyDTO().getSecondPlayerStrategySequence());
        stats.setAverageAmountOfRounds(roundsCounter/gameAmount);

        return stats;
    }


    public RoundInfo gameWithStrategiesForStrenghtComparison(StrengthDTO strengthDTO) {
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setStrategySequence(strengthDTO.getPlayersStrategyDTO().getFisrtPlayerStrategySequence());
        player2.setStrategySequence(strengthDTO.getPlayersStrategyDTO().getSecondPlayerStrategySequence());
        List<CardDTO> player1Cards = new ArrayList<>();
        List<CardDTO> player2Cards = new ArrayList<>();
        player1Cards.addAll(strengthDTO.getPlayer1Cards());
        player2Cards.addAll(strengthDTO.getPlayer2Cards());
        GameUtils.shuffleDeck(player1Cards);
        GameUtils.shuffleDeck(player2Cards);
        player1.setCards(player1Cards);
        player2.setCards(player2Cards);
        boolean playerCannotPlayWar = false;
        int counter = 0;
        int warCounter = 0;
        while (GameUtils.playerHasCards(player1) && GameUtils.playerHasCards(player2) && counter < 100000) {
            if (GameUtils.getPlayerCard(player1).getRank() > GameUtils.getPlayerCard(player2).getRank()) {
                handlePlayerWinWithStrategy(player1, player2, true);
            } else if (Objects.equals(GameUtils.getPlayerCard(player1).getRank(), GameUtils.getPlayerCard(player2).getRank())) {
                if (player1.getCards().size() == 1 || player2.getCards().size() == 1) {
                    playerCannotPlayWar = true;
                    break;
                }
                handleWar(player1, player2, true, new ArrayList<>());
                warCounter++;
            } else {
                handlePlayerWinWithStrategy(player1, player2, false);
            }
            counter++;
        }
        RoundInfo roundInfo = new RoundInfo();
        log.info("Player1 has " + player1.getCards().size() + " cards.");
        log.info("Player2 has " + player2.getCards().size() + " cards.");
        log.info("War counter " + warCounter);
        log.info("Round counter " + counter);
        return GameUtils.getRoundInfo(roundInfo, player1, player2, playerCannotPlayWar, counter);
    }


}
