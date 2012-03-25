package com.palmergames.bukkit.towny.question.towny;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.question.QuestionTask;

public abstract class TownyQuestionTask extends QuestionTask {
    protected Towny towny;

    protected TownyUniverse universe;

    public TownyUniverse getUniverse() {
        return universe;
    }

    public void setTowny(Towny towny) {
        this.towny = towny;
        this.universe = towny.getTownyUniverse();
    }

    @Override
    public abstract void run();

}
