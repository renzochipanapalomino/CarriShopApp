package com.example.carrishop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(RobolectricTestRunner.class)
public class ActividadModoNinosCategoriaTest {

    @Test
    public void launchColorsCategory_shouldBindExpectedTitle() {
        ActividadModoNinosCategoria activity = launchCategory("COLORS");

        TextView title = activity.findViewById(R.id.txtKidsCategoryTitle);
        assertEquals(activity.getString(R.string.modo_kids_category_colors), title.getText().toString());
    }

    @Test
    public void numbersCategory_shouldShowTenButtonsWithSingleAudio() {
        KidsAudioButtonAdapter adapter = adapterForCategory("NUMBERS");

        assertNotNull(adapter);
        List<KidsCategoryItem> items = adapter.getItemsSnapshot();
        assertEquals(10, items.size());

        Set<String> audioNames = new HashSet<>();
        for (KidsCategoryItem item : items) {
            audioNames.add(item.getAudioName());
        }

        assertEquals(1, audioNames.size());
        assertTrue(audioNames.contains("numeros_1_10"));
    }

    @Test
    public void fruitsCategory_shouldShowEightButtonsSharingAudio() {
        KidsAudioButtonAdapter adapter = adapterForCategory("FRUITS");

        assertNotNull(adapter);
        List<KidsCategoryItem> items = adapter.getItemsSnapshot();
        assertEquals(8, items.size());

        Set<String> ids = new HashSet<>();
        for (KidsCategoryItem item : items) {
            ids.add(item.getId());
        }

        Set<String> expectedIds = new HashSet<>(Arrays.asList(
                "fruta_manzana",
                "fruta_naranja",
                "fruta_uva",
                "fruta_pera",
                "fruta_platano",
                "fruta_fresa",
                "fruta_sandia",
                "fruta_kiwi"
        ));
        assertEquals(expectedIds, ids);

        Set<String> audioNames = new HashSet<>();
        for (KidsCategoryItem item : items) {
            audioNames.add(item.getAudioName());
        }
        assertEquals(1, audioNames.size());
        assertTrue(audioNames.contains("frutas"));
    }

    @Test
    public void colorsCategory_shouldExposeUniqueAudioPerButton() {
        KidsAudioButtonAdapter adapter = adapterForCategory("COLORS");
        assertNotNull(adapter);

        List<KidsCategoryItem> items = adapter.getItemsSnapshot();
        assertEquals(9, items.size());

        Set<String> audioNames = new HashSet<>();
        for (KidsCategoryItem item : items) {
            audioNames.add(item.getAudioName());
        }

        assertEquals(items.size(), audioNames.size());
    }

    @Test
    public void vowelsCategory_shouldShareSingleAudio() {
        KidsAudioButtonAdapter adapter = adapterForCategory("VOWELS");
        assertNotNull(adapter);

        List<KidsCategoryItem> items = adapter.getItemsSnapshot();
        assertEquals(5, items.size());

        Set<String> audioNames = new HashSet<>();
        for (KidsCategoryItem item : items) {
            audioNames.add(item.getAudioName());
        }

        assertEquals(1, audioNames.size());
        assertTrue(audioNames.contains("vocales"));
    }

    @Test
    public void animalsCategory_shouldShowFiveButtons() {
        KidsAudioButtonAdapter adapter = adapterForCategory("ANIMALS");
        assertNotNull(adapter);

        assertEquals(5, adapter.getItemsSnapshot().size());
    }

    private ActividadModoNinosCategoria launchCategory(String key) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ActividadModoNinosCategoria.class);
        intent.putExtra(ActividadModoNinosCategoria.EXTRA_CATEGORY, key);

        return Robolectric.buildActivity(ActividadModoNinosCategoria.class, intent)
                .setup()
                .get();
    }

    private KidsAudioButtonAdapter adapterForCategory(String key) {
        ActividadModoNinosCategoria activity = launchCategory(key);
        RecyclerView recyclerView = activity.findViewById(R.id.recyclerKidButtons);
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        assertNotNull(adapter);
        return (KidsAudioButtonAdapter) adapter;
    }
}
