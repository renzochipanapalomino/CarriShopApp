package com.example.carrishop;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ActividadModoNinosCategoriaTest {

    @Test
    public void launchColorsCategory_shouldBindExpectedTitle() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ActividadModoNinosCategoria.class);
        intent.putExtra(ActividadModoNinosCategoria.EXTRA_CATEGORY, "COLORS");

        ActividadModoNinosCategoria activity = Robolectric.buildActivity(ActividadModoNinosCategoria.class, intent)
                .setup()
                .get();

        TextView title = activity.findViewById(R.id.txtKidsCategoryTitle);
        assertEquals(activity.getString(R.string.modo_kids_category_colors), title.getText().toString());
    }
}
