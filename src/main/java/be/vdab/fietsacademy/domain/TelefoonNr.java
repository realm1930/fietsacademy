package be.vdab.fietsacademy.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
public class TelefoonNr {
    private String nummer;
    private boolean fax;
    private String opmerking;

    public TelefoonNr(String nummer, boolean fax, String opmerking) {
        this.nummer = nummer;
        this.fax = fax;
        this.opmerking = opmerking;
    }

    public TelefoonNr() {
    }

    @Override
    public int hashCode(){
        return nummer.toUpperCase().hashCode();
    }
    @Override
    public boolean equals(Object object){
        if (object instanceof TelefoonNr){
            var anderTelefoonNr = (TelefoonNr) object;
            return nummer.equalsIgnoreCase(anderTelefoonNr.nummer);
        }
        return false;
    }

    public String getNummer() {
        return nummer;
    }

    public boolean isFax() {
        return fax;
    }

    public String getOpmerking() {
        return opmerking;
    }
}
