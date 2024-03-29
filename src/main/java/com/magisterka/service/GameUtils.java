package com.magisterka.service;

import com.magisterka.model.CardDTO;
import com.magisterka.model.Player;
import com.magisterka.model.RoundInfo;

import java.util.*;

public class GameUtils {

    public static List<CardDTO> initializeDeck() {
        List<CardDTO> cards = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            cards.add(new CardDTO(i));
        }
        return cards;
    }

    public static void shuffleDeck(List<CardDTO> cards) {
        long seed = System.nanoTime(); // Using nanoTime for a more fine-grained seed
        Collections.shuffle(cards, new Random(seed));
    }

    public static void assignCardsToPlayers(List<CardDTO> cards, Player player1, Player player2) {
        shuffleDeck(cards);
        for (int i = 0; i < 52; i = i + 2) {
            player1.getCards().add(cards.get(i));
            player2.getCards().add(cards.get(i + 1));
        }
    }

    public static boolean playerHasCards(Player player) {
        return !player.getCards().isEmpty();
    }

    public static CardDTO getPlayerCard(Player player) {
        return player.getCards().get(0);
    }

    public static void sortCardsAscending(List<CardDTO> cards) {
        cards.sort(Comparator.comparing(CardDTO::getCardNumber));
    }

    public static void sortCardsDescending(List<CardDTO> cards) {
        cards.sort(Comparator.comparing(CardDTO::getCardNumber, Comparator.reverseOrder()));
    }

    static RoundInfo getRoundInfo(RoundInfo roundInfo, Player player1, Player player2, boolean playerCannotPlayWar, int counter, int warCounter) {
        roundInfo.setRoundLength(counter);
        roundInfo.setWarCounter(warCounter);
        if (playerCannotPlayWar) {
            roundInfo.setRoundResult(player2.getCards().size() == 1 ? 1 : 2);
            return roundInfo;
        }
        if (player2.getCards().isEmpty()) {
            roundInfo.setRoundResult(1);
        } else if (player1.getCards().isEmpty()) {
            roundInfo.setRoundResult(2);
        } else {
            roundInfo.setRoundResult(0);
        }
        return roundInfo;
    }



}
