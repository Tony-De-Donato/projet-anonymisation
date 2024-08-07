package com.netceler.project_anonymization.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryRepository extends JpaRepository<DictionaryEntity, Long> {

    @Query("select dictionary from DictionaryEntity dictionary where dictionary.id in (select max(dictionary.id) from DictionaryEntity dictionary where dictionary.name = ?1 group by dictionary.uniqueness)")
    List<DictionaryEntity> findByName(String name);

    @Query("select dictionary from DictionaryEntity dictionary where dictionary.id in (select max(dictionary.id) from DictionaryEntity dictionary where dictionary.name like %?1% group by dictionary.uniqueness)")
    List<DictionaryEntity> findByNameLike(String name);

    @Query("select dictionary from DictionaryEntity dictionary where dictionary.dictFileName = ?1")
    List<DictionaryEntity> findByDictFileName(String dictFileName);

    @Query("select dictionary from DictionaryEntity dictionary where dictionary.defaultPattern = true")
    List<DictionaryEntity> findDefaultPatterns();

    @Query("select dictionary from DictionaryEntity dictionary where dictionary.uniqueness = ?1 and dictionary.dictFileName = ?2")
    List<DictionaryEntity> findByUniquenessAndFilename(String uniqueness, String dictFileName);

}
