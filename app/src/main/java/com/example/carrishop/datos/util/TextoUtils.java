package com.example.carrishop.datos.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class TextoUtils {

    private TextoUtils() {}

    // Regex precompiladas
    private static final Pattern MARKS      = Pattern.compile("\\p{M}+");                 // tildes tras NFD

    private static final Pattern DIGITS     = Pattern.compile("\\d+");                    // cualquier número
    private static final Pattern UNITS      = Pattern.compile(
            "\\b(ml|l|lt|litro|litros|cc|cm3|g|gr|gramo|gramos|kg|kilo|kilos|"
                    + "unidad|unidades|und|pack|botella|botellas|bolsa|bolsas|lata|latas)\\b");
    private static final Pattern NON_LETTERS = Pattern.compile("[^a-z ]");               // solo letras y espacio
    private static final Pattern MULTISPACE  = Pattern.compile("\\s+");                   // colapsar espacios

    /**
     * Normaliza el texto para búsqueda:
     * - minúsculas
     * - sin tildes
     * - sin dígitos ni unidades (ml, kg, etc.)
     * - sin símbolos
     * - espacios colapsados
     */
    public static String normalizar(String s) {
        if (s == null) return "";
        String t = s.toLowerCase(Locale.ROOT).trim();

        // Quita tildes (NFD + eliminar marcas diacríticas)
        t = Normalizer.normalize(t, Normalizer.Form.NFD);
        t = MARKS.matcher(t).replaceAll("");

        // Quita números y unidades comunes
        t = DIGITS.matcher(t).replaceAll(" ");
        t = UNITS.matcher(t).replaceAll(" ");

        // Deja solo letras y espacios
        t = NON_LETTERS.matcher(t).replaceAll(" ");

        // Colapsa espacios
        t = MULTISPACE.matcher(t).replaceAll(" ").trim();

        return t;
    }
}
