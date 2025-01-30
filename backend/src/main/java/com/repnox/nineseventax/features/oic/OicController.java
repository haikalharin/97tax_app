package com.repnox.nineseventax.features.oic;

import com.repnox.nineseventax.features.oic.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@RestController()
@RequestMapping("/api")
public class OicController {

    public static final String OIC_KEY = "oicKey";

    @Autowired
    private OicRepository oicRepository;

    @RequestMapping(path= "/oic", method=RequestMethod.GET)
    public @ResponseBody OicModel get(HttpSession session) {
        OicModel oicModel = oicRepository.getByKey((String)session.getAttribute(OIC_KEY));
        return oicModel;
    }

    @RequestMapping(path = "/oic", method = RequestMethod.POST)
    public void post(HttpSession session, @RequestBody OicModel oicModel) {
        oicModel.setId(null);

        String key = UUID.randomUUID().toString();
        oicModel.setKey(key);

        sanitizeCalculatedValues(oicModel);
        calculateOffer(oicModel);
        oicRepository.save(oicModel);

        session.setAttribute(OIC_KEY, key);
    }

    @RequestMapping(path = "/oic", method = RequestMethod.PUT)
    public void put(HttpSession session, @RequestBody OicModel oicModel) {
        OicModel existing = oicRepository.getByKey((String)session.getAttribute(OIC_KEY));

        oicModel.setId(existing.getId());
        oicModel.setKey(existing.getKey());

        sanitizeCalculatedValues(oicModel);
        calculateOffer(oicModel);
        oicRepository.save(oicModel);
    }

    private void calculateOffer(OicModel model) {
        int item1 = calculateItem1(model);

        int item2And3 = calculateItem2And3(model);

        int item4 = calculateItem4(model);

        int item5 = calculateItem5(model);

        int item6 = calculateItem6(model);

        int boxA = item1+item2And3+item4+item5+item6;

        int boxB = calculateBoxB(model);

        int boxAB = boxA + boxB;

        int boxC = calculateBoxC(model);

        int boxD = calculateBoxD(model, boxC);

        int boxE = calculateBoxE(model);

        int boxF = boxD - boxE;
        if (boxF < 0) boxF = 0;

        int boxG = boxF * 12;
        int boxH = boxF * 24;

        int offerAmount12 = boxAB + boxG;
        int offerAmount24 = boxAB + boxH;

        int dueWithApp = (int) Math.ceil(offerAmount12 * .2);

        OicCalculations oicCalculations = new OicCalculations();
        oicCalculations.setCalc12MonthSettlement(offerAmount12);
        oicCalculations.setCalc24MonthSettlement(offerAmount24);
        oicCalculations.setCalcDueWithApplication(dueWithApp);
        model.setOicCalculations(oicCalculations);
        System.out.println("Offer amount 12: "+offerAmount12);
        System.out.println("Offer amount 24: "+offerAmount24);
    }

    private int calculateBoxB(OicModel model) {
        // Should be included in normal assets, no need for distinction.
        return 0;
    }


    private int calculateBoxE(OicModel model) {
        int boxE = 0;

        boxE += nullToZero(model.getFoodClothingMisc());
        boxE += nullToZero(model.getHousingAndUtilities());
        boxE += nullToZero(model.getVehicleLoanLeasePayment());
        boxE += nullToZero(model.getVehicleOperatingCost());
        boxE += nullToZero(model.getPublicTransportCost());
        boxE += nullToZero(model.getHealthInsurancePremium());
        boxE += nullToZero(model.getHealthcareCost());
        boxE += nullToZero(model.getCourtOrderedPayments());
        boxE += nullToZero(model.getChildCarePayments());
        boxE += nullToZero(model.getLifeInsurancePremiums());
        boxE += nullToZero(model.getCurrentMonthlyTaxes());
        boxE += nullToZero(model.getSecuredDebts());
        boxE += nullToZero(model.getDelinquentTaxes());

        return boxE;
    }

    private int calculateBoxC(OicModel model) {
        int boxC = 0;

        if (model.getSelfEmployed() == Boolean.TRUE) {
            boxC += nullToZero(model.getGrossReceipts());
            boxC += nullToZero(model.getGrossRentalIncome());
            boxC += nullToZero(model.getInterestIncome());
            boxC += nullToZero(model.getBusinessDividends());
            boxC += nullToZero(model.getBusinessOtherIncome());

            boxC -= nullToZero(model.getMaterialsPurchased());
            boxC -= nullToZero(model.getInventoryPurchased());
            boxC -= nullToZero(model.getGrossWagesSalaries());
            boxC -= nullToZero(model.getRent());
            boxC -= nullToZero(model.getSupplies());
            boxC -= nullToZero(model.getUtilitiesPhones());
            boxC -= nullToZero(model.getVehicleCost());
            boxC -= nullToZero(model.getBusinessInsurance());
            boxC -= nullToZero(model.getCurrentBusinessTaxes());
            boxC -= nullToZero(model.getBusinessSecuredDebts());
            boxC -= nullToZero(model.getBusinessOtherExpenses());

            if (boxC < 0) boxC = 0;
        }

        return boxC;
    }

    private int calculateBoxD(OicModel model, int boxC) {
        int boxD = 0;

        boxD += nullToZero(model.getMonthlyIncome());
        boxD += nullToZero(model.getOtherIncome());

        if (model.getMarried() == Boolean.TRUE) {
            boxD += nullToZero(model.getSpouseIncome());
            boxD += nullToZero(model.getSpouseOtherIncome());
        }

        boxD += nullToZero(model.getAdditionalIncome());
        boxD += nullToZero(model.getInterestAndDividends());
        boxD += nullToZero(model.getDistributions());
        boxD += nullToZero(model.getNetRentalIncome());
        boxD += nullToZero(model.getChildSupportReceived());
        boxD += nullToZero(model.getAlimonyReceived());

        boxD += boxC;

        return boxD;
    }

    private int nullToZero(Integer integer) {
        if (integer == null || integer < 0) {
            return 0;
        } else {
            return integer;
        }
    }

    private int calculateItem5(OicModel model) {
        int item5 = 0;

        if (model.getRealEstates() != null) {
            for (OicRealEstate realEstate : model.getRealEstates()) {
                int worth = calculateWorth(realEstate.getMarketValue(), realEstate.getLoanBalance());
                item5 += worth;
            }
        }
        return item5;
    }

    private int calculateItem6(OicModel model) {
        int item4 = 0;

        if (model.getVehicles() != null) {
            boolean isFirst = true;
            for (OicVehicle vehicle : model.getVehicles()) {
                if (vehicle.getFinanceType() != OicVehicle.VehicleFinanceType.Lease) {
                    int worth = calculateWorth(vehicle.getMarketValue(), vehicle.getLoanBalance());
                    if (vehicle.getBusinessAsset() != Boolean.TRUE) {
                        if (isFirst || model.getMarried() == Boolean.TRUE) {
                            worth -= 3450;
                            if (worth < 0) worth = 0;
                        }
                        isFirst = false;
                    }
                    item4 += worth;
                }
            }
        }

        return item4;
    }

    private int calculateItem4(OicModel model) {
        int item4 = 0;

        if (model.getLifeInsurances() != null) {
            for (OicLifeInsurance insurance : model.getLifeInsurances()) {
                int worth = calculateWorth(insurance.getCashValue(), insurance.getLoanBalance());
                item4 += worth;
            }
        }

        return item4;
    }

    private int calculateWorth(Integer marketValue, Integer loanBalance) {
        int worth = 0;
        if (marketValue != null && marketValue > 0) {
            worth = (int) Math.ceil(marketValue * .8);
            if (loanBalance != null && loanBalance > 0) {
                worth = worth - loanBalance;
                if (worth < 0) worth = 0;
            }
        }
        return worth;
    }

    private int calculateItem2And3(OicModel model) {
        int item2And3 = 0;

        if (model.getInvestments() != null) {
            for (OicInvestment investment : model.getInvestments()) {
                int worth = calculateWorth(investment.getMarketValue(), investment.getLoanBalance());
                item2And3 += worth;
            }
        }

        return item2And3;
    }

    private int calculateItem1(OicModel model) {
        int item1 = 0;
        int accountsTotal = 0;
        if (model.getAccounts() != null) {
            for (OicAccount account : model.getAccounts()) {
                if (account.getValue() != null && account.getValue() > 0) {
                    accountsTotal += account.getValue();
                }
            }
        }

        accountsTotal -= 1000;
        if (accountsTotal > 0) {
            item1 = accountsTotal;
        }
        return item1;
    }


    private void sanitizeCalculatedValues(OicModel model) {
        if (model.getOicCalculations() != null) {
            model.getOicCalculations().setId(null);
            model.getOicCalculations().setOicId(null);
            model.getOicCalculations().setCalcDueWithApplication(null);
            model.getOicCalculations().setCalc12MonthSettlement(null);
            model.getOicCalculations().setCalc24MonthSettlement(null);
        }
        if (model.getAccounts() != null) {
            for (OicAccount account : model.getAccounts()) {
                account.setId(null);
                account.setOicId(null);
            }
        }
        if (model.getInvestments() != null) {
            for (OicInvestment investment : model.getInvestments()) {
                investment.setId(null);
                investment.setOicId(null);
            }
        }
        if (model.getLifeInsurances() != null) {
            for (OicLifeInsurance insurance : model.getLifeInsurances()) {
                insurance.setId(null);
                insurance.setOicId(null);
            }
        }
        if (model.getRealEstates() != null) {
            for (OicRealEstate realEstate : model.getRealEstates()) {
                realEstate.setId(null);
                realEstate.setOicId(null);
            }
        }
        if (model.getVehicles() != null) {
            for (OicVehicle vehicle : model.getVehicles()) {
                vehicle.setId(null);
                vehicle.setOicId(null);
            }
        }
    }

}
