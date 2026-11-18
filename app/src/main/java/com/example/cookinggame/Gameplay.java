package com.example.cookinggame;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Plays one recipe as a book: Assembly and Cooking pages for each step,
 * then Results, then back to the recipe book.
 *
 * The recipe's steps decide which stage is showing (see Recipes). Mechanics
 * live in the GameStage subclasses and call completeCurrentStep() when done.
 */
public class Gameplay extends AppCompatActivity {

    /** Intent extra holding the chosen Recipe's id (see Recipes.byId). */
    public static final String EXTRA_RECIPE_ID = "recipeId";

    private static final String KEY_STEP = "stepIndex";
    private static final String KEY_START = "startTime";
    private static final String KEY_FINISH = "finishTime";

    private Recipe recipe;
    private int stepIndex = 0;
    private long startTime;
    private long finishTime = 0;

    private BookSpreadView book;
    private FrameLayout leftPage;
    private FrameLayout rightPage;
    private View cover;
    private GameStage stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recipe = Recipes.byId(getIntent().getStringExtra(EXTRA_RECIPE_ID));
        if (recipe == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_gameplay);
        book = findViewById(R.id.book);
        leftPage = findViewById(R.id.bookLeftPage);
        rightPage = findViewById(R.id.bookRightPage);
        cover = findViewById(R.id.bookCover);

        if (savedInstanceState != null) {
            stepIndex = savedInstanceState.getInt(KEY_STEP, 0);
            startTime = savedInstanceState.getLong(KEY_START);
            finishTime = savedInstanceState.getLong(KEY_FINISH);
        } else {
            startTime = SystemClock.elapsedRealtime();
        }

        book.setOnStateChangedListener(state -> {
            if (stage != null) stage.onBookStateChanged(state);
        });
        cover.findViewById(R.id.stepDoneButton).setOnClickListener(v -> completeCurrentStep());

        showCurrentStep();
    }

    // ---- For stages ----

    public Recipe getRecipe() {
        return recipe;
    }

    /** The step being played, or null once the recipe is finished (Results). */
    public Recipe.Step getCurrentStep() {
        return stepIndex < recipe.steps.length ? recipe.steps[stepIndex] : null;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    /** Whether the phone is folded (COVER), half open or open. */
    public BookSpreadView.State getBookState() {
        return book.getState();
    }

    /** How long the player has been cooking (stops counting at Results). */
    public long getElapsedMillis() {
        long end = finishTime > 0 ? finishTime : SystemClock.elapsedRealtime();
        return end - startTime;
    }

    /** Call when the player has finished the current step; moves to the next step or stage. */
    public void completeCurrentStep() {
        if (stepIndex >= recipe.steps.length) return;
        stepIndex++;
        if (stepIndex == recipe.steps.length) {
            finishTime = SystemClock.elapsedRealtime();
        }
        showCurrentStep();
    }

    /** Restart this recipe from the first step. */
    public void cookAgain() {
        stepIndex = 0;
        startTime = SystemClock.elapsedRealtime();
        finishTime = 0;
        showCurrentStep();
    }

    /** Close the game and go back to the recipe book (it stays on this recipe's page). */
    public void returnToRecipeBook() {
        finish();
    }

    // ---- Stage switching ----

    private void showCurrentStep() {
        Recipe.Step step = getCurrentStep();
        Stage wanted = step != null ? step.stage : Stage.RESULTS;

        if (stage == null || stage.getType() != wanted) {
            switchTo(createStage(wanted));
        }

        bindStepViews(leftPage, step);
        bindStepViews(rightPage, step);
        bindCover(step);
        stage.onStepChanged(step, stepIndex);
    }

    private GameStage createStage(Stage type) {
        switch (type) {
            case ASSEMBLY: return new AssemblyStage(this);
            case COOKING:  return new CookingStage(this);
            default:       return new ResultsStage(this);
        }
    }

    private void switchTo(GameStage next) {
        if (stage != null) stage.onExit();

        leftPage.removeAllViews();
        rightPage.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        View left = inflater.inflate(next.getLeftPageLayout(), leftPage, false);
        View right = inflater.inflate(next.getRightPageLayout(), rightPage, false);
        leftPage.addView(left);
        rightPage.addView(right);

        // Placeholder "DONE" buttons skip the step until the real mechanic is in.
        hookDoneButton(left);
        hookDoneButton(right);

        stage = next;
        stage.attach(left, right);
        stage.onEnter();
    }

    private void hookDoneButton(View page) {
        View done = page.findViewById(R.id.stepDoneButton);
        if (done != null) done.setOnClickListener(v -> completeCurrentStep());
    }

    /** Fills in the shared step views (any that exist on this page). */
    private void bindStepViews(View root, Recipe.Step step) {
        setText(root, R.id.recipeName, recipe.name);
        setText(root, R.id.stageTitle, stage.getType().label);
        if (step != null) {
            setText(root, R.id.stepCounter,
                    getString(R.string.step_counter, stepIndex + 1, recipe.steps.length));
            setText(root, R.id.stepAction, step.action.label);
            setText(root, R.id.stepInstruction, step.text);
        }
    }

    private void bindCover(Recipe.Step step) {
        bindStepViews(cover, step);
        boolean finished = step == null;
        setShown(cover, R.id.stepCounter, !finished);
        setShown(cover, R.id.stepAction, !finished);
        setShown(cover, R.id.stepDoneButton, !finished);
        if (finished) {
            setText(cover, R.id.stepInstruction, getString(R.string.results_made, recipe.name));
        }
        setText(cover, R.id.coverHint, getString(
                finished ? R.string.cover_open_for_results : R.string.cover_open_to_cook));
    }

    private static void setText(View root, int id, CharSequence text) {
        TextView view = root.findViewById(id);
        if (view != null) view.setText(text);
    }

    private static void setShown(View root, int id, boolean shown) {
        View view = root.findViewById(id);
        if (view != null) view.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (stage != null) stage.onExit();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_STEP, stepIndex);
        outState.putLong(KEY_START, startTime);
        outState.putLong(KEY_FINISH, finishTime);
        super.onSaveInstanceState(outState);
    }
}
