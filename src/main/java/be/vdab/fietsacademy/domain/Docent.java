package be.vdab.fietsacademy.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "docenten")
public class Docent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String voornaam;
    private String familienaam;
    private BigDecimal wedde;
    private String emailAdres;
    @Enumerated(EnumType.STRING)
    private Geslacht geslacht;
    @ElementCollection
    @CollectionTable(name = "docentenbijnamen",
            joinColumns = @JoinColumn(name = "docentid") )
    @Column(name = "bijnaam")
    private Set<String> bijnamen;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "campusid")
    private Campus campus;


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Docent) {
            return emailAdres.equalsIgnoreCase(((Docent) obj).emailAdres);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return emailAdres == null ? 0 : emailAdres.toLowerCase().hashCode();
    }

    public Docent(String voornaam, String familienaam, BigDecimal wedde,
                  String emailAdres, Geslacht geslacht, Campus campus) {
        this.voornaam = voornaam;
        this.familienaam = familienaam;
        this.wedde = wedde;
        this.emailAdres = emailAdres;
        this.geslacht = geslacht;
        setCampus(campus);
        this.bijnamen = new LinkedHashSet<>();

    }

    protected Docent(){}

    public Campus getCampus() {
        return campus;
    }

    public void setCampus(Campus campus) {
        if (!campus.getDocenten().contains(this)){
            campus.add(this);
        }
        this.campus = campus;
    }

    public long getId() {
        return id;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public String getFamilienaam() {
        return familienaam;
    }

    public BigDecimal getWedde() {
        return wedde;
    }

    public String getEmailAdres() {
        return emailAdres;
    }

    public Geslacht getGeslacht() {
        return geslacht;
    }

    public void opslag(BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException();
        }
        var factor = BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100)));
        wedde = wedde.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
    public Set<String> getBijnamen() {
        return Collections.unmodifiableSet(bijnamen);
    }
    public boolean addBijnaam(String bijnaam) {
        if (bijnaam.isBlank()) {
            throw new IllegalArgumentException();
        }
        return bijnamen.add(bijnaam);
    }
    public boolean removeBijnaam(String bijnaam) {
        return bijnamen.remove(bijnaam);
    }
}
