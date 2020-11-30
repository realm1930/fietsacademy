package be.vdab.fietsacademy.domain;

import org.springframework.core.annotation.Order;

import javax.persistence.*;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity @Table(name = "campussen")
public class Campus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String naam;
    @Embedded
    private Adres adres;
    @ElementCollection
    @CollectionTable(name = "campussentelefoonnrs",
            joinColumns = @JoinColumn(name = "campusId"))
    @OrderBy("fax")
    private Set<TelefoonNr> telefoonNrs;
    @OneToMany
    @JoinColumn(name="campusid")
    @OrderBy("voornaam, familienaam")
    private Set<Docent> docenten;
    public Set<Docent> getDocenten() {
        return Collections.unmodifiableSet(docenten);
    }
    public boolean add(Docent docent) {
        if (docent == null) {
            throw new NullPointerException();
        }
        return docenten.add(docent);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Campus)) return false;
        Campus campus = (Campus) o;
        return id == campus.id &&
                naam.toUpperCase().equals(campus.naam.toUpperCase()) &&
                adres.equals(campus.adres) &&
                telefoonNrs.equals(campus.telefoonNrs) &&
                docenten.equals(campus.docenten);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, naam, adres, telefoonNrs, docenten);
    }

    public Campus(String naam, Adres adres) {
        this.naam = naam;
        this.adres = adres;
        this.telefoonNrs = new LinkedHashSet<>();
        this.docenten = new LinkedHashSet<>();
    }

    protected Campus() {
    }

    public long getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public Adres getAdres() {
        return adres;
    }
    public Set<TelefoonNr> getTelefoonNrs() {
        return Collections.unmodifiableSet(telefoonNrs);
    }
}
