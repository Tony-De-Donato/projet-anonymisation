package com.netceler.project_anonymization.dictionary;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @Column(name = "dictfile_name")
    String dictFileName;

    @Column(name = "default_pattern")
    Boolean defaultPattern;

}
