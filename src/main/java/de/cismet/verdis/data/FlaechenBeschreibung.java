/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.verdis.data;

/**
 *
 * @author thorsten
 */
public class FlaechenBeschreibung {
    private boolean dachflaeche=true;
    private String beschreibung;

    public FlaechenBeschreibung(boolean dachflaeche,String beschreibung) {
        this.beschreibung = beschreibung;
        this.dachflaeche=dachflaeche;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public boolean isDachflaeche() {
        return dachflaeche;
    }

    public void setDachflaeche(boolean dachflaeche) {
        this.dachflaeche = dachflaeche;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FlaechenBeschreibung other = (FlaechenBeschreibung) obj;
        if (this.dachflaeche != other.dachflaeche) {
            return false;
        }
        if (this.beschreibung != other.beschreibung && (this.beschreibung == null || !this.beschreibung.equals(other.beschreibung))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.dachflaeche ? 1 : 0);
        hash = 83 * hash + (this.beschreibung != null ? this.beschreibung.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return beschreibung;
    }
    

}
