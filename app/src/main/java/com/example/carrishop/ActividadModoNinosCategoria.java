package com.example.carrishop;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActividadModoNinosCategoria extends AppCompatActivity implements KidsAudioButtonAdapter.OnItemClickListener {

    public static final String EXTRA_CATEGORY = "extra_category";

    private MediaPlayer mediaPlayer;
    private TextView titleView;
    private TextView descriptionView;
    private KidsCategory currentCategory = KidsCategory.ANIMALS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_ninos_categoria);

        titleView = findViewById(R.id.txtKidsCategoryTitle);
        descriptionView = findViewById(R.id.txtKidsCategoryDescription);
        ImageButton closeButton = findViewById(R.id.btnCloseCategory);
        RecyclerView recyclerView = findViewById(R.id.recyclerKidButtons);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        KidsAudioButtonAdapter adapter = new KidsAudioButtonAdapter(this);
        recyclerView.setAdapter(adapter);

        closeButton.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        if (intent != null) {
            String categoryValue = intent.getStringExtra(EXTRA_CATEGORY);
            currentCategory = KidsCategory.from(categoryValue);
        }

        titleView.setText(getTitleFor(currentCategory));
        descriptionView.setText(getDescriptionFor(currentCategory));
        adapter.submitList(buildItemsFor(currentCategory));
    }

    @Override
    public void onItemClick(KidsCategoryItem item) {
        playAudio(item.getAudioName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void playAudio(@Nullable String audioName) {
        if (audioName == null || audioName.trim().isEmpty()) {
            Toast.makeText(this, R.string.kids_missing_audio_generic, Toast.LENGTH_SHORT).show();
            return;
        }

        releasePlayer();
        int resId = getResources().getIdentifier(audioName, "raw", getPackageName());
        if (resId == 0) {
            // Recordatorio visual para que coloques tu archivo en res/raw.
            String message = getString(R.string.kids_missing_audio_specific, audioName);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return;
        }

        mediaPlayer = MediaPlayer.create(this, resId);
        if (mediaPlayer == null) {
            Toast.makeText(this, R.string.kids_missing_audio_generic, Toast.LENGTH_SHORT).show();
            return;
        }
        mediaPlayer.setOnCompletionListener(mp -> releasePlayer());
        mediaPlayer.start();
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private CharSequence getTitleFor(KidsCategory category) {
        switch (category) {
            case COLORS:
                return getString(R.string.modo_kids_category_colors);
            case NUMBERS:
                return getString(R.string.modo_kids_category_numbers);
            case FRUITS:
                return getString(R.string.modo_kids_category_fruits);
            case VOWELS:
                return getString(R.string.modo_kids_category_vowels);
            case ANIMALS:
            default:
                return getString(R.string.modo_kids_category_animals);
        }
    }

    private CharSequence getDescriptionFor(KidsCategory category) {
        switch (category) {
            case COLORS:
                return getString(R.string.kids_desc_colors);
            case NUMBERS:
                return getString(R.string.kids_desc_numbers);
            case FRUITS:
                return getString(R.string.kids_desc_fruits);
            case VOWELS:
                return getString(R.string.kids_desc_vowels);
            case ANIMALS:
            default:
                return getString(R.string.kids_desc_animals);
        }
    }

    private List<KidsCategoryItem> buildItemsFor(KidsCategory category) {
        List<KidsCategoryItem> items = new ArrayList<>();
        @ColorInt int orange = ContextCompat.getColor(this, R.color.kids_orange);
        @ColorInt int green = ContextCompat.getColor(this, R.color.kids_green);
        @ColorInt int blue = ContextCompat.getColor(this, R.color.kids_blue);
        @ColorInt int pink = ContextCompat.getColor(this, R.color.kids_pink);
        @ColorInt int purple = ContextCompat.getColor(this, R.color.kids_purple);
        @ColorInt int yellow = ContextCompat.getColor(this, R.color.kids_yellow);
        @ColorInt int red = ContextCompat.getColor(this, R.color.kids_red);
        @ColorInt int teal = ContextCompat.getColor(this, R.color.kids_teal);
        @ColorInt int brown = ContextCompat.getColor(this, R.color.kids_brown);
        @ColorInt int black = ContextCompat.getColor(this, R.color.black);

        switch (category) {
            case COLORS:
                items.add(new KidsCategoryItem("color_azul", getString(R.string.kids_color_blue), "🔵", blue, "color_azul"));
                items.add(new KidsCategoryItem("color_marron", getString(R.string.kids_color_brown), "🟤", brown, "color_marron"));
                items.add(new KidsCategoryItem("color_naranja", getString(R.string.kids_color_orange), "🟠", orange, "color_naranja"));
                items.add(new KidsCategoryItem("color_negro", getString(R.string.kids_color_black), "⚫", black, "color_negro"));
                items.add(new KidsCategoryItem("color_purpura", getString(R.string.kids_color_purple), "🟣", purple, "color_purpura"));
                items.add(new KidsCategoryItem("color_rojo", getString(R.string.kids_color_red), "🔴", red, "color_rojo"));
                items.add(new KidsCategoryItem("color_rosado", getString(R.string.kids_color_pink), "🌸", pink, "color_rosado"));
                items.add(new KidsCategoryItem("color_verde", getString(R.string.kids_color_green), "🟢", green, "color_verde"));
                items.add(new KidsCategoryItem("color_yellow", getString(R.string.kids_color_yellow), "🟡", yellow, "color_yellow"));
                break;
            case NUMBERS:
                for (int number = 1; number <= 10; number++) {
                    items.add(new KidsCategoryItem(
                            "numero_" + number,
                            getString(R.string.kids_number_label, number),
                            String.valueOf(number),
                            blue,
                            "numeros_1_10" // Un solo audio para los diez números.
                    ));
                }
                break;
            case FRUITS:
                String fruitAudio = "frutas"; // Un solo audio guía para todas las frutas.
                items.add(new KidsCategoryItem("fruta_manzana", getString(R.string.kids_fruit_apple), "🍎", red, fruitAudio));
                items.add(new KidsCategoryItem("fruta_platano", getString(R.string.kids_fruit_banana), "🍌", yellow, fruitAudio));
                items.add(new KidsCategoryItem("fruta_naranja", getString(R.string.kids_fruit_orange), "🍊", orange, fruitAudio));
                items.add(new KidsCategoryItem("fruta_uvas", getString(R.string.kids_fruit_grape), "🍇", purple, fruitAudio));
                items.add(new KidsCategoryItem("fruta_sandia", getString(R.string.kids_fruit_watermelon), "🍉", green, fruitAudio));
                break;
            case VOWELS:
                items.add(new KidsCategoryItem("vocal_a", getString(R.string.kids_vowel_a), "A", pink, "vocales"));
                items.add(new KidsCategoryItem("vocal_e", getString(R.string.kids_vowel_e), "E", orange, "vocales"));
                items.add(new KidsCategoryItem("vocal_i", getString(R.string.kids_vowel_i), "I", blue, "vocales"));
                items.add(new KidsCategoryItem("vocal_o", getString(R.string.kids_vowel_o), "O", green, "vocales"));
                items.add(new KidsCategoryItem("vocal_u", getString(R.string.kids_vowel_u), "U", purple, "vocales"));
                break;
            case ANIMALS:
            default:
                items.add(new KidsCategoryItem("animal_ave", getString(R.string.kids_animal_bird), "🕊️", blue, "animal_ave"));
                items.add(new KidsCategoryItem("animal_chivo", getString(R.string.kids_animal_goat), "🐐", brown, "animal_chivo"));
                items.add(new KidsCategoryItem("animal_gallo", getString(R.string.kids_animal_rooster), "🐓", orange, "animal_gallo"));
                items.add(new KidsCategoryItem("animal_gato", getString(R.string.kids_animal_cat), "🐱", pink, "animal_gato"));
                items.add(new KidsCategoryItem("animal_perro", getString(R.string.kids_animal_dog), "🐶", teal, "animal_perro"));
                break;
        }
        return items;
    }

    enum KidsCategory {
        ANIMALS,
        COLORS,
        NUMBERS,
        FRUITS,
        VOWELS;

        static KidsCategory from(@Nullable String value) {
            if (value == null) {
                return ANIMALS;
            }
            try {
                return KidsCategory.valueOf(value);
            } catch (IllegalArgumentException exception) {
                return ANIMALS;
            }
        }
    }
}
