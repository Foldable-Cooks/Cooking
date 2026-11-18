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
        /** The workstation on the right side of the table during this step. */
        public final Station station;
        public final String text;
        /**
         * Items the player must drag onto the station to finish the step.
         * Usually ingredient names; anything else (e.g. "Batter") appears in the
         * tray just for this step. Empty for steps that use another mechanic.
         */
        public final String[] needs;
        /**
         * For fold-to-split steps: the item on the station that breaks in half when
         * the player folds and reopens the phone. It becomes halved(item). Otherwise null.
         */
        public final String splits;

        public Step(Stage stage, Action action, Station station, String text, String... needs) {
            this(stage, action, station, text, null, needs);
        }

        private Step(Stage stage, Action action, Station station, String text,
                     String splits, String[] needs) {
            this.stage = stage;
            this.action = action;
            this.station = station;
            this.text = text;
            this.splits = splits;
            this.needs = needs;
        }

        /** A FOLD step that breaks `item` (already on the station) in half. */
        public static Step split(Stage stage, Station station, String text, String item) {
            return new Step(stage, Action.FOLD, station, text, item, new String[0]);
        }

        public boolean needs(String item) {
            for (String need : needs) {
                if (need.equals(item)) return true;
            }
            return false;
        }

        /** True if gameplay can finish this step by itself (drag or fold-to-split). */
        public boolean hasMechanic() {
            return needs.length > 0 || splits != null;
        }
    }

    /** Name of an item after it's been split: "Mango" -> "Halved mango". */
    public static String halved(String item) {
        return "Halved " + item.toLowerCase(java.util.Locale.US);
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
