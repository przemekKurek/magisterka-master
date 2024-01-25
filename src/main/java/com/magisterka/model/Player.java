package com.magisterka.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Player {
    private List<CardDTO> cards = new ArrayList<>();
    private String strategySequence;
    private String warStrategySequence;
    private Long winCounter = 0L;
    private Long warWinCounter = 0L;

    public void winCounterIncrement() {
        this.winCounter++;
    }

    public void warWinCounterIncrement() {
        this.warWinCounter++;
    }

    public Player(Player other) {
        this.cards = new ArrayList<>(other.cards);  // Creating a new list with the same elements
        this.strategySequence = other.strategySequence;
        this.warStrategySequence = other.warStrategySequence;
        this.winCounter = other.winCounter;
        this.warWinCounter = other.warWinCounter;
    }


}
