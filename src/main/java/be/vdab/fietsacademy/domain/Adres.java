package be.vdab.fietsacademy.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class Adres {
    private String straat;
    private String huisNr;
    private String postcode;
    private String gemeente;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Adres)) return false;
        Adres adres = (Adres) o;
        return straat.equals(adres.straat) &&
                huisNr.equals(adres.huisNr) &&
                postcode.equals(adres.postcode) &&
                gemeente.equals(adres.gemeente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(straat, huisNr, postcode, gemeente);
    }

    public Adres(String straat, String huisNr, String postcode, String gemeente) {
        this.straat = straat;
        this.huisNr = huisNr;
        this.postcode = postcode;
        this.gemeente = gemeente;
    }

    protected Adres() {
    }

    public String getStraat() {
        return straat;
    }

    public String getHuisNr() {
        return huisNr;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getGemeente() {
        return gemeente;
    }
}
