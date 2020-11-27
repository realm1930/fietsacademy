package be.vdab.fietsacademy.repositories;

import be.vdab.fietsacademy.domain.Docent;
import be.vdab.fietsacademy.domain.Geslacht;
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
@Sql("/insertDocent.sql")
@Import(JpaDocentRepository.class)
class JpaDocentRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private final EntityManager manager;
    private final JpaDocentRepository repository;
    private Docent docent;
    private static final String DOCENTEN = "docenten";

    public JpaDocentRepositoryTest(EntityManager manager, JpaDocentRepository repository) {
        this.manager = manager;
        this.repository = repository;
    }

    @BeforeEach
    void beforeEach() {
        docent = new Docent("test", "test", BigDecimal.TEN, "test@test.be", Geslacht.MAN);
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
        repository.create(docent);
        assertThat(docent.getId()).isPositive();
        assertThat(super.countRowsInTableWhere(DOCENTEN, "id=" + idVanTestDocent())).isOne();
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

}