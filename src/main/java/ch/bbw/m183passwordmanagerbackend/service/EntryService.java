package ch.bbw.m183passwordmanagerbackend.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EntryService {
    private EntryRepository entryRepository;

    @Autowired
    public EntryService (EntryRepository entryRepository) {this.entryRepository = entryRepository;}
}
