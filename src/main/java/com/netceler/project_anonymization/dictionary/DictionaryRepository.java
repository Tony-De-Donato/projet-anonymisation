package com.netceler.project_anonymization.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryRepository extends JpaRepository<DictionaryEntity, Long> {

    @Query("select distinct dictionary from DictionaryEntity dictionary where dictionary.regexp in (select distinct dictionary.regexp from DictionaryEntity dictionary where dictionary.name = ?1)")
    List<DictionaryEntity> findByName(String dictFileName);

    @Query("select distinct dictionary from DictionaryEntity dictionary where dictionary.regexp in (select distinct dictionary.regexp from DictionaryEntity dictionary where dictionary.name like %?1%)")
    List<DictionaryEntity> findByNameLike(String dictFileName);

    @Query("select dictionary from DictionaryEntity dictionary where dictionary.dictFileName = ?1")
    List<DictionaryEntity> findByDictFileName(String dictFileName);

    @Query("select dictionary from DictionaryEntity dictionary where dictionary.defaultPattern = true")
    List<DictionaryEntity> findDefaultPatterns();

    @Query("select dictionary from DictionaryEntity dictionary where dictionary.dictFileName = ?1 and dictionary.name = ?2")
    List<DictionaryEntity> findByDictFileNameAndName(String dictFileName, String name);

    @Query("select dictionary from DictionaryEntity dictionary where dictionary.dictFileName = ?1 and dictionary.name = ?2 and dictionary.regexp = ?3")
    List<DictionaryEntity> findByUniqueness(String Uniqueness);

}
