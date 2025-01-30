package com.repnox.nineseventax.features.excel;

import com.repnox.nineseventax.features.admin.OrderSpecs;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.oic.OicRepository;
import com.repnox.nineseventax.features.oic.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OfferInCompromiseExport {

    private static final Logger LOG = LoggerFactory.getLogger(OfferInCompromiseExport.class);


    @Autowired
    public OrderRecordRepo orderRecordRepo;

    @Autowired
    public OicRepository oicRepository;

    public void exortOicCsv(OutputStream os) {
        exportOicCsv(mapRows(findOrders()), os);
    }

    public void exortOicExcel(OutputStream os) {
        exportOicExcel(mapRows(findOrders()), os);
    }

    public List<OfferInCompromiseRow> mapRows(List<OrderRecord> records) {
        return records.stream().map(r -> {
            OicModel model = oicRepository.getByOrderNum(r.getOrderNum());
            OfferInCompromiseRow row = new OfferInCompromiseRow();
            row.setOicModel(model);
            row.setOrderRecord(r);
            return row;
        }).collect(Collectors.toList());
    }

    public List<OrderRecord> findOrders() {
        return orderRecordRepo.findAll(OrderSpecs.pendingOic());
    }

    public void exportOicCsv(List<OfferInCompromiseRow> rows, OutputStream os) {
        TableExporter exporter = new CsvTableExport();
        exportOic(exporter, rows, os);
    }

    public void exportOicExcel(List<OfferInCompromiseRow> rows, OutputStream os) {
        TableExporter exporter = new ExcelTableExporter("Orders");
        exportOic(exporter, rows, os);
    }

    private void exportOic(TableExporter exporter, List<OfferInCompromiseRow> rows, OutputStream os) {


        exporter.nextCell("Order");
        exporter.nextCell("Date");

        exporter.nextCell("First Name");
        exporter.nextCell("Last Name");
        exporter.nextCell("Date of Birth");
        exporter.nextCell("Social Security Number");
        exporter.nextCell("Unmarried");
        exporter.nextCell("Married");

        exporter.nextCell("Home Physical Address");

        exporter.nextCell("Own your home");
        exporter.nextCell("Rent");
        exporter.nextCell("Other");
        exporter.nextCell("Own Rent Description");

        exporter.nextCell("County of Residence");
        exporter.nextCell("Primary Phone");
        exporter.nextCell("Secondary Phone");
        exporter.nextCell("Fax Number");

        exporter.nextCell("Home Mailing Address");

        exporter.nextCell("Spouse's Last Name");
        exporter.nextCell("Spouse's First Name");
        exporter.nextCell("Spouse Date of Birth");
        exporter.nextCell("Spouse Social Security Number");

        exporter.nextCell("Dependent Name 1");
        exporter.nextCell("Dependent Age 1");
        exporter.nextCell("Dependent Relationship 1");
        exporter.nextCell("Dependent 1040 Claimed 1");
        exporter.nextCell("Dependent Contributes 1");
        exporter.nextCell("Dependent Name 2");
        exporter.nextCell("Dependent Age 2");
        exporter.nextCell("Dependent Relationship 2");
        exporter.nextCell("Dependent 1040 Claimed 2");
        exporter.nextCell("Dependent Contributes 2");
        exporter.nextCell("Dependent Name 3");
        exporter.nextCell("Dependent Age 3");
        exporter.nextCell("Dependent Relationship 3");
        exporter.nextCell("Dependent 1040 Claimed 3");
        exporter.nextCell("Dependent Contributes 3");
        exporter.nextCell("Dependent Name 4");
        exporter.nextCell("Dependent Age 4");
        exporter.nextCell("Dependent Relationship 4");
        exporter.nextCell("Dependent 1040 Claimed 4");
        exporter.nextCell("Dependent Contributes 4");

        exporter.nextCell("Employer Name");
        exporter.nextCell("Employer Ownership Interest");
        exporter.nextCell("Employer Interest Partner");
        exporter.nextCell("Employer Interest Officer");
        exporter.nextCell("Employer Interest Sole Proprietor");
        exporter.nextCell("Occupation");
        exporter.nextCell("Employer Duration Years");
        exporter.nextCell("Employer Duration Months");

        exporter.nextCell("Employer Address");

        exporter.nextCell("Spouse Employer Name");
        exporter.nextCell("Spouse Employer Ownership Interest");
        exporter.nextCell("Spouse Employer Interest Partner");
        exporter.nextCell("Spouse Employer Interest Officer");
        exporter.nextCell("Spouse Employer Interest Sole Proprietary");
        exporter.nextCell("Spouse Occupation");
        exporter.nextCell("Spouse Duration Years");
        exporter.nextCell("Spouse Duration Months");

        exporter.nextCell("Spouse Employer Address");

        exporter.nextCell("Asset 1 Cash");
        exporter.nextCell("Asset 1 Checking");
        exporter.nextCell("Asset 1 Savings");
        exporter.nextCell("Asset 1 Money Market");
        exporter.nextCell("Asset 1 Online");
        exporter.nextCell("Asset 1 Stored Value Card");
        exporter.nextCell("Asset 1 Virtual Currency");
        exporter.nextCell("Asset 1 Bank Name");
        exporter.nextCell("Asset 1 Account Number");
        exporter.nextCell("Asset 1 Value");

        exporter.nextCell("Asset 2 Checking");
        exporter.nextCell("Asset 2 Savings");
        exporter.nextCell("Asset 2 Money Market");
        exporter.nextCell("Asset 2 Online");
        exporter.nextCell("Asset 2 Stored Value Card");
        exporter.nextCell("Asset 2 Bank Name");
        exporter.nextCell("Asset 2 Account Number");
        exporter.nextCell("Asset 2 Value");

        exporter.nextCell("Asset 3 Stocks");
        exporter.nextCell("Asset 3 Bonds");
        exporter.nextCell("Asset 3 Other");
        exporter.nextCell("Asset 3 Other Description");
        exporter.nextCell("Asset 3 Institution");
        exporter.nextCell("Asset 3 Account Number");
        exporter.nextCell("Asset 3 Value");
        exporter.nextCell("Asset 3 Loan Balance");

        exporter.nextCell("Asset 4 Stocks");
        exporter.nextCell("Asset 4 Bonds");
        exporter.nextCell("Asset 4 Other");
        exporter.nextCell("Asset 4 Other Description");
        exporter.nextCell("Asset 4 Institution");
        exporter.nextCell("Asset 4 Account Number");
        exporter.nextCell("Asset 4 Value");
        exporter.nextCell("Asset 4 Loan Balance");

        exporter.nextCell("Asset 5 401K");
        exporter.nextCell("Asset 5 IRA");
        exporter.nextCell("Asset 5 Other");
        exporter.nextCell("Asset 5 Other Description");
        exporter.nextCell("Asset 5 Institution");
        exporter.nextCell("Asset 5 Account Number");
        exporter.nextCell("Asset 5 Value");
        exporter.nextCell("Asset 5 Loan Balance");

        exporter.nextCell("Asset 6 401K");
        exporter.nextCell("Asset 6 IRA");
        exporter.nextCell("Asset 6 Other");
        exporter.nextCell("Asset 6 Other Description");
        exporter.nextCell("Asset 6 Institution");
        exporter.nextCell("Asset 6 Account Number");
        exporter.nextCell("Asset 6 Value");
        exporter.nextCell("Asset 6 Loan Balance");

        exporter.nextCell("Insurance Company");
        exporter.nextCell("Insurance Policy Number");
        exporter.nextCell("Insurance Value");
        exporter.nextCell("Insurance Loan Balance");

        exporter.nextCell("Real Estate 1 Address");
        exporter.nextCell("Real Estate 1 Primary");
        exporter.nextCell("Real Estate 1 Date Purchased");
        exporter.nextCell("Real Estate 1 County and Country");
        exporter.nextCell("Real Estate 1 Date of Final Payment");
        exporter.nextCell("Real Estate 1 Title Held");
        exporter.nextCell("Real Estate 1 Property Description");
        exporter.nextCell("Real Estate 1 Value");
        exporter.nextCell("Real Estate 1 Loan Balance");

        exporter.nextCell("Real Estate 2 Address");
        exporter.nextCell("Real Estate 2 Primary");
        exporter.nextCell("Real Estate 2 Date Purchased");
        exporter.nextCell("Real Estate 2 County and Country");
        exporter.nextCell("Real Estate 2 Date of Final Payment");
        exporter.nextCell("Real Estate 2 Title Held");
        exporter.nextCell("Real Estate 2 Property Description");
        exporter.nextCell("Real Estate 2 Value");
        exporter.nextCell("Real Estate 2 Loan Balance");

        exporter.nextCell("Vehicle 1 Make Model");
        exporter.nextCell("Vehicle 1 Year");
        exporter.nextCell("Vehicle 1 Date Purchased");
        exporter.nextCell("Vehicle 1 Mileage");
        exporter.nextCell("Vehicle 1 Lease");
        exporter.nextCell("Vehicle 1 Loan");
        exporter.nextCell("Vehicle 1 Creditor Name");
        exporter.nextCell("Vehicle 1 Date Final Payment");
        exporter.nextCell("Vehicle 1 Monthly Payment");
        exporter.nextCell("Vehicle 1 Value");
        exporter.nextCell("Vehicle 1 Loan Balance");

        exporter.nextCell("Vehicle 2 Make Model");
        exporter.nextCell("Vehicle 2 Year");
        exporter.nextCell("Vehicle 2 Date Purchased");
        exporter.nextCell("Vehicle 2 Mileage");
        exporter.nextCell("Vehicle 2 Lease");
        exporter.nextCell("Vehicle 2 Loan");
        exporter.nextCell("Vehicle 2 Creditor Name");
        exporter.nextCell("Vehicle 2 Date Final Payment");
        exporter.nextCell("Vehicle 2 Monthly Payment");
        exporter.nextCell("Vehicle 2 Value");
        exporter.nextCell("Vehicle 2 Loan Balance");

        exporter.nextCell("Valuable 1 Description");
        exporter.nextCell("Valuable 1 Value");
        exporter.nextCell("Valuable 1 Loan Balance");
        exporter.nextCell("Valuable 2 Description");
        exporter.nextCell("Valuable 2 Value");
        exporter.nextCell("Valuable 2 Loan Balance");
        exporter.nextCell("Valuable 3 Description");
        exporter.nextCell("Valuable 3 Value");
        exporter.nextCell("Valuable 3 Loan Balance");

        exporter.nextCell("Self Emp Sole Proprietorship");
        exporter.nextCell("Self Emp Business Name");
        exporter.nextCell("Self Emp Business Address");

        exporter.nextCell("Self Emp Business Phone");
        exporter.nextCell("Self Emp EIN");
        exporter.nextCell("Self Emp Business Website");
        exporter.nextCell("Self Emp Business Description");
        exporter.nextCell("Self Emp Employees");
        exporter.nextCell("Self Emp Frequency Tax Deposit");
        exporter.nextCell("Self Emp Gross Monthly Payroll");
        exporter.nextCell("Self Emp Trade Name");

        exporter.nextCell("Business Interest");
        exporter.nextCell("Business Ownership Percent");
        exporter.nextCell("Business Title");
        exporter.nextCell("Business Address");

        exporter.nextCell("Business Name");
        exporter.nextCell("Business Phone");
        exporter.nextCell("Business EIN");

        exporter.nextCell("Business Type Partnership");
        exporter.nextCell("Business Type LLC");
        exporter.nextCell("Business Type Corporation");
        exporter.nextCell("Business Type Other");

        exporter.nextCell("Business Asset 1 Cash");
        exporter.nextCell("Business Asset 1 Checking");
        exporter.nextCell("Business Asset 1 Savings");
        exporter.nextCell("Business Asset 1 Money Market");
        exporter.nextCell("Business Asset 1 Online Account");
        exporter.nextCell("Business Asset 1 Stored Value Card");
        exporter.nextCell("Business Asset 1 Bank Name");
        exporter.nextCell("Business Asset 1 Account Number");

        exporter.nextCell("Business Asset 2 Cash");
        exporter.nextCell("Business Asset 2 Checking");
        exporter.nextCell("Business Asset 2 Savings");
        exporter.nextCell("Business Asset 2 Money Market");
        exporter.nextCell("Business Asset 2 Online Account");
        exporter.nextCell("Business Asset 2 Stored Value Card");
        exporter.nextCell("Business Asset 2 Bank Name");
        exporter.nextCell("Business Asset 2 Account Number");

        exporter.nextCell("Business Valuable 1 Description");
        exporter.nextCell("Business Valuable 1 Value");
        exporter.nextCell("Business Valuable 1 Loan Balance");
        exporter.nextCell("Business Valuable 2 Description");
        exporter.nextCell("Business Valuable 2 Value");
        exporter.nextCell("Business Valuable 2 Loan Balance");

        exporter.nextCell("Business Income Gross Receipts");
        exporter.nextCell("Business Income Rental Income");
        exporter.nextCell("Business Income Interest Income");
        exporter.nextCell("Business Income Dividends");
        exporter.nextCell("Business Income Other Income");
        exporter.nextCell("Business Income Materials Purchased");
        exporter.nextCell("Business Income Inventory Purchased");
        exporter.nextCell("Business Income Gross Wages Salaries");
        exporter.nextCell("Business Income Rent");
        exporter.nextCell("Business Income Supplies");
        exporter.nextCell("Business Income Utilities");
        exporter.nextCell("Business Income Vehicle Costs");
        exporter.nextCell("Business Income Business Insurance");
        exporter.nextCell("Business Income Taxes");
        exporter.nextCell("Business Income Secured Debts");
        exporter.nextCell("Business Income Other Expenses");

        exporter.nextCell("Income Gross Wages");
        exporter.nextCell("Income Social Security");
        exporter.nextCell("Income Pension(s)");
        exporter.nextCell("Income Other Income");

        exporter.nextCell("Spouse Income Gross Wages");
        exporter.nextCell("Spouse Income Social Security");
        exporter.nextCell("Spouse Income Pension(s)");
        exporter.nextCell("Spouse Income Other Income");

        exporter.nextCell("Income Additional");
        exporter.nextCell("Income Interest Dividends");
        exporter.nextCell("Income Distributions");
        exporter.nextCell("Income Rental Income");
        exporter.nextCell("Income Child Support");
        exporter.nextCell("Income Alimony");

        exporter.nextCell("Expense Food Clothing");
        exporter.nextCell("Expense Housing Utilities");
        exporter.nextCell("Expense Vehicle Loan");
        exporter.nextCell("Expense Vehicle Operating Cost");
        exporter.nextCell("Expense Public Transportation");
        exporter.nextCell("Expense Health Insurance");
        exporter.nextCell("Expense Out of Pocket");
        exporter.nextCell("Expense Court Ordered");
        exporter.nextCell("Expense Child Care");
        exporter.nextCell("Expense Life Insurance");
        exporter.nextCell("Expense Monthly Taxes");
        exporter.nextCell("Expense Secured Debts");
        exporter.nextCell("Expense Delinquent Tax");


        for (OfferInCompromiseRow row : rows) {
            exporter.nextRow();

            OicModel oicModel = row.getOicModel();
            OrderRecord record = row.getOrderRecord();

            exporter.nextCell(convertString(oicModel.getOrderNum()));
            exporter.nextCell(defaultString(record.getCreatedDate()));

            exporter.nextCell(oicModel.getFirstName());
            exporter.nextCell(oicModel.getLastName());
            exporter.nextCell(oicModel.getDob());
            exporter.nextCell(oicModel.getSsn());
            exporter.nextCell(invertBool(oicModel.getMarried()));
            exporter.nextCell(convertBool(oicModel.getMarried()));

            exporter.nextCell(combineAddress(
                    oicModel.getAddressLine1(),
                    oicModel.getAddressLine2(),
                    oicModel.getCity(),
                    oicModel.getState(),
                    oicModel.getZip()
            ));

            exporter.nextCell(convertBool("Own".equals(oicModel.getOwnRent())));
            exporter.nextCell(convertBool("Rent".equals(oicModel.getOwnRent())));
            exporter.nextCell(convertBool("Other".equals(oicModel.getOwnRent())));
            exporter.nextCell(oicModel.getOwnRentDesc());

            exporter.nextCell(oicModel.getCounty());
            exporter.nextCell(oicModel.getPhoneNumber());
            exporter.nextCell("");
            exporter.nextCell("");

            if (StringUtils.isNotBlank(oicModel.getMailLine1())) {
                exporter.nextCell(combineAddress(
                        oicModel.getMailLine1(),
                        oicModel.getMailLine2(),
                        oicModel.getMailCity(),
                        oicModel.getMailState(),
                        oicModel.getMailZip()
                ));
            } else {
                exporter.nextCell(combineAddress(
                        oicModel.getAddressLine1(),
                        oicModel.getAddressLine2(),
                        oicModel.getCity(),
                        oicModel.getState(),
                        oicModel.getZip()
                ));
            }

            exporter.nextCell(oicModel.getSpouseLastName());
            exporter.nextCell(oicModel.getSpouseFirstName());
            exporter.nextCell(oicModel.getSpouseDob());
            exporter.nextCell(oicModel.getSpouseSsn());

            for (int i=0; i<4; i++) {
                OicDependent dependent;
                if (oicModel.getDependents().size() > i) {
                    dependent = oicModel.getDependents().get(i);
                } else {
                    dependent = new OicDependent();
                }
                exporter.nextCell(dependent.getName());
                exporter.nextCell(convertString(dependent.getAge()));
                exporter.nextCell(dependent.getRelationship());
                exporter.nextCell(convertBool(dependent.getDependentOn1040()));
                exporter.nextCell(convertBool(dependent.getContributesIncome()));
            }

            exporter.nextCell(oicModel.getEmployerName());
            exporter.nextCell(convertBool(oicModel.getEmployerOwnership()));
            exporter.nextCell(convertBool("Partner".equals(oicModel.getEmployerBusInterest())));
            exporter.nextCell(convertBool("Officer".equals(oicModel.getEmployerBusInterest())));
            exporter.nextCell(convertBool("Sole Proprietor".equals(oicModel.getEmployerBusInterest())));
            exporter.nextCell(oicModel.getOccupation());
            exporter.nextCell(convertString(oicModel.getEmployDurationYears()));
            exporter.nextCell(convertString(oicModel.getEmployDurationMonths()));

            exporter.nextCell(combineAddress(
                    oicModel.getEmployAddressLine1(),
                    oicModel.getEmployAddressLine2(),
                    oicModel.getEmployCity(),
                    oicModel.getEmployState(),
                    oicModel.getEmployZip()
            ));

            exporter.nextCell(oicModel.getSpouseEmployerName());
            exporter.nextCell(convertBool(oicModel.getSpouseEmployerOwnership()));
            exporter.nextCell(convertBool("Partner".equals(oicModel.getSpouseEmployerBusInterest())));
            exporter.nextCell(convertBool("Officer".equals(oicModel.getSpouseEmployerBusInterest())));
            exporter.nextCell(convertBool("Sole Proprietor".equals(oicModel.getSpouseEmployerBusInterest())));
            exporter.nextCell(oicModel.getSpouseOccupation());
            exporter.nextCell(convertString(oicModel.getSpouseEmployDurationYears()));
            exporter.nextCell(convertString(oicModel.getSpouseEmployDurationMonths()));

            exporter.nextCell(combineAddress(
                    oicModel.getSpouseEmployAddressLine1(),
                    oicModel.getSpouseEmployAddressLine2(),
                    oicModel.getSpoouseEmployCity(),
                    oicModel.getSpouseEmployState(),
                    oicModel.getSpouseEmployZip()
            ));

            OicAccount firstAsset = null;
            OicAccount secondAsset = null;
            List<OicAccount> remainder = new ArrayList<>();

            List<OicAccount.AccountType> cashTypes = Arrays.asList(OicAccount.AccountType.CASH,
                    OicAccount.AccountType.VIRTUAL);

            List<OicAccount.AccountType> accountTypes = Arrays.asList(OicAccount.AccountType.CHECKING,
                    OicAccount.AccountType.SAVINGS,
                    OicAccount.AccountType.MONEY_MARKET,
                    OicAccount.AccountType.ONLINE,
                    OicAccount.AccountType.CARD);

            for (OicAccount account : oicModel.getAccounts()) {
                if (account.getBusinessAsset() == Boolean.TRUE) {
                    continue;
                }
                if (firstAsset == null && cashTypes.contains(account.getType())) {
                    firstAsset = account;
                } else if (secondAsset == null && accountTypes.contains(account.getType())) {
                    secondAsset = account;
                } else {
                    remainder.add(account);
                }
            }

            for (OicAccount account : remainder) {
                if (firstAsset == null) {
                    firstAsset = account;
                } else if (secondAsset == null) {
                    secondAsset = account;
                } else {
                    LOG.warn("Skipping account due to lack of space: Order number "+oicModel.getOrderNum());
                }
            }

            firstAsset = firstAsset == null ? new OicAccount() : firstAsset;
            secondAsset = secondAsset == null ? new OicAccount() : secondAsset;


            exporter.nextCell(convertBool(firstAsset.getType() == OicAccount.AccountType.CASH));
            exporter.nextCell(convertBool(firstAsset.getType() == OicAccount.AccountType.CHECKING));
            exporter.nextCell(convertBool(firstAsset.getType() == OicAccount.AccountType.SAVINGS));
            exporter.nextCell(convertBool(firstAsset.getType() == OicAccount.AccountType.MONEY_MARKET));
            exporter.nextCell(convertBool(firstAsset.getType() == OicAccount.AccountType.ONLINE));
            exporter.nextCell(convertBool(firstAsset.getType() == OicAccount.AccountType.CARD));
            exporter.nextCell(convertBool(firstAsset.getType() == OicAccount.AccountType.VIRTUAL));
            exporter.nextCell(firstAsset.getBankName());
            exporter.nextCell(firstAsset.getAccountNumber());
            exporter.nextCell(convertString(firstAsset.getValue()));

            exporter.nextCell(convertBool(secondAsset.getType() == OicAccount.AccountType.CHECKING));
            exporter.nextCell(convertBool(secondAsset.getType() == OicAccount.AccountType.SAVINGS));
            exporter.nextCell(convertBool(secondAsset.getType() == OicAccount.AccountType.MONEY_MARKET));
            exporter.nextCell(convertBool(secondAsset.getType() == OicAccount.AccountType.ONLINE));
            exporter.nextCell(convertBool(secondAsset.getType() == OicAccount.AccountType.CARD));
            exporter.nextCell(secondAsset.getBankName());
            exporter.nextCell(secondAsset.getAccountNumber());
            exporter.nextCell(convertString(secondAsset.getValue()));

            List<OicInvestment> investments = new ArrayList<>();
            List<OicInvestment> retirements = new ArrayList<>();
            List<OicInvestment> overflow = new ArrayList<>();

            List<OicInvestment.InvestmentType> investmentTypes = Arrays.asList(OicInvestment.InvestmentType.STOCKS,
                    OicInvestment.InvestmentType.BONDS, OicInvestment.InvestmentType.OTHER);

            List<OicInvestment.InvestmentType> retirementTypes = Arrays.asList(OicInvestment.InvestmentType._401K,
                    OicInvestment.InvestmentType.IRA, OicInvestment.InvestmentType.RETIREMENT);

            for (OicInvestment investment : oicModel.getInvestments()) {
                if (investment.getBusinessAsset() == Boolean.TRUE) {
                    continue;
                }
                if (investments.size() < 2 && investmentTypes.contains(investment.getType())) {
                    investments.add(investment);
                } else if (retirements.size() < 2 && retirementTypes.contains(investment.getType())) {
                    retirements.add(investment);
                } else {
                    overflow.add(investment);
                }
            }

            for (int i=0; i<2; i++) {
                OicInvestment investment = null;
                if (i < investments.size()) {
                    investment = investments.get(i);
                } else if (overflow.size() > 0) {
                    investment = overflow.remove(0);
                } else {
                    investment = new OicInvestment();
                }

                exporter.nextCell(convertBool(investment.getType() == OicInvestment.InvestmentType.STOCKS));
                exporter.nextCell(convertBool(investment.getType() == OicInvestment.InvestmentType.BONDS));
                exporter.nextCell(convertBool(investment.getType() == OicInvestment.InvestmentType.OTHER));
                exporter.nextCell(investment.getTypeDescription());
                exporter.nextCell(investment.getName());
                exporter.nextCell(investment.getAccountNumber());
                exporter.nextCell(convertString(investment.getMarketValue()));
                exporter.nextCell(convertString(investment.getLoanBalance()));

            }

            for (int i=0; i<2; i++) {
                OicInvestment retirement = null;
                if (i < retirements.size()) {
                    retirement = retirements.get(i);
                } else if (overflow.size() > 0) {
                    retirement = overflow.remove(0);
                } else {
                    retirement = new OicInvestment();
                }

                exporter.nextCell(convertBool(retirement.getType() == OicInvestment.InvestmentType._401K));
                exporter.nextCell(convertBool(retirement.getType() == OicInvestment.InvestmentType.IRA));
                exporter.nextCell(convertBool(retirement.getType() == OicInvestment.InvestmentType.RETIREMENT));
                exporter.nextCell(retirement.getTypeDescription());
                exporter.nextCell(retirement.getName());
                exporter.nextCell(retirement.getAccountNumber());
                exporter.nextCell(convertString(retirement.getMarketValue()));
                exporter.nextCell(convertString(retirement.getLoanBalance()));
            }

            OicLifeInsurance lifeInsurance = null;
            if (oicModel.getLifeInsurances().size() >0) {
                lifeInsurance = oicModel.getLifeInsurances().get(0);
            } else {
                lifeInsurance = new OicLifeInsurance();
            }

            exporter.nextCell(lifeInsurance.getName());
            exporter.nextCell(lifeInsurance.getPolicyNumber());
            exporter.nextCell(convertString(lifeInsurance.getCashValue()));
            exporter.nextCell(convertString(lifeInsurance.getLoanBalance()));

            for (int i=0; i<2; i++) {
                OicRealEstate realEstate;
                if (oicModel.getRealEstates().size() > i) {
                    realEstate = oicModel.getRealEstates().get(0);
                } else {
                    realEstate = new OicRealEstate();
                }

                exporter.nextCell(combineAddress(
                        realEstate.getAddressLine1(),
                        realEstate.getAddressLine2(),
                        realEstate.getAddressCity(),
                        realEstate.getAddressState(),
                        realEstate.getAddressZip()
                ));
                exporter.nextCell(convertBool(realEstate.getPrimaryResidence()));
                exporter.nextCell(formatDate(realEstate.getDatePurchased()));
                exporter.nextCell(realEstate.getCounty() + " " + realEstate.getCountry());
                exporter.nextCell(formatDate(realEstate.getDateFinalPayment()));
                exporter.nextCell(realEstate.getTitleDescription());
                exporter.nextCell(realEstate.getPropertyDescription());
                exporter.nextCell(convertString(realEstate.getMarketValue()));
                exporter.nextCell(convertString(realEstate.getLoanBalance()));

            }

            for (int i=0; i<2; i++) {
                OicVehicle vehicle;
                if (oicModel.getVehicles().size() > i) {
                    vehicle = oicModel.getVehicles().get(i);
                } else {
                    vehicle = new OicVehicle();
                }

                exporter.nextCell(vehicle.getMakeModel());
                exporter.nextCell(vehicle.getYear());
                exporter.nextCell(formatDate(vehicle.getDatePurchased()));
                exporter.nextCell(vehicle.getMileage());
                exporter.nextCell(convertBool(vehicle.getFinanceType() == OicVehicle.VehicleFinanceType.Lease));
                exporter.nextCell(convertBool(vehicle.getFinanceType() == OicVehicle.VehicleFinanceType.Loan));
                exporter.nextCell(vehicle.getCreditorName());
                exporter.nextCell(formatDate(vehicle.getDateFinalPayment()));
                exporter.nextCell(convertString(vehicle.getMonthlyPayment()));
                exporter.nextCell(convertString(vehicle.getMarketValue()));
                exporter.nextCell(convertString(vehicle.getLoanBalance()));

            }

            for (int i=0; i<3; i++) {
                // TODO valuables
                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell("");
            }


            if (oicModel.getSelfEmployed() == Boolean.TRUE) {
                exporter.nextCell(convertBool("Proprietor".equals(oicModel.getEmployerBusInterest())));
                exporter.nextCell(oicModel.getEmployerName());
                exporter.nextCell(combineAddress(
                        oicModel.getEmployAddressLine1(),
                        oicModel.getEmployAddressLine2(),
                        oicModel.getEmployCity(),
                        oicModel.getEmployState(),
                        oicModel.getEmployZip()
                ));
                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell(convertString(oicModel.getGrossWagesSalaries()));
                exporter.nextCell("");

                exporter.nextCell(oicModel.getEmployerBusInterest());
                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell(combineAddress(
                        oicModel.getEmployAddressLine1(),
                        oicModel.getEmployAddressLine2(),
                        oicModel.getEmployCity(),
                        oicModel.getEmployState(),
                        oicModel.getEmployZip()
                ));

                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell("");

                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell("");
            } else {
                exporter.skipCells(22);
            }

            List<OicAccount> businessAccounts = oicModel.getAccounts().stream()
                    .filter((b) -> b.getBusinessAsset() == Boolean.TRUE)
                    .collect(Collectors.toList());

            for (int i=0; i<2; i++) {
                OicAccount account;
                if (businessAccounts.size() > i) {
                    account = businessAccounts.get(i);
                } else {
                    account = new OicAccount();
                }

                exporter.nextCell(convertBool(account.getType() == OicAccount.AccountType.CASH));
                exporter.nextCell(convertBool(account.getType() == OicAccount.AccountType.CHECKING));
                exporter.nextCell(convertBool(account.getType() == OicAccount.AccountType.SAVINGS));
                exporter.nextCell(convertBool(account.getType() == OicAccount.AccountType.MONEY_MARKET));
                exporter.nextCell(convertBool(account.getType() == OicAccount.AccountType.ONLINE));
                exporter.nextCell(convertBool(account.getType() == OicAccount.AccountType.CARD));
                exporter.nextCell(account.getBankName());
                exporter.nextCell(account.getAccountNumber());
            }

            for (int i=0; i<2; i++) {
                // TODO business valuables
                exporter.nextCell("");
                exporter.nextCell("");
                exporter.nextCell("");
            }

            exporter.nextCell(convertString(oicModel.getGrossReceipts()));
            exporter.nextCell(convertString(oicModel.getGrossRentalIncome()));
            exporter.nextCell(convertString(oicModel.getInterestIncome()));
            exporter.nextCell(convertString(oicModel.getBusinessDividends()));
            exporter.nextCell(convertString(oicModel.getBusinessOtherIncome()));
            exporter.nextCell(convertString(oicModel.getMaterialsPurchased()));
            exporter.nextCell(convertString(oicModel.getInventoryPurchased()));
            exporter.nextCell(convertString(oicModel.getGrossWagesSalaries()));
            exporter.nextCell(convertString(oicModel.getRent()));
            exporter.nextCell(convertString(oicModel.getSupplies()));
            exporter.nextCell(convertString(oicModel.getUtilitiesPhones()));
            exporter.nextCell(convertString(oicModel.getVehicleCost()));
            exporter.nextCell(convertString(oicModel.getBusinessInsurance()));
            exporter.nextCell(convertString(oicModel.getCurrentBusinessTaxes()));
            exporter.nextCell(convertString(oicModel.getBusinessSecuredDebts()));
            exporter.nextCell(convertString(oicModel.getBusinessOtherExpenses()));

            exporter.nextCell(convertString(oicModel.getMonthlyIncome()));
            exporter.nextCell("");
            exporter.nextCell("");
            exporter.nextCell(convertString(oicModel.getOtherIncome()));

            exporter.nextCell(convertString(oicModel.getSpouseIncome()));
            exporter.nextCell("");
            exporter.nextCell("");
            exporter.nextCell(convertString(oicModel.getSpouseOtherIncome()));

            exporter.nextCell(convertString(oicModel.getAdditionalIncome()));
            exporter.nextCell(convertString(oicModel.getInterestAndDividends()));
            exporter.nextCell(convertString(oicModel.getDistributions()));
            exporter.nextCell(convertString(oicModel.getNetRentalIncome()));
            exporter.nextCell(convertString(oicModel.getChildSupportReceived()));
            exporter.nextCell(convertString(oicModel.getAlimonyReceived()));

            exporter.nextCell(convertString(oicModel.getFoodClothingMisc()));
            exporter.nextCell(convertString(oicModel.getHousingAndUtilities()));
            exporter.nextCell(convertString(oicModel.getVehicleLoanLeasePayment()));
            exporter.nextCell(convertString(oicModel.getVehicleOperatingCost()));
            exporter.nextCell(convertString(oicModel.getPublicTransportCost()));
            exporter.nextCell(convertString(oicModel.getHealthInsurancePremium()));
            exporter.nextCell(convertString(oicModel.getHealthcareCost()));
            exporter.nextCell(convertString(oicModel.getCourtOrderedPayments()));
            exporter.nextCell(convertString(oicModel.getChildCarePayments()));
            exporter.nextCell(convertString(oicModel.getLifeInsurancePremiums()));
            exporter.nextCell(convertString(oicModel.getCurrentMonthlyTaxes()));
            exporter.nextCell(convertString(oicModel.getSecuredDebts()));
            exporter.nextCell(convertString(oicModel.getDelinquentTaxes()));

        }

        exporter.export(os);

    }

    private String combineAddress(String line1, String line2, String city, String state, String zip) {
        String address = "";
        address += line1;
        if (StringUtils.isNotBlank(line2)) {
            address += "\r\n"+line2;
        }
        address += "\r\n";
        address += city + ", "+state+", "+zip;
        return address;
    }

    private String convertString(Number i) {
        return i != null ? i.toString() : "";
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("M/d/yyyy")) : "";
    }

    private String invertBool(Boolean b) {
        return b != null ? (b ? "" : "Y") : "";
    }

    private String convertBool(Boolean b) {
        return b != null ? (b ? "Y" : "") : "";
    }

    private String defaultString(Date v) {
        return v != null ? new SimpleDateFormat("MM/dd/yyyy").format(v) : "";
    }

}








