package com.repnox.nineseventax.features.taxlienremoval;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="taxlien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxLienRemovalDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderNum;

    private String firstname;

    private String lastname;

    private String phone;

    private String email;

    private Boolean gotIrsForm;

    private Boolean existingTaxLien;

    private Boolean taxLienIsBusiness;

    private String taxLienType;

    private String taxLienBusinessName;

    private String taxLienBusinessEin;

    private String taxLienRemediationType;

    private String taxLienRemediationDescription;

    private Boolean taxLienAutomaticDebit;

    private String serialNumber;
}
