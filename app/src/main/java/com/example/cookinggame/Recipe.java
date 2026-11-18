package com.example.cookinggame;

import androidx.annotation.DrawableRes;

/**
 * One recipe in the recipe book: its picture, ingredients and the steps
 * the player will perform during gameplay.
 */
public class Recipe {

    /** What the player physically does for a step (shown as a tag in the book). */
    public enum Action {
        TAP("TAP"),
        DRAG("DRAG"),
        FOLD("FOLD"),
        SHAKE("SHAKE"),
        TIMER("TIMER");

        public final String label;

        Action(String label) {
            this.label = label;
        }
    }

    public static class Step {
        /** Which gameplay stage (ASSEMBLY or COOKING) this step is played in. */
        public final Stage stage;
        public final Action action;
        public final String text;

        public Step(Stage stage, Action action, String text) {
            this.stage = stage;
            this.action = action;
            this.text = text;
        }
    }

    public final String id;
    public final String name;
    @DrawableRes
    public final int imageRes;
    public final String[] ingredients;
    public final Step[] steps;

    public Recipe(String id, String name, @DrawableRes int imageRes,
                  String[] ingredients, Step[] steps) {
        this.id = id;
        this.name = name;
        this.imageRes = imageRes;
        this.ingredients = ingredients;
        this.steps = steps;
    }
}
