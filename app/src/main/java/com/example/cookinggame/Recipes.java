package com.example.cookinggame;

import com.example.cookinggame.Recipe.Step;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.cookinggame.Recipe.Action.DRAG;
import static com.example.cookinggame.Recipe.Action.FOLD;
import static com.example.cookinggame.Recipe.Action.SHAKE;
import static com.example.cookinggame.Recipe.Action.TAP;
import static com.example.cookinggame.Recipe.Action.TIMER;
import static com.example.cookinggame.Stage.ASSEMBLY;
import static com.example.cookinggame.Stage.COOKING;

/**
 * Every recipe in the book, in page order.
 *
 * Steps are played in the order listed. Each step says which stage it happens in;
 * gameplay switches between the Assembly and Cooking pages whenever that changes,
 * so a recipe can go Assembly -> Cooking -> Assembly (e.g. the taco).
 */
public final class Recipes {

    private Recipes() {}

    public static final List<Recipe> ALL = Collections.unmodifiableList(Arrays.asList(

            new Recipe("birria_quesadilla", "Birria Quesadilla", R.drawable.food_birria_quesadilla,
                    new String[]{"Tortilla", "Birria beef", "Cheese", "Onion & cilantro", "Consomme"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, "Drag the tortilla onto the cutting board."),
                            new Step(ASSEMBLY, DRAG, "Drag the cheese and birria beef onto the tortilla."),
                            new Step(ASSEMBLY, FOLD, "Fold your phone to fold the quesadilla in half."),
                            new Step(COOKING, DRAG, "Drag the quesadilla onto the hot pan."),
                            new Step(COOKING, TIMER, "Let it crisp until the timer rings."),
                            new Step(COOKING, TAP, "Tap to dip it in the consomme. Enjoy!"),
                    }),

            new Recipe("taco", "Taco", R.drawable.food_taco,
                    new String[]{"Tortilla", "Ground beef", "Lettuce", "Tomato", "Cheese"},
                    new Step[]{
                            new Step(ASSEMBLY, SHAKE, "Shake your phone to wash the lettuce."),
                            new Step(COOKING, DRAG, "Drag the ground beef into the pan."),
                            new Step(COOKING, TIMER, "Cook the beef until the timer rings."),
                            new Step(ASSEMBLY, DRAG, "Drag the beef, lettuce, tomato and cheese onto the tortilla."),
                            new Step(ASSEMBLY, FOLD, "Fold your phone to fold the taco."),
                    }),

            new Recipe("smoothie", "Smoothie", R.drawable.food_smoothie,
                    new String[]{"Chocolate bar", "Granola bar", "Banana", "Strawberries", "Milk"},
                    new Step[]{
                            new Step(ASSEMBLY, FOLD, "Fold your phone to break the chocolate bar."),
                            new Step(ASSEMBLY, FOLD, "Fold again to break the granola bar."),
                            new Step(ASSEMBLY, TAP, "Tap the fruit to chop it."),
                            new Step(ASSEMBLY, DRAG, "Drag everything into the blender."),
                            new Step(COOKING, SHAKE, "Shake your phone to blend!"),
                            new Step(COOKING, DRAG, "Drag the blender to pour into a cup."),
                    }),

            new Recipe("crepes", "Crepes", R.drawable.food_crepes,
                    new String[]{"Flour", "Eggs", "Milk", "Butter", "Strawberries"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, "Drag the flour, eggs and milk into the bowl."),
                            new Step(ASSEMBLY, SHAKE, "Shake your phone to whisk the batter."),
                            new Step(COOKING, DRAG, "Drag the batter onto the buttered pan."),
                            new Step(COOKING, TIMER, "Cook until the timer rings."),
                            new Step(COOKING, FOLD, "Fold your phone to fold the crepe."),
                            new Step(ASSEMBLY, DRAG, "Drag strawberries on top."),
                    }),

            new Recipe("omelette", "Omelette", R.drawable.food_omelette,
                    new String[]{"Eggs", "Cheese", "Ham", "Bell pepper"},
                    new Step[]{
                            new Step(ASSEMBLY, TAP, "Tap the eggs to crack them into the bowl."),
                            new Step(ASSEMBLY, SHAKE, "Shake your phone to whisk the eggs."),
                            new Step(COOKING, DRAG, "Drag the eggs into the pan."),
                            new Step(COOKING, DRAG, "Drag the cheese, ham and peppers on top."),
                            new Step(COOKING, FOLD, "Fold your phone to fold the omelette."),
                            new Step(COOKING, TIMER, "Cook until golden when the timer rings."),
                    })
    ));

    /** Looks up a recipe by its id, or returns null if there is none. */
    public static Recipe byId(String id) {
        for (Recipe recipe : ALL) {
            if (recipe.id.equals(id)) {
                return recipe;
            }
        }
        return null;
    }
}
