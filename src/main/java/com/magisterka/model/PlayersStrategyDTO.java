package com.magisterka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayersStrategyDTO implements Serializable {
    private String fisrtPlayerStrategySequence;
    private String fisrtPlayerWarStrategySequence;
    private String secondPlayerStrategySequence;
    private String secondPlayerWarStrategySequence;

    public PlayersStrategyDTO(String fisrtPlayerStrategySequence, String secondPlayerStrategySequence) {
        this.fisrtPlayerStrategySequence = fisrtPlayerStrategySequence;
        this.secondPlayerStrategySequence = secondPlayerStrategySequence;
    }
}
