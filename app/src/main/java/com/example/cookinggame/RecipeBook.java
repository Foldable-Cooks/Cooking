package com.example.cookinggame;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * The recipe book. Folded it shows the cover; open, the left page lists every
 * recipe and the right page shows the selected recipe's picture, ingredients and
 * steps, plus a button to start cooking. BookSpreadView puts the spine on the hinge.
 */
public class RecipeBook extends AppCompatActivity {

    private static final String KEY_SELECTED = "selectedRecipe";

    private int selected = 0;

    private final List<TextView> indexItems = new ArrayList<>();
    private TextView recipeTitle;
    private TextView recipeIngredients;
    private TextView pageNumber;
    private ImageView recipeImage;
    private LinearLayout recipeSteps;
    private ScrollView recipeScroll;
    private TextView coverBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipebook);

        recipeTitle = findViewById(R.id.recipeTitle);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        pageNumber = findViewById(R.id.pageNumber);
        recipeImage = findViewById(R.id.recipeImage);
        recipeSteps = findViewById(R.id.recipeSteps);
        recipeScroll = findViewById(R.id.recipeScroll);
        coverBookmark = findViewById(R.id.coverBookmark);

        // Folding/unfolding recreates the activity, so remember the open page.
        if (savedInstanceState != null) {
            selected = savedInstanceState.getInt(KEY_SELECTED, 0);
        }

        buildIndex();

        int count = Recipes.ALL.size();
        findViewById(R.id.prevPageButton).setOnClickListener(
                v -> showRecipe((selected - 1 + count) % count));
        findViewById(R.id.nextPageButton).setOnClickListener(
                v -> showRecipe((selected + 1) % count));

        findViewById(R.id.startCookingButton).setOnClickListener(v -> {
            Intent intent = new Intent(RecipeBook.this, Gameplay.class);
            intent.putExtra(Gameplay.EXTRA_RECIPE_ID, Recipes.ALL.get(selected).id);
            startActivity(intent);
        });

        showRecipe(selected);
    }

    /** Fills the left page with one button per recipe. */
    private void buildIndex() {
        LinearLayout list = findViewById(R.id.recipeIndexList);
        LayoutInflater inflater = getLayoutInflater();

        for (int i = 0; i < Recipes.ALL.size(); i++) {
            TextView item = (TextView) inflater.inflate(R.layout.item_recipe_index, list, false);
            item.setText((i + 1) + ". " + Recipes.ALL.get(i).name);

            final int index = i;
            item.setOnClickListener(v -> showRecipe(index));

            list.addView(item);
            indexItems.add(item);
        }
    }

    /** Turns the right page to the recipe at the given index. */
    private void showRecipe(int index) {
        selected = index;
        Recipe recipe = Recipes.ALL.get(index);

        recipeTitle.setText(recipe.name);
        coverBookmark.setText(getString(R.string.cover_bookmark, recipe.name));
        PixelArt.setImage(recipeImage, recipe.imageRes);

        StringBuilder ingredients = new StringBuilder();
        for (String ingredient : recipe.ingredients) {
            if (ingredients.length() > 0) ingredients.append('\n');
            ingredients.append("- ").append(ingredient);
        }
        recipeIngredients.setText(ingredients);

        recipeSteps.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < recipe.steps.length; i++) {
            Recipe.Step step = recipe.steps[i];
            View row = inflater.inflate(R.layout.item_recipe_step, recipeSteps, false);
            ((TextView) row.findViewById(R.id.stepNumber)).setText((i + 1) + ".");
            ((TextView) row.findViewById(R.id.stepAction)).setText(step.action.label);
            ((TextView) row.findViewById(R.id.stepText)).setText(step.text);
            recipeSteps.addView(row);
        }

        pageNumber.setText(getString(R.string.page_number, index + 1, Recipes.ALL.size()));

        for (int i = 0; i < indexItems.size(); i++) {
            indexItems.get(i).setSelected(i == index);
        }

        recipeScroll.scrollTo(0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED, selected);
        super.onSaveInstanceState(outState);
    }
}
