package be.vdab.fietsacademy.repositories;

import be.vdab.fietsacademy.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql({"/insertCampus.sql", "/insertVerantwoordelijkheid.sql", "/insertDocent.sql", "/insertDocentVerantwoordelijkheid.sql"})
@Import(JpaDocentRepository.class)
class JpaDocentRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private final EntityManager manager;
    private final JpaDocentRepository repository;
    private Docent docent;
    private Campus campus;
    private static final String DOCENTEN = "docenten";

    public JpaDocentRepositoryTest(EntityManager manager, JpaDocentRepository repository) {
        this.manager = manager;
        this.repository = repository;
    }

    @BeforeEach
    void beforeEach() {campus = new Campus("test", new Adres("test", "test", "test", "test"));

        docent = new Docent("test", "test", BigDecimal.TEN, "test@test.be", Geslacht.MAN, campus);
        campus.add(docent);
    }

    private long idVanTestMan(){
        return super.jdbcTemplate.queryForObject("select id from docenten where voornaam = 'testM'",Long.class);
    }

    @Test
    void findById() {
        assertThat(repository.findById(idVanTestMan()).get().getVoornaam()).isEqualTo("testM");
    }

    @Test
    void findByOnbestaandeId() {
        assertThat(repository.findById(-1)).isNotPresent();
    }

    private long idVanTestVrouw() {
        return super.jdbcTemplate.queryForObject(
                "select id from docenten where voornaam='testV'", Long.class);
    }
    private long idVanTestDocent() {
        return super.jdbcTemplate.queryForObject(
                "select id from docenten where voornaam='test'", Long.class);
    }
    @Test
    void man() {
        assertThat(repository.findById(idVanTestMan())
                .get().getGeslacht()).isEqualTo(Geslacht.MAN);
    }
    @Test
    void vrouw() {
        assertThat(repository.findById(idVanTestVrouw())
                .get().getGeslacht()).isEqualTo(Geslacht.VROUW);
    }

    @Test
    void create() {
        manager.persist(campus);
        repository.create(docent);
        assertThat(docent.getId()).isPositive();
        assertThat(super.countRowsInTableWhere(DOCENTEN, "id=" + idVanTestDocent())).isOne();
        assertThat(super.jdbcTemplate.queryForObject(
                "select campusid from docenten where id=?", Long.class, docent.getId()))
                .isEqualTo(campus.getId());
        manager.flush();
    }

    @Test
    void delete() {
        var id = idVanTestMan();
        repository.delete(id);
        manager.flush();
        assertThat(super.countRowsInTableWhere(DOCENTEN, "id=" + id)).isZero();
    }

    @Test
    void findAll() {
        assertThat(repository.findAll()).hasSize(super.countRowsInTable(DOCENTEN))
                .extracting(docent->docent.getWedde()).isSorted();
    }
    @Test
    void findByWeddeBetween() {
        var duizend = BigDecimal.valueOf(1_000);
        var tweeduizend = BigDecimal.valueOf(2_000);
        var docenten = repository.findByWeddeBetween(duizend, tweeduizend);
        assertThat(docenten).hasSize(
                super.countRowsInTableWhere(DOCENTEN, "wedde between 1000 and 2000"))
                .allSatisfy(
                        docent -> assertThat(docent.getWedde()).isBetween(duizend, tweeduizend));
    }
    @Test
    void findEmailAdressen() {
        assertThat(repository.findEmailAdressen())
                .hasSize(super.jdbcTemplate.queryForObject(
                        "select count(emailadres) from docenten", Integer.class))
                .allSatisfy(adres -> assertThat(adres).contains("@"));
    }
    @Test
    void findIdsEnEmailAdressen() {
        assertThat(repository.findIdsEnEmailAdressen())
                .hasSize(super.countRowsInTable(DOCENTEN));
    }
    @Test
    void findGrootsteWedde() {
        assertThat(repository.findGrootsteWedde()).isEqualByComparingTo(
                super.jdbcTemplate.queryForObject("select max(wedde) from docenten",
                        BigDecimal.class));
    }
    @Test
    void findAantalDocentenPerWedde() {
        var duizend = BigDecimal.valueOf(1_000);
        assertThat(repository.findAantalDocentenPerWedde())
                .hasSize(super.jdbcTemplate.queryForObject(
                        "select count(distinct wedde) from docenten", Integer.class))
                .filteredOn(aantalPerWedde ->
                        aantalPerWedde.getWedde().compareTo(duizend) == 0)
                .allSatisfy(aantalPerWedde -> assertThat(aantalPerWedde.getAantal())
                        .isEqualTo(super.countRowsInTableWhere(DOCENTEN, "wedde = 1000")));
    }
    @Test
    void algemeneOpslag() {
        assertThat(repository.algemeneOpslag(BigDecimal.TEN))
                .isEqualTo(super.countRowsInTable(DOCENTEN));
        assertThat(super.jdbcTemplate.queryForObject(
                "select wedde from docenten where id=?", BigDecimal.class, idVanTestMan()))
                .isEqualByComparingTo("1100");
    }
    @Test
    void verantwoordelijkhedenLezen() {
        assertThat(repository.findById(idVanTestMan()).get().getVerantwoordelijkheden())
                .containsOnly(new Verantwoordelijkheid("test"));
    }
    @Test
    void verantwoordelijkheidToevoegen() {
        var verantwoordelijkheid = new Verantwoordelijkheid("test2");
        manager.persist(verantwoordelijkheid);
        manager.persist(campus);
        repository.create(docent);
        docent.add(verantwoordelijkheid);
        manager.flush();
        assertThat(super.jdbcTemplate.queryForObject(
                "select verantwoordelijkheidid from docentenverantwoordelijkheden " +
                        "where docentid=?", Long.class, docent.getId()).longValue())
                .isEqualTo(verantwoordelijkheid.getId());
    }

}