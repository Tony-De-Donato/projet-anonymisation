package com.netceler.project_anonymization.dictionary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dictionaries")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DictionaryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "regexp")
    String regexp;

    @Column(name = "replacement")
    String replacement;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "default_pattern")
    Boolean defaultPattern;

    @Column(name = "uniqueness")
    String uniqueness;

}
