package be.vdab.fietsacademy.domain;

import javax.persistence.*;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "verantwoordelijkheden")
public class Verantwoordelijkheid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String naam;
    @ManyToMany
    @JoinTable(
            name = "docentenverantwoordelijkheden",
            joinColumns = @JoinColumn(name = "verantwoordelijkheidid"),
            inverseJoinColumns = @JoinColumn(name = "docentid"))
    private Set<Docent> docenten = new LinkedHashSet<>();

    public boolean add(Docent docent) {
        var toegevoegd = docenten.add(docent);
        if ( ! docent.getVerantwoordelijkheden().contains(this)) {
            docent.add(this);
        }
        return toegevoegd;
    }

    public Verantwoordelijkheid(String naam) {

        this.naam = naam;
    }

    public Verantwoordelijkheid() {
    }

    public boolean remove(Docent docent) {
        var verwijderd = docenten.remove(docent);
        if (docent.getVerantwoordelijkheden().contains(this)) {
            docent.remove(this);
        }
        return verwijderd;
    }
    public Set<Docent> getDocenten() {
        return Collections.unmodifiableSet(docenten);
    }

    public long getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }
}
