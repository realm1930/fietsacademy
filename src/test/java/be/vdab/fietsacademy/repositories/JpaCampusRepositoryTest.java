package be.vdab.fietsacademy.repositories;

import be.vdab.fietsacademy.domain.Adres;
import be.vdab.fietsacademy.domain.Campus;
import be.vdab.fietsacademy.domain.TelefoonNr;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaCampusRepository.class)
@Sql("/insertCampus.sql")
class JpaCampusRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String CAMPUSSEN = "campussen";
    private JpaCampusRepository repository;

    public JpaCampusRepositoryTest(JpaCampusRepository repository) {
        this.repository = repository;
    }

    private long idVanTestCampus() {
        return super.jdbcTemplate.queryForObject(
                "select id from campussen where naam='test'", Long.class);
    }

    @Test
    void findById() {
        var campus = repository.findById(idVanTestCampus()).get();
        assertThat(campus.getNaam()).isEqualTo("test");
        assertThat(campus.getAdres().getGemeente()).isEqualTo("test");
    }
    @Test
    void findByOnbestaandeId() {
        assertThat(repository.findById(-1)).isNotPresent();
    }
    @Test
    void create() {
        var campus = new Campus("test", new Adres("test", "test", "test", "test"));
        repository.create(campus);
        assertThat(super.countRowsInTableWhere(CAMPUSSEN,"id=" + campus.getId()))
                .isOne();
    }
    @Test
    void telefoonNrsLezen() {
        assertThat(repository.findById(idVanTestCampus()).get().getTelefoonNrs())
                .containsOnly(new TelefoonNr("1", false, "test"));
    }
}
