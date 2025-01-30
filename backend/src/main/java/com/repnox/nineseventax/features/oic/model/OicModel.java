package com.repnox.nineseventax.features.oic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OicModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="key_uuid")
    private String key;

    private Long orderNum;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private Integer debtOwed;

    private String dob;

    private String ssn;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String zip;

    private String county;

    private String mailLine1;

    private String mailLine2;

    private String mailCity;

    private String mailState;

    private String mailZip;

    private String mailCounty;

    private String ownRent;

    private String ownRentDesc;

    private String spouseFirstName;

    private String spouseLastName;

    private String spouseDob;

    private String spouseSsn;

    // Employment

    private String employerName;

    private Boolean employerOwnership;

    private String employerBusInterest;

    private String occupation;

    private Integer employDurationYears;

    private Integer employDurationMonths;

    private String employAddressLine1;

    private String employAddressLine2;

    private String employCity;

    private String employState;

    private String employZip;

    // Spouse Employment

    private String spouseEmployerName;

    private Boolean spouseEmployerOwnership;

    private String spouseEmployerBusInterest;

    private String spouseOccupation;

    private Integer spouseEmployDurationYears;

    private Integer spouseEmployDurationMonths;

    private String spouseEmployAddressLine1;

    private String spouseEmployAddressLine2;

    private String spoouseEmployCity;

    private String spouseEmployState;

    private String spouseEmployZip;

    // Household Income

    private Integer monthlyIncome;

    private Integer otherIncome;

    private Boolean married;

    private Integer spouseIncome;

    private Integer spouseOtherIncome;

    private Integer additionalIncome;

    private Integer interestAndDividends;

    private Integer distributions;

    private Integer netRentalIncome;

    private Integer childSupportReceived;

    private Integer alimonyReceived;

    // Household Expenses

    private Integer foodClothingMisc;

    private Integer housingAndUtilities;

    private Integer vehicleLoanLeasePayment;

    private Integer vehicleOperatingCost;

    private Integer publicTransportCost;

    private Integer healthInsurancePremium;

    private Integer healthcareCost;

    private Integer courtOrderedPayments;

    private Integer childCarePayments;

    private Integer lifeInsurancePremiums;

    private Integer currentMonthlyTaxes;

    private Integer securedDebts;

    private Integer delinquentTaxes;

    // Business Income

    private Boolean selfEmployed;

    private Integer grossReceipts;

    private Integer grossRentalIncome;

    private Integer interestIncome;

    private Integer businessDividends;

    private Integer businessOtherIncome;

    // Business Expenses

    private Integer materialsPurchased;

    private Integer inventoryPurchased;

    private Integer grossWagesSalaries;

    private Integer rent;

    private Integer supplies;

    private Integer utilitiesPhones;

    private Integer vehicleCost;

    private Integer businessInsurance;

    private Integer currentBusinessTaxes;

    private Integer businessSecuredDebts;

    private Integer businessOtherExpenses;

    // Additional Info

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="oicId")
    private List<OicDependent> dependents;

    // Assets

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="oicId")
    private List<OicAccount> accounts;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="oicId")
    private List<OicInvestment> investments;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="oicId")
    private List<OicRealEstate> realEstates;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="oicId")
    private List<OicVehicle> vehicles;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="oicId")
    private List<OicLifeInsurance> lifeInsurances;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="oicId")
    private OicCalculations oicCalculations;
}

