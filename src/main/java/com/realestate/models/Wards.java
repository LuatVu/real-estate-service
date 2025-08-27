package com.realestate.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "wards")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wards {
    @Id
    @Column(name="code")
    private String code;
    
    @Column(name="name")
    private String name;

    @Column(name="name_en")
    private String nameEn;

    @Column(name="full_name")
    private String fullName;

    @Column(name="full_name_en")
    private String fullNameEn;

    @Column(name="code_name")
    private String codeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_code", insertable = false, updatable = false)
    private Provinces province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrative_unit_id", insertable = false, updatable = false)
    private AdministrativeUnits administrativeUnit;
}
