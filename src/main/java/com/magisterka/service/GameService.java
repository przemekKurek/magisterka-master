package com.magisterka.service;

import com.magisterka.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {

    private final Integer ROUND_LIMIT = 100000;
    private final Integer GAME_AMOUNT = 10000;


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
        if (cardsToGet.size() == 2) {
            if (getStrategy(winnerOfTheRound) == 'H') {
                GameUtils.sortCardsDescending(cardsToGet);
            } else if (getStrategy(winnerOfTheRound) == 'L') {
                GameUtils.sortCardsAscending(cardsToGet);
            } else if (getStrategy(winnerOfTheRound) == 'R') {
                GameUtils.shuffleDeck(cardsToGet);
            } else if ("GANBPSCZX".contains(String.valueOf(getStrategy(winnerOfTheRound)))) {
                distributeCardsGreedy(cardsToGet, winnerOfTheRound, loserOfTheRound, getStrategy(winnerOfTheRound));
            }
        } else {
            if (getWarStrategy(winnerOfTheRound) == 'D') {
                GameUtils.sortCardsDescending(cardsToGet);
            } else if (getWarStrategy(winnerOfTheRound) == 'A') {
                GameUtils.sortCardsAscending(cardsToGet);
            } else if (getWarStrategy(winnerOfTheRound) == 'R') {
                GameUtils.shuffleDeck(cardsToGet);
            }
        }
    }

    private void distributeCardsGreedy(List<CardDTO> cardsToGet, Player player1, Player player2, char greedyOption) {
        if (player1.getCards().size() >= player2.getCards().size()) {
            if (greedyOption == 'P') {
                GameUtils.sortCardsDescending(cardsToGet);
                }
          else {
                GameUtils.shuffleDeck(cardsToGet);
            }
        } else {
            if ((greedyOption == 'B' || greedyOption == 'P') && player1.getCards().size() + 4 >= player2.getCards().size()) {
                greedyOption = 'G';
            }
            else if (greedyOption == 'X' && player1.getCards().size() + 2 >= player2.getCards().size()) {
                GameUtils.sortCardsAscending(cardsToGet);
                return;
            }
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
            if (greedyOption == 'X') {
                if ( bw || ww) {
                    GameUtils.sortCardsAscending(cardsToGet);
                } else if (bb || wb) {
                    GameUtils.sortCardsDescending(cardsToGet);
                } else {
                    if (eb || ee || ew) {
                        Integer c3 = player2.getCards().get(player1DeckSize + 2).getRank();
                        sortCardsLoosingProbability(c3, cardsToGet);
                    } else {
                        Integer c4 = player2.getCards().get(player1DeckSize + 3).getRank();
                        sortCardsLoosingProbability(c4, cardsToGet);
                    }
                }
                return;

            }
            if (bb || bw || ww) {
                GameUtils.sortCardsDescending(cardsToGet);
            } else if (wb) {
                GameUtils.sortCardsAscending(cardsToGet);
            } else if (be || eb || ee || ew || we) {    // possibilty to avoid or force war
                if (greedyOption == 'A') {
                    GameUtils.sortCardsDescending(cardsToGet);
                } else if (greedyOption == 'N') {
                    GameUtils.sortCardsAscending(cardsToGet);
                } else if (greedyOption == 'B') {
                    if (eb || ee || ew) {
                        Integer c3 = player2.getCards().get(player1DeckSize + 2).getRank();
                        sortCardsGreedyProbability(c3, cardsToGet);
                    } else {
                        Integer c4 = player2.getCards().get(player1DeckSize + 3).getRank();
                        sortCardsGreedyProbability(c4, cardsToGet);
                    }
                } else if (greedyOption == 'P') {
                    player2CardsToCompare.add(player2.getCards().get(player1DeckSize + 2));
                    player2CardsToCompare.add(player2.getCards().get(player1DeckSize + 3));
                    if (eb || ee || ew) {
                        CardDTO cardToCheck = checkYourNextCard(player1, player2, true, cardsToGet);
                        if (cardToCheck == null || cardToCheck.getRank() < player2.getCards().get(player1DeckSize + 2).getRank()) {
                            GameUtils.sortCardsDescending(cardsToGet);
                        } else {
                            GameUtils.sortCardsAscending(cardsToGet);
                        }
                    } else {
                        CardDTO cardToCheck = checkYourNextCard(player1, player2, false, cardsToGet);
                        if (cardToCheck == null || cardToCheck.getRank() < player2.getCards().get(player1DeckSize + 3).getRank()) {
                            GameUtils.sortCardsDescending(cardsToGet);
                        } else {
                            GameUtils.sortCardsAscending(cardsToGet);
                        }
                    }
                } else if (greedyOption == 'S') {
                    Integer card1Rank = cardsToGet.get(0).getRank();
                    Integer card2Rank = cardsToGet.get(1).getRank();
                    if (player2CardsToCompare.get(0).getRank() == 12 || player2CardsToCompare.get(1).getRank() == 12 || player2CardsToCompare.get(0).getRank() + player2CardsToCompare.get(1).getRank() > 18) {
                        GameUtils.sortCardsAscending(cardsToGet);
                    }
                    else if (card1Rank == 12 || card2Rank == 12 || card1Rank + card2Rank > 4) {
                        GameUtils.sortCardsDescending(cardsToGet);
                    } else {
                        GameUtils.sortCardsAscending(cardsToGet);
                    }
                } else if (greedyOption == 'C') {
                    sortCardsStrategyC(player1, player2, cardsToGet);
                } else if (greedyOption == 'Z') {
                    sortCardsStrategyZ(player1, player2, cardsToGet);
                } else {
                    if (moreAces) {
                        GameUtils.sortCardsDescending(cardsToGet);
                    } else {
                        GameUtils.sortCardsAscending(cardsToGet);
                    }
                }
            }
        }
    }

    private void sortCardsLoosingProbability(Integer cardRank, List<CardDTO> cardsToGet) {
        if (cardRank > 3) {
            GameUtils.sortCardsAscending(cardsToGet);
        } else {
            GameUtils.sortCardsDescending(cardsToGet);
        }
    }

    private void sortCardsStrategyC(Player p1, Player p2, List<CardDTO> cardsToGet) {
        Integer p1DeckStrength = calculateCardStrengthInDeck(p1);
        Integer p2DeckStrength = calculateCardStrengthInDeck(p2);
        if (p1DeckStrength > p2DeckStrength) {
            GameUtils.sortCardsDescending(cardsToGet);
        } else {
            GameUtils.sortCardsAscending(cardsToGet);
        }
    }

    private void sortCardsStrategyZ(Player p1, Player p2, List<CardDTO> cardsToGet) {
        Integer p1DeckStrength = calculateCardStrengthInDeck(p1);
        Integer p2DeckStrength = calculateCardStrengthInDeck(p2);
        if (p1DeckStrength <= p2DeckStrength) {
            GameUtils.sortCardsDescending(cardsToGet);
        } else {
            GameUtils.sortCardsAscending(cardsToGet);
        }
    }

    private void sortCardsGreedyProbability(Integer cardRank, List<CardDTO> cardsToGet) {
        if (cardRank <= 3) {
            GameUtils.sortCardsAscending(cardsToGet);
        } else {
            GameUtils.sortCardsDescending(cardsToGet);
        }
    }

    private CardDTO checkYourNextCard(Player p1, Player p2, boolean firstCard, List<CardDTO> cardsToGet) {
        Player tempP1 = new Player(p1);
        Player tempP2 = new Player(p2);
        tempP1.winCounterIncrement();
        tempP1.getCards().addAll(cardsToGet);
        tempP1.setStrategySequence("H");
        if (tempP1.getCards().size() < 4) {
            return null;
        }
        Integer startingDeckSize = tempP1.getCards().size();
        Integer counter = 0;
        while (GameUtils.playerHasCards(tempP1) && GameUtils.playerHasCards(tempP2) && counter < startingDeckSize - 2) {
            counter++;
            if (GameUtils.getPlayerCard(tempP1).getRank() > GameUtils.getPlayerCard(tempP2).getRank()) {
                handlePlayerWinWithStrategy(tempP1, tempP2, true);
                if (firstCard) {
                    return tempP1.getCards().get(tempP1.getCards().size() - 2);
                } else {
                    return tempP1.getCards().get(tempP1.getCards().size() - 1);
                }
            } else if (Objects.equals(GameUtils.getPlayerCard(tempP1).getRank(), GameUtils.getPlayerCard(tempP2).getRank())) {
                if (tempP1.getCards().size() == 1 || tempP1.getCards().size() == 2) {
                    return null;
                }
                Integer deckSizeBeforeWar = tempP1.getCards().size();
                handleWar(tempP1, tempP2, true, new ArrayList<>());
                if (deckSizeBeforeWar < tempP1.getCards().size()) {
                    if (firstCard) {
                        return tempP1.getCards().get(tempP1.getCards().size() - 2);
                    } else {
                        return tempP1.getCards().get(tempP1.getCards().size() - 1);
                    }
                }
            } else {
                handlePlayerWinWithStrategy(tempP1, tempP2, false);
            }
        }
        return null;

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
        boolean playerCannotPlayWar = false;
        int counter = 0;
        int warCounter = 0;
        while (GameUtils.playerHasCards(player1) && GameUtils.playerHasCards(player2) && counter < ROUND_LIMIT) {
            if (GameUtils.getPlayerCard(player1).getRank() > GameUtils.getPlayerCard(player2).getRank()) {
                handlePlayerWinWithStrategy(player1, player2, true);
            } else if (Objects.equals(GameUtils.getPlayerCard(player1).getRank(), GameUtils.getPlayerCard(player2).getRank())) {
                warCounter++;
                if (player1.getCards().size() == 1 || player2.getCards().size() == 1) {
                    playerCannotPlayWar = true;
                    break;
                }
                handleWar(player1, player2, true, new ArrayList<>());
            } else {
                handlePlayerWinWithStrategy(player1, player2, false);
            }
            counter++;
        }
        RoundInfo roundInfo = new RoundInfo();
        return GameUtils.getRoundInfo(roundInfo, player1, player2, playerCannotPlayWar, counter, warCounter);
    }

    public StatisticsDTO getStatisticsForTwoPlayers(PlayersStrategyDTO playersStrategyDTO) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int roundsCounter = 0;
        int warCounter = 0;
        for (int i = 0; i < GAME_AMOUNT; i++) {
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
            warCounter += result.getWarCounter();

        }
        StatisticsDTO stats = new StatisticsDTO();
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setDraws(drawCounter * 100.0 / GAME_AMOUNT);
        stats.setPlayersStrategyDTO(playersStrategyDTO);
        stats.setAverageAmountOfRounds(roundsCounter / GAME_AMOUNT);
        stats.setAverageAmountOfWars(warCounter / GAME_AMOUNT);

        return stats;
    }

    public List<StatisticsDTO> compareStrategyWithBasicStrategies(PlayersStrategyDTO playersStrategyDTO) {
        List<StatisticsDTO> result = new ArrayList<>();
        String strategy = playersStrategyDTO.getFisrtPlayerStrategySequence();
        String warStrategy = playersStrategyDTO.getFisrtPlayerWarStrategySequence();
        PlayersStrategyDTO toRandom = new PlayersStrategyDTO(strategy, warStrategy, "R", "R");
        StatisticsDTO compareToRandom = getStatisticsForTwoPlayers(toRandom);

        PlayersStrategyDTO toGetHigher = new PlayersStrategyDTO(strategy, warStrategy, "H", "R");
        StatisticsDTO compareToGetHigher = getStatisticsForTwoPlayers(toGetHigher);

        PlayersStrategyDTO toGetLower = new PlayersStrategyDTO(strategy, warStrategy, "L", "R");
        StatisticsDTO compareToGetLower = getStatisticsForTwoPlayers(toGetLower);

        PlayersStrategyDTO greedy = new PlayersStrategyDTO(strategy, warStrategy, "G", "R");
        StatisticsDTO compareToGreedy = getStatisticsForTwoPlayers(greedy);

        PlayersStrategyDTO greedyA = new PlayersStrategyDTO(strategy, warStrategy, "A", "R");
        StatisticsDTO compareToGreedyA = getStatisticsForTwoPlayers(greedyA);

        PlayersStrategyDTO greedyN = new PlayersStrategyDTO(strategy, warStrategy, "N", "R");
        StatisticsDTO compareToGreedyN = getStatisticsForTwoPlayers(greedyN);

        PlayersStrategyDTO greedyP = new PlayersStrategyDTO(strategy, warStrategy, "P", "R");
        StatisticsDTO compareToGreedyP = getStatisticsForTwoPlayers(greedyP);

        PlayersStrategyDTO greedyS = new PlayersStrategyDTO(strategy, warStrategy, "S", "R");
        StatisticsDTO compareToGreedyS = getStatisticsForTwoPlayers(greedyS);

        PlayersStrategyDTO greedyB = new PlayersStrategyDTO(strategy, warStrategy, "B", "R");
        StatisticsDTO compareToGreedyB = getStatisticsForTwoPlayers(greedyB);

        PlayersStrategyDTO greedyC = new PlayersStrategyDTO(strategy, warStrategy, "C", "R");
        StatisticsDTO compareToGreedyC = getStatisticsForTwoPlayers(greedyC);

        PlayersStrategyDTO greedyZ = new PlayersStrategyDTO(strategy, warStrategy, "Z", "R");
        StatisticsDTO compareToGreedyZ = getStatisticsForTwoPlayers(greedyZ);


        result.add(compareToRandom);
        result.add(compareToGetHigher);
        result.add(compareToGetLower);
        result.add(compareToGreedy);
        result.add(compareToGreedyA);
        result.add(compareToGreedyN);
        result.add(compareToGreedyP);
        result.add(compareToGreedyS);
        result.add(compareToGreedyB);
        result.add(compareToGreedyC);
        result.add(compareToGreedyZ);
        return result;
    }


    //compare strength
    public StatisticsDTO compareStrength(StrengthDTO strengthDTO) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int roundsCounter = 0;
        int warCounter = 0;
        for (int i = 0; i < GAME_AMOUNT; i++) {
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
            warCounter += result.getWarCounter();

        }
        StatisticsDTO stats = new StatisticsDTO();
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setDraws(drawCounter * 100.0 / GAME_AMOUNT);
        stats.setPlayersStrategyDTO(strengthDTO.getPlayersStrategyDTO());
        stats.setAverageAmountOfRounds(roundsCounter / GAME_AMOUNT);
        stats.setAverageAmountOfWars(warCounter / GAME_AMOUNT);

        return stats;
    }


    public RoundInfo gameWithStrategiesForStrenghtComparison(StrengthDTO strengthDTO) {
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setStrategySequence(strengthDTO.getPlayersStrategyDTO().getFisrtPlayerStrategySequence());
        player2.setStrategySequence(strengthDTO.getPlayersStrategyDTO().getSecondPlayerStrategySequence());
        player1.setWarStrategySequence(strengthDTO.getPlayersStrategyDTO().getFisrtPlayerWarStrategySequence());
        player2.setWarStrategySequence(strengthDTO.getPlayersStrategyDTO().getSecondPlayerWarStrategySequence());
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
        while (GameUtils.playerHasCards(player1) && GameUtils.playerHasCards(player2) && counter < ROUND_LIMIT) {
            if (GameUtils.getPlayerCard(player1).getRank() > GameUtils.getPlayerCard(player2).getRank()) {
                handlePlayerWinWithStrategy(player1, player2, true);
            } else if (Objects.equals(GameUtils.getPlayerCard(player1).getRank(), GameUtils.getPlayerCard(player2).getRank())) {
                warCounter++;
                if (player1.getCards().size() == 1 || player2.getCards().size() == 1) {
                    playerCannotPlayWar = true;
                    break;
                }
                handleWar(player1, player2, true, new ArrayList<>());
            } else {
                handlePlayerWinWithStrategy(player1, player2, false);
            }
            counter++;
        }
        RoundInfo roundInfo = new RoundInfo();
        return GameUtils.getRoundInfo(roundInfo, player1, player2, playerCannotPlayWar, counter, warCounter);
    }

    private Integer calculateCardStrengthInDeck(Player player) {
        Integer deckStrength = 0;
        if (player.getCards().isEmpty()) {
            return 0;
        }
        for (CardDTO cardDTO : player.getCards()) {
            Integer c = cardDTO.getRank();
            deckStrength = deckStrength + getCardStrength(c);
        }
        return deckStrength / player.getCards().size();
    }

    private Integer getCardStrength(Integer cardRank) {
        //calculated using min-max normalization
        switch (cardRank) {
            case 0:
                return 0;
            case 1:
                return 45;
            case 2:
                return 317;
            case 3:
                return 397;
            case 4:
                return 476;
            case 5:
                return 635;
            case 6:
                return 1150;
            case 7:
                return 1720;
            case 8:
                return 2290;
            case 9:
                return 6050;
            case 10:
                return 10630;
            case 11:
                return 20750;
            case 12:
                return 100000;
            default:
                return 0;
        }
    }


}
