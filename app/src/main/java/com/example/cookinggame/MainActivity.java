package com.example.cookinggame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

//Tracking Folding
import androidx.appcompat.app.AppCompatActivity;
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;
import androidx.window.layout.FoldingFeature;

import java.util.concurrent.Executor;
import androidx.core.util.Consumer;

public class MainActivity extends AppCompatActivity {

    Button playButton;
    private TextView waitingMsg;

    private boolean playPressed = false;
    private boolean recipeOpened = false;

    //folding tracking
    private WindowInfoTrackerCallbackAdapter windowTracker;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final Executor mainExecutor = command -> mainHandler.post(command);

    private final Consumer<WindowLayoutInfo> layoutListener = this::checkFoldState;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.playButton);
        waitingMsg = findViewById(R.id.waitingMsg);

        if (savedInstanceState != null) {
            playPressed = savedInstanceState.getBoolean("playPressed");
            recipeOpened = savedInstanceState.getBoolean("recipeOpened");

            // If the book was already opened before this activity was recreated,
            // the player is coming back from the recipe book: show the title again.
            if (recipeOpened) {
                resetToTitle();
            } else if (playPressed) {
                playButton.setVisibility(TextView.GONE);
                waitingMsg.setVisibility(TextView.VISIBLE);
            }
        }

        windowTracker = new WindowInfoTrackerCallbackAdapter(
                WindowInfoTracker.getOrCreate(this)
        );

        playButton.setOnClickListener(v -> {
            playPressed = true;

            playButton.setVisibility(TextView.GONE);
            waitingMsg.setVisibility(TextView.VISIBLE);
        });

    }

    // Called when the player presses Back from the recipe book.
    // Runs before onStart re-registers the fold listener, so the book
    // won't instantly reopen; the player presses play again instead.
    @Override
    protected void onRestart() {
        super.onRestart();
        if (recipeOpened) {
            resetToTitle();
        }
    }

    private void resetToTitle() {
        playPressed = false;
        recipeOpened = false;
        playButton.setVisibility(TextView.VISIBLE);
        waitingMsg.setVisibility(TextView.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        windowTracker.addWindowLayoutInfoListener(
                this,
                mainExecutor,
                layoutListener
        );
    }

    @Override
    protected void onStop() {
        windowTracker.removeWindowLayoutInfoListener(
                layoutListener
        );

        super.onStop();
    }

    private void checkFoldState(WindowLayoutInfo layoutInfo) {

        if (!playPressed || recipeOpened) {
            return;
        }

        boolean isHalfOpened = false;
        boolean hasFoldFeature = false;

        for (androidx.window.layout.DisplayFeature feature :
                layoutInfo.getDisplayFeatures()) {

            if (feature instanceof FoldingFeature) {

                hasFoldFeature = true;

                FoldingFeature foldingFeature =
                        (FoldingFeature) feature;

                if (foldingFeature.getState()
                        == FoldingFeature.State.HALF_OPENED) {

                    isHalfOpened = true;
                }
            }
        }

        // Pixel Fold inner screen is substantially wider
        // than its outer cover screen.

        int screenWidthDp =
                getResources().getConfiguration().screenWidthDp;

        boolean isUnfolded =
                screenWidthDp >= 600 && !isHalfOpened;

        if (isUnfolded) {
            openRecipeBook();
        }
    }

    private void openRecipeBook() {

        if (recipeOpened) {
            return;
        }

        recipeOpened = true;

        Intent intent = new Intent(
                MainActivity.this,
                RecipeBook.class
        );

        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("playPressed", playPressed);
        outState.putBoolean("recipeOpened", recipeOpened);
        super.onSaveInstanceState(outState);
    }
}
