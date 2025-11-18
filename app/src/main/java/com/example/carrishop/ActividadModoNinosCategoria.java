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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO: Ajusta estos imports si tus clases (Adapter, Item) están en otro paquete.
import com.example.carrishop.KidsAudioButtonAdapter;
import com.example.carrishop.KidsCategoryItem;

public class ActividadModoNinosCategoria extends AppCompatActivity implements KidsAudioButtonAdapter.OnItemClickListener {

    public static final String EXTRA_CATEGORY = "extra_category";
    private MediaPlayer mediaPlayer;
    private KidsCategory currentCategory = KidsCategory.ANIMALS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_ninos_categoria);

        TextView titleView = findViewById(R.id.txtKidsCategoryTitle);
        TextView descriptionView = findViewById(R.id.txtKidsCategoryDescription);
        ImageButton closeButton = findViewById(R.id.btnCloseCategory);
        RecyclerView recyclerView = findViewById(R.id.recyclerKidButtons);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        KidsAudioButtonAdapter adapter = new KidsAudioButtonAdapter(this);
        recyclerView.setAdapter(adapter);

        closeButton.setOnClickListener(v -> finish());

        // Este código ahora funcionará porque la actividad anterior SÍ está enviando un Serializable (el enum)
        Serializable categoryExtra = getIntent().getSerializableExtra(EXTRA_CATEGORY);
        if (categoryExtra instanceof KidsCategory) {
            currentCategory = (KidsCategory) categoryExtra;
        } else {
            // Si por alguna razón no llega la categoría, se usa una por defecto para evitar un crash.
            currentCategory = KidsCategory.ANIMALS;
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
            Toast.makeText(this, "Audio no especificado.", Toast.LENGTH_SHORT).show();
            return;
        }
        releasePlayer();
        int resId = getResources().getIdentifier(audioName, "raw", getPackageName());
        if (resId == 0) {
            String message = "Falta el archivo de audio: " + audioName + ".mp3 en res/raw.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            mediaPlayer = MediaPlayer.create(this, resId);
            if (mediaPlayer == null) {
                Toast.makeText(this, "No se pudo crear el reproductor de audio.", Toast.LENGTH_SHORT).show();
                return;
            }
            mediaPlayer.setOnCompletionListener(mp -> releasePlayer());
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(this, "Error al reproducir el sonido.", Toast.LENGTH_SHORT).show();
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private String getTitleFor(KidsCategory category) { return category.name(); }
    private String getDescriptionFor(KidsCategory category) { return "Toca un botón para escuchar el sonido"; }

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
                items.add(new KidsCategoryItem("color_azul", "Azul", "🔵", blue, "color_azul"));
                items.add(new KidsCategoryItem("color_marron", "Marrón", "🟤", brown, "color_marron"));
                items.add(new KidsCategoryItem("color_naranja", "Naranja", "🟠", orange, "color_naranja"));
                items.add(new KidsCategoryItem("color_negro", "Negro", "⚫", black, "color_negro"));
                items.add(new KidsCategoryItem("color_purpura", "Púrpura", "🟣", purple, "color_purpura"));
                items.add(new KidsCategoryItem("color_rojo", "Rojo", "🔴", red, "color_rojo"));
                items.add(new KidsCategoryItem("color_rosado", "Rosado", "🌸", pink, "color_rosado"));
                items.add(new KidsCategoryItem("color_verde", "Verde", "🟢", green, "color_verde"));
                items.add(new KidsCategoryItem("color_yellow", "Amarillo", "🟡", yellow, "color_yellow"));
                break;
            case NUMBERS:
                items.add(new KidsCategoryItem("numeros_general", "Números", "🔢", blue, "numeros_1_10"));
                break;
            case FRUITS:
                items.add(new KidsCategoryItem("frutas_general", "Frutas", "🍎🍌🍇", green, "frutas"));
                break;
            case VOWELS:
                items.add(new KidsCategoryItem("vocales_general", "Las Vocales", "A E I O U", pink, "vocales"));
                break;
            case ANIMALS:
            default:
                items.add(new KidsCategoryItem("animal_ave", "Ave", "🕊️", blue, "animal_ave"));
                items.add(new KidsCategoryItem("animal_chivo", "Chivo", "🐐", brown, "animal_chivo"));
                items.add(new KidsCategoryItem("animal_gallo", "Gallo", "🐓", orange, "animal_gallo"));
                items.add(new KidsCategoryItem("animal_gato", "Gato", "🐱", pink, "animal_gato"));
                items.add(new KidsCategoryItem("animal_perro", "Perro", "🐶", teal, "animal_perro"));
                break;
        }
        return items;
    }

    // El enum debe ser 'public' para ser accesible desde 'ActividadModoNinos'
    // Y debe implementar 'Serializable' para poder ser pasado en un Intent.
    public enum KidsCategory implements Serializable {
        ANIMALS, COLORS, NUMBERS, FRUITS, VOWELS;
    }
}
