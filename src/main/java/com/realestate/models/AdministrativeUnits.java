package com.realestate.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "administrative_units")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministrativeUnits {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name="full_name")
    private String fullName;

    @Column(name="full_name_en")
    private String fullNameEn;

    @Column(name="short_name")
    private String shortName;

    @Column(name="short_name_en")
    private String shortNameEn;

    @Column(name="code_name")
    private String codeName;

    @Column(name="code_name_en")
    private String codeNameEn;
}
