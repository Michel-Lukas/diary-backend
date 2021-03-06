package webtech.deardiary.web.service;

import org.springframework.stereotype.Service;
import webtech.deardiary.persistence.EntryEntity;
import webtech.deardiary.persistence.EntryRepository;
import webtech.deardiary.web.api.Entry;
import webtech.deardiary.web.api.EntryManipulationRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntryService {

    private final EntryTransformer entryTransformer;

    private final EntryRepository entryRepository;

    public EntryService(EntryRepository entryRepository, EntryTransformer entryTransformer) {
        this.entryRepository = entryRepository;
        this.entryTransformer = entryTransformer;
    }

    public List<Entry> findAll() {
        List<EntryEntity> entries = entryRepository.findAll();
        return entries.stream()
                .map(entryTransformer::transformEntity)
                .collect(Collectors.toList());
    }

    public Entry findById(Long id) {
        var entryEntity = entryRepository.findById(id);
        return entryEntity.map(entryTransformer::transformEntity).orElse(null);
    }

    public Entry create(EntryManipulationRequest request) {

        var entryEntity = new EntryEntity(request.getInput(),
                request.getDate(),
                request.getTime());

        entryEntity = entryRepository.save(entryEntity);
        return entryTransformer.transformEntity(entryEntity);
    }

    public Entry update(Long id, EntryManipulationRequest request) {
        var entryEntityOptional = entryRepository.findById(id);
        if (entryEntityOptional.isEmpty()) {
            return null;
        }
        var entryEntity = entryEntityOptional.get();
        entryEntity.setInput(request.getInput());
        entryEntity.setDate(request.getDate());
        entryEntity.setTime(request.getTime());
        entryEntity = entryRepository.save(entryEntity);

        return entryTransformer.transformEntity(entryEntity);
    }

    public boolean deleteById(Long id){
        if (!entryRepository.existsById(id)) {
            return false;
        }

        entryRepository.deleteById(id);
        return true;
    }
}
