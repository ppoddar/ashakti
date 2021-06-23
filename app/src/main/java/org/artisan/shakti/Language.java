package org.artisan.shakti;

/**
 * Enumerate supported language
 */
public enum Language {
    ENGLISH, BANGLA;

    public Language flip() {
        return this == ENGLISH ? BANGLA : ENGLISH;
    }
}
