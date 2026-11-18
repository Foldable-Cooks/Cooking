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
import static com.example.cookinggame.Recipe.halved;
import static com.example.cookinggame.Stage.ASSEMBLY;
import static com.example.cookinggame.Stage.COOKING;
import static com.example.cookinggame.Station.BLENDER;
import static com.example.cookinggame.Station.BOARD;
import static com.example.cookinggame.Station.BOWL;
import static com.example.cookinggame.Station.CUP;
import static com.example.cookinggame.Station.PAN;
import static com.example.cookinggame.Station.PLATE;

/**
 * Every recipe in the book, in page order.
 *
 * Each step: stage, action, station, instruction, then the items to drag onto the
 * station (DRAG steps only). Steps play in order; gameplay switches between the
 * Assembly and Cooking stages whenever a step's stage changes.
 */
public final class Recipes {

    private Recipes() {}

    public static final List<Recipe> ALL = Collections.unmodifiableList(Arrays.asList(

            new Recipe("birria_quesadilla", "Birria Quesadilla", R.drawable.food_birria_quesadilla,
                    new String[]{"Tortilla", "Birria beef", "Cheese", "Onion & cilantro", "Consomme"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, BOARD, "Drag the tortilla onto the cutting board.", "Tortilla"),
                            new Step(ASSEMBLY, DRAG, BOARD, "Drag the cheese, birria beef, onion & cilantro onto the tortilla.",
                                    "Cheese", "Birria beef", "Onion & cilantro"),
                            new Step(ASSEMBLY, FOLD, BOARD, "Fold your phone to fold the quesadilla in half."),
                            new Step(COOKING, DRAG, PAN, "Drag the quesadilla onto the hot pan.", "Quesadilla"),
                            new Step(COOKING, TIMER, PAN, "Let it crisp until the timer rings."),
                            new Step(COOKING, DRAG, BOWL, "Drag the consomme into the bowl for dipping. Enjoy!", "Consomme"),
                    }),

            new Recipe("taco", "Taco", R.drawable.food_taco,
                    new String[]{"Tortilla", "Ground beef", "Lettuce", "Tomato", "Cheese"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, BOWL, "Drag the lettuce into the bowl.", "Lettuce"),
                            new Step(ASSEMBLY, SHAKE, BOWL, "Shake your phone to wash the lettuce."),
                            new Step(COOKING, DRAG, PAN, "Drag the ground beef into the pan.", "Ground beef"),
                            new Step(COOKING, TIMER, PAN, "Cook the beef until the timer rings."),
                            new Step(ASSEMBLY, DRAG, BOARD, "Build the taco: tortilla, cooked beef, lettuce, tomato and cheese.",
                                    "Tortilla", "Cooked beef", "Lettuce", "Tomato", "Cheese"),
                            new Step(ASSEMBLY, FOLD, BOARD, "Fold your phone to fold the taco."),
                    }),

            new Recipe("smoothie", "Smoothie", R.drawable.food_smoothie,
                    new String[]{"Chocolate bar", "Granola bar", "Banana", "Strawberry", "Milk"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, BOARD, "Drag the chocolate and granola bars onto the board.",
                                    "Chocolate bar", "Granola bar"),
                            new Step(ASSEMBLY, FOLD, BOARD, "Fold your phone to break the bars."),
                            new Step(ASSEMBLY, TAP, BOARD, "Tap the fruit to chop it."),
                            new Step(ASSEMBLY, DRAG, BLENDER, "Drag everything into the blender.",
                                    "Chocolate bar", "Granola bar", "Banana", "Strawberry", "Milk"),
                            new Step(COOKING, SHAKE, BLENDER, "Shake your phone to blend!"),
                            new Step(COOKING, DRAG, CUP, "Drag the smoothie into the cup.", "Smoothie"),
                    }),

            new Recipe("crepes", "Crepes", R.drawable.food_crepes,
                    new String[]{"Flour", "Eggs", "Milk", "Butter", "Strawberry"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, BOWL, "Drag the flour, eggs and milk into the bowl.",
                                    "Flour", "Eggs", "Milk"),
                            new Step(ASSEMBLY, SHAKE, BOWL, "Shake your phone to whisk the batter."),
                            new Step(COOKING, DRAG, PAN, "Drag the butter and batter onto the pan.", "Butter", "Batter"),
                            new Step(COOKING, TIMER, PAN, "Cook until the timer rings."),
                            new Step(COOKING, FOLD, PAN, "Fold your phone to fold the crepe."),
                            new Step(ASSEMBLY, DRAG, PLATE, "Drag the crepe and strawberries onto the plate.",
                                    "Crepe", "Strawberry"),
                    }),

            new Recipe("omelette", "Omelette", R.drawable.food_omelette,
                    new String[]{"Eggs", "Cheese", "Ham", "Bell pepper"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, BOWL, "Drag the eggs into the bowl.", "Eggs"),
                            new Step(ASSEMBLY, SHAKE, BOWL, "Shake your phone to whisk the eggs."),
                            new Step(COOKING, DRAG, PAN, "Drag the whisked eggs into the pan.", "Whisked eggs"),
                            new Step(COOKING, DRAG, PAN, "Drag the cheese, ham and peppers on top.",
                                    "Cheese", "Ham", "Bell pepper"),
                            new Step(COOKING, FOLD, PAN, "Fold your phone to fold the omelette."),
                            new Step(COOKING, TIMER, PAN, "Cook until golden when the timer rings."),
                    }),

            // ---- Fruit recipes: fold the phone to break fruit in half ----

            new Recipe("tropical_smoothie", "Tropical Smoothie", R.drawable.ingredient_halved_mango,
                    new String[]{"Mango", "Coconut", "Pineapple", "Banana"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, BOARD, "Drag the mango onto the cutting board.", "Mango"),
                            Step.split(ASSEMBLY, BOARD, "Fold your phone to split the mango in half!", "Mango"),
                            new Step(ASSEMBLY, DRAG, BOARD, "Drag the coconut onto the board.", "Coconut"),
                            Step.split(ASSEMBLY, BOARD, "Fold your phone to crack the coconut open!", "Coconut"),
                            new Step(ASSEMBLY, DRAG, BLENDER, "Drag the mango, coconut, pineapple and banana into the blender.",
                                    halved("Mango"), halved("Coconut"), "Pineapple", "Banana"),
                            new Step(COOKING, SHAKE, BLENDER, "Shake your phone to blend!"),
                            new Step(COOKING, DRAG, CUP, "Drag the smoothie into the cup.", "Smoothie"),
                    }),

            new Recipe("berry_blast", "Berry Blast Smoothie", R.drawable.ingredient_halved_strawberry,
                    new String[]{"Strawberry", "Blueberries", "Raspberries", "Banana", "Yogurt"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, BOARD, "Drag the strawberry onto the cutting board.", "Strawberry"),
                            Step.split(ASSEMBLY, BOARD, "Fold your phone to slice the strawberry in half!", "Strawberry"),
                            new Step(ASSEMBLY, DRAG, BLENDER, "Drag the berries, banana and yogurt into the blender.",
                                    halved("Strawberry"), "Blueberries", "Raspberries", "Banana", "Yogurt"),
                            new Step(COOKING, SHAKE, BLENDER, "Shake your phone to blend!"),
                            new Step(COOKING, DRAG, CUP, "Drag the smoothie into the cup.", "Smoothie"),
                    }),

            new Recipe("citrus_sunrise", "Citrus Sunrise Juice", R.drawable.ingredient_halved_orange,
                    new String[]{"Orange", "Grapefruit", "Lemon slice", "Ice"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, BOARD, "Drag the orange onto the cutting board.", "Orange"),
                            Step.split(ASSEMBLY, BOARD, "Fold your phone to cut the orange in half!", "Orange"),
                            new Step(ASSEMBLY, DRAG, BOARD, "Drag the grapefruit onto the board.", "Grapefruit"),
                            Step.split(ASSEMBLY, BOARD, "Fold your phone to cut the grapefruit in half!", "Grapefruit"),
                            new Step(ASSEMBLY, DRAG, BOWL, "Drag the orange and grapefruit halves into the bowl.",
                                    halved("Orange"), halved("Grapefruit")),
                            new Step(COOKING, SHAKE, BOWL, "Shake your phone to squeeze out the juice!"),
                            new Step(COOKING, DRAG, CUP, "Drag the juice, ice and a lemon slice into the cup.",
                                    "Juice", "Ice", "Lemon slice"),
                    }),

            new Recipe("dragon_fruit_bowl", "Dragon Fruit Bowl", R.drawable.ingredient_halved_dragon_fruit,
                    new String[]{"Dragon fruit", "Kiwi", "Banana", "Blueberries", "Granola"},
                    new Step[]{
                            new Step(ASSEMBLY, DRAG, BOARD, "Drag the dragon fruit and kiwi onto the cutting board.",
                                    "Dragon fruit", "Kiwi"),
                            Step.split(ASSEMBLY, BOARD, "Fold your phone to split the dragon fruit!", "Dragon fruit"),
                            Step.split(ASSEMBLY, BOARD, "Fold again to split the kiwi!", "Kiwi"),
                            new Step(ASSEMBLY, DRAG, BLENDER, "Drag the dragon fruit and banana into the blender.",
                                    halved("Dragon fruit"), "Banana"),
                            new Step(COOKING, SHAKE, BLENDER, "Shake your phone to blend!"),
                            new Step(ASSEMBLY, DRAG, BOWL, "Pour the smoothie into the bowl.", "Smoothie"),
                            new Step(ASSEMBLY, DRAG, BOWL, "Top it with kiwi, blueberries and granola.",
                                    halved("Kiwi"), "Blueberries", "Granola"),
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
