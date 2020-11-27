package be.vdab.fietsacademy.services;

import be.vdab.fietsacademy.exceptions.DocentNietGevondenException;
import be.vdab.fietsacademy.repositories.DocentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
class DefaultDocentService implements DocentService {
    private final DocentRepository docentRepository;

    DefaultDocentService(DocentRepository docentRepository) {
        this.docentRepository = docentRepository;
    }

    @Override
    public void opslag(long id, BigDecimal percentage) {
        docentRepository.findById(id)
                .orElseThrow(()-> new DocentNietGevondenException())
                .opslag(percentage);
    }
}
