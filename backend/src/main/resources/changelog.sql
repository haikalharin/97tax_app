
CREATE TABLE `SPRING_SESSION` (
  `PRIMARY_ID` char(36) NOT NULL,
  `SESSION_ID` char(36) NOT NULL,
  `CREATION_TIME` bigint(20) NOT NULL,
  `LAST_ACCESS_TIME` bigint(20) NOT NULL,
  `MAX_INACTIVE_INTERVAL` int(11) NOT NULL,
  `EXPIRY_TIME` bigint(20) NOT NULL,
  `PRINCIPAL_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`PRIMARY_ID`),
  UNIQUE KEY `SPRING_SESSION_IX1` (`SESSION_ID`),
  KEY `SPRING_SESSION_IX2` (`EXPIRY_TIME`),
  KEY `SPRING_SESSION_IX3` (`PRINCIPAL_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

CREATE TABLE `SPRING_SESSION_ATTRIBUTES` (
  `SESSION_PRIMARY_ID` char(36) NOT NULL,
  `ATTRIBUTE_NAME` varchar(200) NOT NULL,
  `ATTRIBUTE_BYTES` blob NOT NULL,
  PRIMARY KEY (`SESSION_PRIMARY_ID`,`ATTRIBUTE_NAME`),
  CONSTRAINT `SPRING_SESSION_ATTRIBUTES_FK` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `SPRING_SESSION` (`PRIMARY_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

CREATE TABLE `commission_type` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

CREATE TABLE `coupon_codenum_gen_tbl` (
  `codenum_gen_name` varchar(255) NOT NULL,
  `codenum_gen_val` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`codenum_gen_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `coupon_codes` (
  `id` bigint(20) NOT NULL,
  `code` varchar(100) DEFAULT NULL,
  `percentage_off` float DEFAULT NULL,
  `partner_name` varchar(255) DEFAULT NULL,
  `contact_name` varchar(255) DEFAULT NULL,
  `commission` int(255) DEFAULT '0',
  `commission_type_id` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Percentage, Flat\nFlat',
  `street_address` varchar(255) DEFAULT NULL,
  `city` varchar(30) DEFAULT NULL,
  `state` varchar(30) DEFAULT NULL,
  `zip` varchar(20) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ein_bot_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ein_order_id` int(11) DEFAULT NULL,
  `bot_id` varchar(32) DEFAULT NULL,
  `url` varchar(256) DEFAULT NULL,
  `is_error` tinyint(3) unsigned DEFAULT '0',
  `created_date` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `ein_orders` (
  `id` int(11) NOT NULL,
  `order_type` enum('SoleProprietor','Partnership','Corporation','LLC') DEFAULT NULL,
  `sub_type` enum('Partnership','Joint Venture','Corporation','S Corporation','Personal Service Corporation','REIT','RIC','Settlement Fund') DEFAULT NULL,
  `reason` varchar(64) DEFAULT NULL,
  `llc_number_members` varchar(32) DEFAULT NULL,
  `first_name` varchar(32) DEFAULT NULL,
  `middle_name` varchar(32) DEFAULT NULL,
  `last_name` varchar(32) DEFAULT NULL,
  `suffix` enum('DDS','MD','PHD','JR','SR','I','II','III','IV','V','VI') DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `ssn` varchar(16) DEFAULT NULL,
  `address` varchar(64) DEFAULT NULL,
  `city` varchar(64) DEFAULT NULL,
  `state` varchar(64) DEFAULT NULL,
  `county` varchar(64) DEFAULT NULL,
  `zip_code` varchar(16) DEFAULT NULL,
  `phone_number` varchar(16) DEFAULT NULL,
  `is_diff_mailing_address` tinyint(3) unsigned DEFAULT NULL,
  `care_of_name` varchar(64) DEFAULT NULL,
  `mailing_address` varchar(64) DEFAULT NULL,
  `mailing_city` varchar(64) DEFAULT NULL,
  `mailing_state` varchar(64) DEFAULT NULL,
  `mailing_zip_code` varchar(16) DEFAULT NULL,
  `mailing_country` varchar(64) DEFAULT NULL,
  `request_reason` enum('Started a new business','Hired employee(s)','Banking purposes','Changed type of organization','Purchased active business') DEFAULT NULL,
  `trade_name` varchar(32) DEFAULT NULL,
  `business_country` varchar(64) DEFAULT NULL,
  `business_state` varchar(32) DEFAULT NULL,
  `business_county` varchar(64) DEFAULT NULL,
  `start_date_year` int(10) unsigned DEFAULT NULL,
  `start_date_month` tinyint(3) unsigned DEFAULT NULL,
  `legal_name` varchar(64) DEFAULT NULL,
  `state_incorporated` varchar(32) DEFAULT NULL,
  `accounting_close_month` tinyint(3) unsigned DEFAULT NULL,
  `reit_type` enum('Mortgage','Equity') DEFAULT NULL,
  `is_large_motor_vehicle` tinyint(3) unsigned DEFAULT NULL,
  `is_gambling` tinyint(3) unsigned DEFAULT NULL,
  `is_excise_tax_form720` tinyint(3) unsigned DEFAULT NULL,
  `is_atf` tinyint(3) unsigned DEFAULT NULL,
  `is_w2_employees` tinyint(3) unsigned DEFAULT NULL,
  `date_first_wages_year` int(11) unsigned DEFAULT NULL,
  `date_first_wages_month` tinyint(3) unsigned DEFAULT NULL,
  `max_ees_next12mos_agri` int(11) DEFAULT NULL,
  `max_ees_next12mos_household` int(11) DEFAULT NULL,
  `max_ees_next12mos_other` int(10) unsigned DEFAULT NULL,
  `card_partner_code` varchar(32) DEFAULT NULL,
  `card_holder_name` varchar(32) DEFAULT NULL,
  `card_number` varchar(64) DEFAULT NULL,
  `card_expire_date` varchar(8) DEFAULT NULL,
  `amount` int(10) unsigned DEFAULT NULL,
  `correlation_id` varchar(64) DEFAULT NULL,
  `authorize_auth_code` varchar(64) DEFAULT NULL,
  `authorize_description` varchar(64) DEFAULT NULL,
  `authorize_transaction_id` varchar(64) DEFAULT NULL,
  `authorize_error_code` varchar(64) DEFAULT NULL,
  `authorize_error_message` varchar(64) DEFAULT NULL,
  `refund_auth_trans_id` varchar(64) DEFAULT NULL,
  `address_status` varchar(64) DEFAULT NULL,
  `trans_type` varchar(16) DEFAULT NULL,
  `transaction_id` varchar(64) DEFAULT NULL,
  `retry_attemp_count` int(10) unsigned DEFAULT NULL,
  `inquiry_retry_number` int(10) unsigned DEFAULT NULL,
  `payload_response_status` varchar(64) DEFAULT NULL,
  `orbital_transaction_number` varchar(64) DEFAULT NULL,
  `orbital_transaction_id` varchar(64) DEFAULT NULL,
  `cbtype` int(11) DEFAULT NULL,
  `signature_verification` varchar(64) DEFAULT NULL,
  `auth_cavv` varchar(64) DEFAULT NULL,
  `auth_eci` varchar(64) DEFAULT NULL,
  `auth_xid` varchar(64) DEFAULT NULL,
  `enrolled` varchar(64) DEFAULT NULL,
  `eci_flag` varchar(64) DEFAULT NULL,
  `customer_ip_address` varchar(32) DEFAULT NULL,
  `shipengine_status_code` varchar(64) DEFAULT NULL,
  `ein` varchar(32) DEFAULT NULL,
  `letter_pdf` varchar(255) DEFAULT NULL,
  `is_pdf_saved` tinyint(255) unsigned DEFAULT '0',
  `status` enum('Processing','Completed','Incomplete','Failed','On Hold','Awaiting Docusign','Charge Back','Cancelled','Deleted') DEFAULT NULL,
  `bot_id` varchar(32) DEFAULT NULL,
  `bot_taken_time` datetime DEFAULT NULL,
  `tracking_number` varchar(64) DEFAULT NULL,
  `tracking_number_entry` datetime DEFAULT NULL,
  `notes` text,
  `notes_entry` datetime DEFAULT NULL,
  `status_last_changed` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

CREATE TABLE `mail_room_batchnum_gen_tbl` (
  `BATCHNUM_GEN_NAME` varchar(255) NOT NULL,
  `BATCHNUM_GEN_VAL` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`BATCHNUM_GEN_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `mail_room_usernum_gen_tbl` (
  `USERNUM_GEN_NAME` varchar(255) NOT NULL,
  `USERNUM_GEN_VAL` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`USERNUM_GEN_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `mail_room_users` (
  `id` bigint(20) NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `update` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `mailroom_batches` (
  `id` bigint(20) NOT NULL,
  `pdf_file` varchar(255) DEFAULT '',
  `shipping_labels` varchar(255) DEFAULT '',
  `created_at` datetime DEFAULT NULL,
  `status` varchar(15) DEFAULT NULL,
  `is_pdf_download` tinyint(1) DEFAULT '0',
  `is_label_download` tinyint(1) DEFAULT '0',
  `start_order_number` bigint(20) DEFAULT NULL,
  `end_order_number` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `oic_calculations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `calc12month_settlement` int(11) DEFAULT NULL,
  `calc24month_settlement` int(11) DEFAULT NULL,
  `calc_due_with_application` int(11) DEFAULT NULL,
  `exception_explanation` varchar(255) DEFAULT NULL,
  `exception_payment_amount1` int(11) DEFAULT NULL,
  `exception_payment_amount2` int(11) DEFAULT NULL,
  `exception_payment_amount3` int(11) DEFAULT NULL,
  `exception_payment_amount4` int(11) DEFAULT NULL,
  `exception_payment_amount5` int(11) DEFAULT NULL,
  `exception_payment_amount_monthly` int(11) DEFAULT NULL,
  `exception_payment_date1` date DEFAULT NULL,
  `exception_payment_date2` date DEFAULT NULL,
  `exception_payment_date3` date DEFAULT NULL,
  `exception_payment_date4` date DEFAULT NULL,
  `exception_payment_date5` date DEFAULT NULL,
  `oic_id` bigint(20) DEFAULT NULL,
  `selected_settlement` varchar(255) DEFAULT NULL,
  `total_assets` int(11) DEFAULT NULL,
  `total_income` int(11) DEFAULT NULL,
  `total_expenses` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `oic_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `additional_income` int(11) DEFAULT NULL,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `alimony_received` int(11) DEFAULT NULL,
  `business_dividends` int(11) DEFAULT NULL,
  `business_insurance` int(11) DEFAULT NULL,
  `business_other_expenses` int(11) DEFAULT NULL,
  `business_other_income` int(11) DEFAULT NULL,
  `business_secured_debts` int(11) DEFAULT NULL,
  `child_care_payments` int(11) DEFAULT NULL,
  `child_support_received` int(11) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `county` varchar(255) DEFAULT NULL,
  `court_ordered_payments` int(11) DEFAULT NULL,
  `current_business_taxes` int(11) DEFAULT NULL,
  `current_monthly_taxes` int(11) DEFAULT NULL,
  `debt_owed` int(11) DEFAULT NULL,
  `delinquent_taxes` int(11) DEFAULT NULL,
  `distributions` int(11) DEFAULT NULL,
  `dob` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `employ_address_line1` varchar(255) DEFAULT NULL,
  `employ_address_line2` varchar(255) DEFAULT NULL,
  `employ_city` varchar(255) DEFAULT NULL,
  `employ_duration_months` int(11) DEFAULT NULL,
  `employ_duration_years` int(11) DEFAULT NULL,
  `employ_state` varchar(255) DEFAULT NULL,
  `employ_zip` varchar(255) DEFAULT NULL,
  `employer_bus_interest` varchar(255) DEFAULT NULL,
  `employer_name` varchar(255) DEFAULT NULL,
  `employer_ownership` bit(1) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `food_clothing_misc` int(11) DEFAULT NULL,
  `gross_receipts` int(11) DEFAULT NULL,
  `gross_rental_income` int(11) DEFAULT NULL,
  `gross_wages_salaries` int(11) DEFAULT NULL,
  `health_insurance_premium` int(11) DEFAULT NULL,
  `healthcare_cost` int(11) DEFAULT NULL,
  `housing_and_utilities` int(11) DEFAULT NULL,
  `interest_and_dividends` int(11) DEFAULT NULL,
  `interest_income` int(11) DEFAULT NULL,
  `inventory_purchased` int(11) DEFAULT NULL,
  `key_uuid` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `life_insurance_premiums` int(11) DEFAULT NULL,
  `mail_city` varchar(255) DEFAULT NULL,
  `mail_county` varchar(255) DEFAULT NULL,
  `mail_line1` varchar(255) DEFAULT NULL,
  `mail_line2` varchar(255) DEFAULT NULL,
  `mail_state` varchar(255) DEFAULT NULL,
  `mail_zip` varchar(255) DEFAULT NULL,
  `married` bit(1) DEFAULT NULL,
  `materials_purchased` int(11) DEFAULT NULL,
  `monthly_income` int(11) DEFAULT NULL,
  `net_rental_income` int(11) DEFAULT NULL,
  `occupation` varchar(255) DEFAULT NULL,
  `order_num` bigint(20) DEFAULT NULL,
  `other_income` int(11) DEFAULT NULL,
  `own_rent` varchar(255) DEFAULT NULL,
  `own_rent_desc` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `public_transport_cost` int(11) DEFAULT NULL,
  `rent` int(11) DEFAULT NULL,
  `secured_debts` int(11) DEFAULT NULL,
  `self_employed` bit(1) DEFAULT NULL,
  `spoouse_employ_city` varchar(255) DEFAULT NULL,
  `spouse_dob` varchar(255) DEFAULT NULL,
  `spouse_employ_address_line1` varchar(255) DEFAULT NULL,
  `spouse_employ_address_line2` varchar(255) DEFAULT NULL,
  `spouse_employ_duration_months` int(11) DEFAULT NULL,
  `spouse_employ_duration_years` int(11) DEFAULT NULL,
  `spouse_employ_state` varchar(255) DEFAULT NULL,
  `spouse_employ_zip` varchar(255) DEFAULT NULL,
  `spouse_employer_bus_interest` varchar(255) DEFAULT NULL,
  `spouse_employer_name` varchar(255) DEFAULT NULL,
  `spouse_employer_ownership` bit(1) DEFAULT NULL,
  `spouse_first_name` varchar(255) DEFAULT NULL,
  `spouse_income` int(11) DEFAULT NULL,
  `spouse_last_name` varchar(255) DEFAULT NULL,
  `spouse_occupation` varchar(255) DEFAULT NULL,
  `spouse_other_income` int(11) DEFAULT NULL,
  `spouse_ssn` varchar(255) DEFAULT NULL,
  `ssn` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `supplies` int(11) DEFAULT NULL,
  `utilities_phones` int(11) DEFAULT NULL,
  `vehicle_cost` int(11) DEFAULT NULL,
  `vehicle_loan_lease_payment` int(11) DEFAULT NULL,
  `vehicle_operating_cost` int(11) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `oic_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK844frlinxh4h7xu1ghvuq8ql1` (`oic_id`),
  CONSTRAINT `FK844frlinxh4h7xu1ghvuq8ql1` FOREIGN KEY (`oic_id`) REFERENCES `oic_calculations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `oic_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_number` varchar(255) DEFAULT NULL,
  `bank_name` varchar(255) DEFAULT NULL,
  `business_asset` bit(1) DEFAULT NULL,
  `oic_id` bigint(20) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `value` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8aqv7fnse32bvnudluau2kbyf` (`oic_id`),
  CONSTRAINT `FK8aqv7fnse32bvnudluau2kbyf` FOREIGN KEY (`oic_id`) REFERENCES `oic_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `oic_dependent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `age` int(11) DEFAULT NULL,
  `contributes_income` bit(1) DEFAULT NULL,
  `dependent_on1040` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `oic_id` bigint(20) DEFAULT NULL,
  `relationship` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7daqevn6nikv6m0rxf0m7dvv3` (`oic_id`),
  CONSTRAINT `FK7daqevn6nikv6m0rxf0m7dvv3` FOREIGN KEY (`oic_id`) REFERENCES `oic_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `oic_investment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_number` varchar(255) DEFAULT NULL,
  `business_asset` bit(1) DEFAULT NULL,
  `loan_balance` int(11) DEFAULT NULL,
  `market_value` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `oic_id` bigint(20) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `type_description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKl79oj7b6u22ewlbj5l0q67du9` (`oic_id`),
  CONSTRAINT `FKl79oj7b6u22ewlbj5l0q67du9` FOREIGN KEY (`oic_id`) REFERENCES `oic_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `oic_life_insurance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cash_value` int(11) DEFAULT NULL,
  `has_life_insurance` bit(1) DEFAULT NULL,
  `loan_balance` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `oic_id` bigint(20) DEFAULT NULL,
  `policy_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKluo5rn3vmxhfrr9a328hvajml` (`oic_id`),
  CONSTRAINT `FKluo5rn3vmxhfrr9a328hvajml` FOREIGN KEY (`oic_id`) REFERENCES `oic_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `oic_real_estate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_city` varchar(255) DEFAULT NULL,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `address_state` varchar(255) DEFAULT NULL,
  `address_zip` varchar(255) DEFAULT NULL,
  `business_asset` bit(1) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `county` varchar(255) DEFAULT NULL,
  `date_final_payment` date DEFAULT NULL,
  `date_purchased` date DEFAULT NULL,
  `loan_balance` int(11) DEFAULT NULL,
  `market_value` int(11) DEFAULT NULL,
  `oic_id` bigint(20) DEFAULT NULL,
  `primary_residence` bit(1) DEFAULT NULL,
  `property_description` varchar(255) DEFAULT NULL,
  `title_description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpu8dlnewas0wrkqpnauh8u87g` (`oic_id`),
  CONSTRAINT `FKpu8dlnewas0wrkqpnauh8u87g` FOREIGN KEY (`oic_id`) REFERENCES `oic_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `oic_vehicle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `business_asset` bit(1) DEFAULT NULL,
  `creditor_name` varchar(255) DEFAULT NULL,
  `date_final_payment` date DEFAULT NULL,
  `date_purchased` date DEFAULT NULL,
  `finance_type` int(11) DEFAULT NULL,
  `loan_balance` int(11) DEFAULT NULL,
  `make_model` varchar(255) DEFAULT NULL,
  `market_value` int(11) DEFAULT NULL,
  `mileage` varchar(255) DEFAULT NULL,
  `monthly_payment` int(11) DEFAULT NULL,
  `oic_id` bigint(20) DEFAULT NULL,
  `year` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpwt0g92oeaa6rchux6ivusrnk` (`oic_id`),
  CONSTRAINT `FKpwt0g92oeaa6rchux6ivusrnk` FOREIGN KEY (`oic_id`) REFERENCES `oic_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `ordernum_gen_tbl` (
  `ORDERNUM_GEN_NAME` varchar(255) NOT NULL,
  `ORDERNUM_GEN_VAL` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ORDERNUM_GEN_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL,
  `acs_url` varchar(255) DEFAULT NULL,
  `amount` varchar(255) DEFAULT NULL,
  `auth_cavv` varchar(255) DEFAULT NULL,
  `auth_eci` varchar(255) DEFAULT NULL,
  `auth_error_desc` varchar(255) DEFAULT NULL,
  `auth_error_no` varchar(255) DEFAULT NULL,
  `auth_xid` varchar(255) DEFAULT NULL,
  `authentication_failed` bit(1) DEFAULT NULL,
  `authorize_auth_code` varchar(255) DEFAULT NULL,
  `authorize_description` varchar(255) DEFAULT NULL,
  `authorize_error_code` varchar(255) DEFAULT NULL,
  `authorize_error_message` varchar(255) DEFAULT NULL,
  `authorize_message_code` varchar(255) DEFAULT NULL,
  `authorize_response_code` varchar(255) DEFAULT NULL,
  `authorize_transaction_id` varchar(255) DEFAULT NULL,
  `billaddr1` varchar(255) DEFAULT NULL,
  `billaddr2` varchar(255) DEFAULT NULL,
  `billcity` varchar(255) DEFAULT NULL,
  `billstate` varchar(255) DEFAULT NULL,
  `billzip` varchar(255) DEFAULT NULL,
  `has_old_address` tinyint(1) DEFAULT '0',
  `old_address1` varchar(100) DEFAULT NULL,
  `old_address2` varchar(100) DEFAULT NULL,
  `old_city` varchar(100) DEFAULT NULL,
  `old_state` varchar(25) DEFAULT NULL,
  `old_zip` varchar(10) DEFAULT NULL,
  `has_prior_names` tinyint(1) DEFAULT '0',
  `prior_names` text,
  `business_name` varchar(255) DEFAULT NULL,
  `payrolldeduction` bit(1) DEFAULT b'0',
  `employername` varchar(255) DEFAULT NULL,
  `employeraddress1` varchar(255) DEFAULT NULL,
  `employeraddress2` varchar(255) DEFAULT NULL,
  `employercity` varchar(255) DEFAULT NULL,
  `employerstate` varchar(255) DEFAULT NULL,
  `employerzip` varchar(255) DEFAULT NULL,
  `bank_name` varchar(100) DEFAULT NULL,
  `bank_address1` varchar(250) DEFAULT NULL,
  `bank_address2` varchar(250) DEFAULT NULL,
  `bank_city` varchar(100) DEFAULT NULL,
  `bank_state` varchar(100) DEFAULT NULL,
  `bank_zip` varchar(100) DEFAULT NULL,
  `payfrequency` int(4) DEFAULT '0',
  `employercontactname` varchar(255) DEFAULT NULL,
  `employercontactphonenumber` varchar(255) DEFAULT NULL,
  `capture_failed` bit(1) DEFAULT NULL,
  `card_bin` varchar(255) DEFAULT NULL,
  `card_brand` varchar(255) DEFAULT NULL,
  `correlation_id` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `eci_flag` varchar(255) DEFAULT NULL,
  `ein` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enrolled` varchar(255) DEFAULT NULL,
  `error_desc` varchar(255) DEFAULT NULL,
  `error_no` varchar(255) DEFAULT NULL,
  `existing_tax_lienq` bit(1) DEFAULT NULL,
  `fname` varchar(255) DEFAULT NULL,
  `got_irs_formq` bit(1) DEFAULT NULL,
  `lname` varchar(255) DEFAULT NULL,
  `lookup_failed` bit(1) DEFAULT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `payload_request` varchar(255) DEFAULT NULL,
  `payload_response` longtext,
  `payload_response_status` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `secondary_phone` varchar(255) DEFAULT NULL,
  `product` varchar(255) DEFAULT NULL,
  `refund_auth_trans_id` varchar(255) DEFAULT NULL,
  `shipaddr1` varchar(255) DEFAULT NULL,
  `shipaddr2` varchar(255) DEFAULT NULL,
  `shipcity` varchar(255) DEFAULT NULL,
  `shipstate` varchar(255) DEFAULT NULL,
  `shipzip` varchar(255) DEFAULT NULL,
  `address_status` varchar(255) DEFAULT NULL,
  `signature_verification` varchar(255) DEFAULT NULL,
  `ssn` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `three_ds_version` varchar(255) DEFAULT NULL,
  `total_debt` decimal(12,2) DEFAULT NULL,
  `tracking_number` varchar(255) DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `notes` text,
  `submission_date` datetime(6) DEFAULT NULL,
  `tracking_number_entry` datetime DEFAULT NULL,
  `notes_entry` datetime DEFAULT NULL,
  `customer_ip_address` varchar(30) DEFAULT NULL,
  `batch` bigint(20) DEFAULT NULL,
  `partner_code` varchar(45) DEFAULT NULL,
  `is_california` bit(1) DEFAULT b'0',
  `is_georgia` bit(1) DEFAULT b'0',
  `is_new_jersey` bit(1) DEFAULT b'0',
  `payment_months` int(11) DEFAULT '72',
  `is_owed_from_business` bit(1) DEFAULT b'0',
  `is_illinois` bit(1) DEFAULT b'0',
  `is_michigan` bit(1) DEFAULT b'0',
  `dba` varchar(255) DEFAULT NULL,
  `illinois_account_id` varchar(255) DEFAULT NULL,
  `good_faith_payment` int(5) DEFAULT '0',
  `mobile` varchar(20) DEFAULT NULL,
  `cbtype` int(4) DEFAULT '0',
  `upsell_clicked` int(4) DEFAULT '0',
  `upsell_shown` int(4) DEFAULT '0',
  `upsell_product` varchar(255) DEFAULT NULL,
  `phys_not_mailing` bit(1) DEFAULT b'0',
  `busaddr1` varchar(255) DEFAULT NULL,
  `busaddr2` varchar(255) DEFAULT NULL,
  `buscity` varchar(255) DEFAULT NULL,
  `busstate` varchar(255) DEFAULT NULL,
  `buszip` varchar(255) DEFAULT NULL,
  `shipengine_status_code` varchar(255) DEFAULT NULL,
  `processing_speed` varchar(255) DEFAULT NULL,
  `treasury_account_number` varchar(100) DEFAULT NULL,
  `business_entity_type` varchar(100) DEFAULT NULL,
  `assessment_number` varchar(100) DEFAULT NULL,
  `orbital_transaction_number` varchar(100) DEFAULT NULL,
  `orbital_transaction_id` varchar(100) DEFAULT NULL,
  `inquiry_retry_number` varchar(100) DEFAULT NULL,
  `retry_attempt_count` varchar(100) DEFAULT NULL,
  `outstanding_amount` varchar(100) DEFAULT NULL,
  `trans_type` varchar(100) DEFAULT NULL,
  `ref_number` varchar(100) DEFAULT NULL,
  `status_last_changed` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `page_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `html_content` longtext,
  `locale` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `partner` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `partner_name` varchar(255) DEFAULT NULL,
  `partner_title` varchar(255) DEFAULT NULL,
  `partner_effective_date` date DEFAULT NULL,
  `partner_address1` varchar(255) DEFAULT NULL,
  `partner_address2` varchar(255) DEFAULT NULL,
  `partner_city` varchar(100) DEFAULT NULL,
  `partner_state` varchar(255) DEFAULT NULL,
  `partner_zip` varchar(255) DEFAULT NULL,
  `partner_phone_number` varchar(100) DEFAULT NULL,
  `partner_ssn` varchar(255) DEFAULT NULL,
  `partner_percent_ownership` varchar(255) DEFAULT NULL,
  `order_record_id` bigint(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `order_record_id` (`order_record_id`),
  CONSTRAINT `partner_ibfk_1` FOREIGN KEY (`order_record_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `partner_codenum_gen_tbl` (
  `codenum_gen_name` varchar(255) NOT NULL,
  `codenum_gen_val` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`codenum_gen_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `partner_codes` (
  `id` bigint(20) NOT NULL,
  `code` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `percentage_off` float DEFAULT NULL,
  `partner_name` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `contact_name` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `commission` int(255) DEFAULT '0',
  `commission_type_id` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Percentage, Flat\nFlat',
  `street_address` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `city` varchar(30) CHARACTER SET latin1 DEFAULT NULL,
  `state` varchar(30) CHARACTER SET latin1 DEFAULT NULL,
  `zip` varchar(20) CHARACTER SET latin1 DEFAULT NULL,
  `notes` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `password_reset_request` (
  `uuid` varchar(36) DEFAULT NULL,
  `username` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `payment_plan_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `married` bit(1) DEFAULT NULL,
  `filing_jointly` varchar(255) DEFAULT NULL,
  `monthly_payment` int(11) DEFAULT NULL,
  `order_num` bigint(20) DEFAULT NULL,
  `payment_day_of_month` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `secondary_phone` varchar(255) DEFAULT NULL,
  `spouse_first_name` varchar(255) DEFAULT NULL,
  `spouse_last_name` varchar(255) DEFAULT NULL,
  `spouse_ssn` varchar(255) DEFAULT NULL,
  `time_to_call` varchar(255) DEFAULT NULL,
  `total_debt` decimal(12,2) DEFAULT NULL,
  `is_california` bit(1) DEFAULT b'0',
  `is_georgia` bit(1) DEFAULT b'0',
  `is_michigan` bit(1) DEFAULT b'0',
  `is_new_jersey` bit(1) DEFAULT b'0',
  `payment_months` int(11) DEFAULT '72',
  `business_name` varchar(255) DEFAULT NULL,
  `ein` varchar(10) DEFAULT NULL,
  `is_owed_from_business` bit(1) DEFAULT b'0',
  `payroll_deduction` bit(1) DEFAULT b'0',
  `order_url` varchar(1024) DEFAULT NULL,
  `is_mobile` bit(1) DEFAULT b'0',
  `is_illinois` bit(1) DEFAULT b'0',
  `dba` varchar(255) DEFAULT NULL,
  `illinois_account_id` varchar(255) DEFAULT NULL,
  `good_faith_payment` int(5) DEFAULT '0',
  `mobile` varchar(20) DEFAULT NULL,
  `processing_speed` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_payment_plan_details_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14400 DEFAULT CHARSET=latin1;

CREATE TABLE `pdf_order_versions` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `pdf_file` longblob,
  `order_id` varchar(200) NOT NULL DEFAULT '',
  `created_date` datetime DEFAULT NULL,
  `version_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `idx_version_id` (`version_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3097 DEFAULT CHARSET=latin1;

CREATE TABLE `penalty_amount` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` int(11) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `penatly_type` varchar(64) DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=166 DEFAULT CHARSET=latin1;

CREATE TABLE `penalty_amount_estimate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` varchar(8) DEFAULT NULL,
  `amount` varchar(16) DEFAULT NULL,
  `filed_late` bit(1) DEFAULT NULL,
  `filed_month` varchar(8) DEFAULT NULL,
  `filed_year` varchar(8) DEFAULT NULL,
  `dd_date` date DEFAULT NULL,
  `ad_date` date DEFAULT NULL,
  `irs_notice_received` date DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `failure_to_file_penalty_amount` int(11) DEFAULT '0',
  `failure_to_pay_penalty_amount` int(11) DEFAULT '0',
  `failure_to_deposit_penalty_amount` int(11) DEFAULT '0',
  `paid_month` varchar(8) DEFAULT NULL,
  `paid_year` varchar(8) DEFAULT NULL,
  `days_late` int(10) unsigned DEFAULT '0',
  `dd_amount` int(10) unsigned DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=337 DEFAULT CHARSET=latin1;

CREATE TABLE `penalty_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fname` varchar(45) DEFAULT NULL,
  `lname` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `billing_address1` varchar(255) DEFAULT NULL,
  `billing_address2` varchar(255) DEFAULT NULL,
  `billing_city` varchar(255) DEFAULT NULL,
  `billing_state` varchar(255) DEFAULT NULL,
  `billing_postal_code` varchar(255) DEFAULT NULL,
  `shipping_address1` varchar(100) DEFAULT NULL,
  `shipping_address2` varchar(100) DEFAULT NULL,
  `shipping_city` varchar(100) DEFAULT NULL,
  `shipping_state` varchar(45) DEFAULT NULL,
  `shipping_postal_code` varchar(10) DEFAULT NULL,
  `ssn` varchar(45) DEFAULT NULL,
  `married` bit(1) DEFAULT NULL,
  `penalty_tax_year` varchar(45) DEFAULT NULL,
  `penalty_amount_waived` varchar(45) DEFAULT NULL,
  `is_penalty_paid` tinyint(4) DEFAULT '0',
  `is_last3irs_wavied` bit(1) DEFAULT NULL,
  `is_decrease_income_tax` bit(1) DEFAULT NULL,
  `partner_code` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `status_last_changed` datetime DEFAULT NULL,
  `correlation_id` varchar(45) DEFAULT NULL,
  `is_last3filed` varchar(45) DEFAULT NULL,
  `largest_penalty` varchar(45) DEFAULT NULL,
  `billing_phone` varchar(45) DEFAULT NULL,
  `penalty_wavie_amt` varchar(45) DEFAULT NULL,
  `shipping_first_name` varchar(45) DEFAULT NULL,
  `shipping_last_name` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `shipping_phone` varchar(45) DEFAULT NULL,
  `authentication_failed` bit(1) DEFAULT NULL,
  `authorize_auth_code` varchar(45) DEFAULT NULL,
  `authorize_description` varchar(45) DEFAULT NULL,
  `authorize_error_code` varchar(45) DEFAULT NULL,
  `authorize_error_message` varchar(255) DEFAULT NULL,
  `authorize_message_code` varchar(255) DEFAULT NULL,
  `authorize_response_code` varchar(255) DEFAULT NULL,
  `authorize_transaction_id` varchar(255) DEFAULT NULL,
  `capture_failed` bit(1) DEFAULT NULL,
  `spouse_firstname` varchar(32) DEFAULT NULL,
  `spouse_lastname` varchar(32) DEFAULT NULL,
  `spouse_ssn` varchar(16) DEFAULT NULL,
  `irs_address1` varchar(128) DEFAULT '',
  `irs_address2` varchar(128) DEFAULT '',
  `irs_city` varchar(64) DEFAULT '',
  `irs_state` varchar(64) DEFAULT '',
  `irs_zipcode` varchar(64) DEFAULT '',
  `notes` text,
  `tracking_number` varchar(64) DEFAULT NULL,
  `tracking_number_entry` datetime DEFAULT NULL,
  `orbital_transaction_number` varchar(100) DEFAULT NULL,
  `orbital_transaction_id` varchar(100) DEFAULT NULL,
  `inquiry_retry_number` varchar(100) DEFAULT NULL,
  `retry_attempt_count` varchar(100) DEFAULT NULL,
  `trans_type` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `ref_number` varchar(100) DEFAULT NULL,
  `order_id` varchar(100) DEFAULT NULL,
  `amount` varchar(100) DEFAULT NULL,
  `cbtype` int(4) DEFAULT NULL,
  `shipengine_status_code` varchar(64) DEFAULT '',
  `processing_speed` varchar(64) DEFAULT NULL,
  `refund_auth_trans_id` varchar(64) DEFAULT NULL,
  `address_status` varchar(128) DEFAULT NULL,
  `transaction_id` varchar(128) DEFAULT NULL,
  `payload_response_status` varchar(255) DEFAULT NULL,
  `signature_verification` varchar(255) DEFAULT NULL,
  `auth_cavv` varchar(128) DEFAULT NULL,
  `auth_eci` varchar(128) DEFAULT NULL,
  `auth_xid` varchar(128) DEFAULT NULL,
  `enrolled` varchar(128) DEFAULT NULL,
  `eci_flag` varchar(128) DEFAULT NULL,
  `customer_ip_address` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=53984 DEFAULT CHARSET=latin1;

CREATE TABLE `process_orders_schedule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `is_automatic` bit(1) DEFAULT b'0',
  `automated_time` datetime DEFAULT NULL,
  `excluded_holidays` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

CREATE TABLE `processed_orders` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `pdf_file` varchar(255) DEFAULT '',
  `created_date` datetime DEFAULT NULL,
  `version_id` varchar(100) DEFAULT NULL,
  `sent_to_mailroom` tinyint(1) DEFAULT '0',
  `shipping_labels` varchar(255) DEFAULT '',
  `start_order_number` bigint(20) DEFAULT NULL,
  `end_order_number` bigint(20) DEFAULT NULL,
  `orders_counter` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2383 DEFAULT CHARSET=latin1;

CREATE TABLE `product_prices` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product` varchar(255) NOT NULL,
  `sub_options` varchar(255) NOT NULL,
  `current_price` float NOT NULL,
  `change_price` float NOT NULL,
  `time_periods_start` datetime DEFAULT NULL,
  `time_periods_end` datetime DEFAULT NULL,
  `additional_timeframes` varchar(255) DEFAULT NULL,
  `is_primary` tinyint(1) NOT NULL DEFAULT '0',
  `last_updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE `sales_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product` varchar(255) NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `on_off` tinyint(1) NOT NULL DEFAULT '0',
  `last_updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

CREATE TABLE `shipping_labels` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL,
  `label_id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `shipment_id` varchar(255) DEFAULT NULL,
  `ship_date` varchar(255) DEFAULT NULL,
  `created_at` varchar(255) DEFAULT NULL,
  `tracking_number` varchar(255) DEFAULT NULL,
  `carrier_id` varchar(255) DEFAULT NULL,
  `service_code` varchar(255) DEFAULT NULL,
  `package_code` varchar(255) DEFAULT NULL,
  `label_format` varchar(255) DEFAULT NULL,
  `label_layout` varchar(255) DEFAULT NULL,
  `tracking_status` varchar(255) DEFAULT NULL,
  `error_message` longtext,
  `process_batch_id` bigint(20) DEFAULT NULL,
  `mailroom_batch_id` bigint(20) DEFAULT NULL,
  `void_status` varchar(255) DEFAULT NULL,
  `label_url` varchar(255) DEFAULT NULL,
  `status_code` varchar(255) DEFAULT NULL,
  `status_description` varchar(255) DEFAULT NULL,
  `carrier_status_code` varchar(255) DEFAULT NULL,
  `carrier_detail_code` varchar(255) DEFAULT NULL,
  `carrier_status_description` varchar(255) DEFAULT NULL,
  `estimated_delivery_date` varchar(255) DEFAULT NULL,
  `actual_delivery_date` varchar(255) DEFAULT NULL,
  `exception_description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79471 DEFAULT CHARSET=latin1;

CREATE TABLE `taxlien` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `existing_tax_lien` bit(1) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `got_irs_form` bit(1) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `order_num` bigint(20) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `tax_lien_business_ein` varchar(255) DEFAULT NULL,
  `tax_lien_business_name` varchar(255) DEFAULT NULL,
  `tax_lien_is_business` bit(1) DEFAULT NULL,
  `tax_lien_remediation_description` varchar(255) DEFAULT NULL,
  `tax_lien_remediation_type` varchar(255) DEFAULT NULL,
  `tax_lien_type` varchar(255) DEFAULT NULL,
  `serial_number` varchar(255) DEFAULT NULL,
  `tax_lien_automatic_debit` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=440 DEFAULT CHARSET=latin1;

CREATE TABLE `transaction_logs` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) unsigned DEFAULT NULL,
  `cardinal_response` text,
  `orbital_payment_response` text,
  `exception_message` text,
  `created_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=7477 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

CREATE TABLE `transaction_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `auth_amount` varchar(255) DEFAULT NULL,
  `auth_code` varchar(255) DEFAULT NULL,
  `authorize_transaction_id` varchar(255) DEFAULT NULL,
  `avs_response` varchar(255) DEFAULT NULL,
  `card_code_response` varchar(255) DEFAULT NULL,
  `card_number_masked` varchar(255) DEFAULT NULL,
  `cavv_response` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `expiration_date_masked` varchar(255) DEFAULT NULL,
  `response_reason_code` varchar(255) DEFAULT NULL,
  `response_reason_description` varchar(255) DEFAULT NULL,
  `settlement_amount` varchar(255) DEFAULT NULL,
  `transaction_status` varchar(255) DEFAULT NULL,
  `transaction_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5124 DEFAULT CHARSET=latin1;

CREATE TABLE `user_record` (
  `username` varchar(30) NOT NULL DEFAULT '',
  `password` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `order_status` int(11) DEFAULT '0',
  `refund_payment` int(11) DEFAULT '0',
  `edit_record` int(11) DEFAULT '0',
  `ssn_privacy` tinyint(1) DEFAULT '1',
  `chargeback_auth` tinyint(1) DEFAULT '1',
  `fulfilment_access` tinyint(1) DEFAULT '1',
  `automated_schedule_access` tinyint(1) DEFAULT '1',
  `user_type` varchar(20) DEFAULT 'admin',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- changeset marme1ad:20230518-002
INSERT INTO `commission_type` VALUES (1,'Percentage'),(2,'Flat');
INSERT INTO `coupon_codenum_gen_tbl` VALUES ('CODENUM_GEN_PK',40000);
INSERT INTO `mail_room_batchnum_gen_tbl` VALUES ('BATCHNUM_GEN_PK',40000);
INSERT INTO `mail_room_usernum_gen_tbl` VALUES ('USERNUM_GEN_PK',40001);
INSERT INTO `mail_room_users` VALUES (40000,'97tax Mail Room Test','mail.room.97tax@mailinator.com',NULL,'password',NULL);
INSERT INTO `ordernum_gen_tbl` VALUES ('ORDERNUM_GEN_PK',40000);
INSERT INTO `partner_codenum_gen_tbl` VALUES ('CODENUM_GEN_PK',40000);

INSERT INTO `product_prices` VALUES (1,'IRS Payment Plan','',119,119,'2022-06-01 02:30:00','2022-08-02 02:30:00','[]',1,NULL),
  (2,'IRS Payment Plan','change_of_address',7,7,'2022-06-14 00:00:00','2022-06-30 13:23:50','[]',0,NULL),
  (3,'IRS Payment Plan','express',15,15,'2022-06-14 12:00:00','2022-06-22 13:23:50','[]',0,NULL),
  (4,'IRS Payment Plan','payroll',9,9,'2022-06-15 13:23:50','2022-06-30 13:23:50','[]',0,NULL),
  (5,'California Payment Plan','',119,97,'2022-05-13 13:23:50','2022-05-13 13:23:50',NULL,1,NULL),
  (6,'California Payment Plan','express',15,16,'2022-05-13 13:23:50','2022-05-12 02:30:00','[]',0,NULL),
  (7,'Georgia Payment Plan','',119,101,'2022-05-13 13:23:50','2022-05-13 13:23:50',NULL,1,NULL),
  (8,'Georgia Payment Plan','express',15,16,'2022-05-13 13:23:50','2022-05-13 13:23:50',NULL,0,NULL),
  (9,'Illinois Payment Plan','',119,97,'2022-05-13 13:23:50','2022-05-13 13:23:50',NULL,1,NULL),
  (10,'Illinois Payment Plan','express',15,17,'2022-05-13 13:23:50','2022-05-13 13:23:50',NULL,0,NULL),
  (11,'New Jersey Payment Plan','',119,17,'2022-05-13 13:23:50','2022-05-13 13:23:50',NULL,1,NULL),
  (12,'New Jersey Payment Plan','express',15,17,'2022-05-13 13:23:50','2022-05-13 13:23:50',NULL,0,NULL),
  (13,'Michigan Payment Plan ','',119,17,'2022-05-13 13:23:50','2022-05-13 13:23:50',NULL,1,NULL),
  (14,'Michigan Payment Plan ','express',15,17,'2022-05-13 13:23:50','2022-05-13 13:23:50',NULL,0,NULL),
  (15,'Penalty Waiver','',97,17,'2022-05-13 13:23:50','2022-05-13 13:23:50',NULL,1,NULL);
INSERT INTO `sales_list` VALUES (1,'IRS Payment Plan','2022-04-27 08:00:00','2022-06-12 14:00:00','913-853-0142',0,NULL),
  (2,'California Payment Plan','2022-05-02 09:00:00','2022-05-02 13:00:00','345-345-4362',1,NULL),
  (3,'New Jersey Payment Plan','2022-06-01 13:00:00','2022-06-30 13:00:00','',0,NULL),
  (4,'Georgia Payment Plan',NULL,NULL,NULL,0,NULL),
  (5,'Tax Lien Removal',NULL,NULL,NULL,0,NULL);

INSERT INTO `user_record` VALUES ('admin','3F91#uk1O528','support@97tax.com',0,0,0,1,1,1,1,'admin');

-- changeset marme1ad:20230616-001
ALTER TABLE `orders`
ADD COLUMN `refund_date` timestamp NULL DEFAULT NULL AFTER `status_last_changed`,
ADD COLUMN `last_four_digits_card` varchar(10) DEFAULT NULL AFTER `refund_date`;

ALTER TABLE `penalty_order`
ADD COLUMN `refund_date` timestamp NULL DEFAULT NULL AFTER `customer_ip_address`,
ADD COLUMN `last_four_digits_card` varchar(10) DEFAULT NULL AFTER `refund_date`,
ADD COLUMN `card_brand` varchar(10) DEFAULT NULL AFTER `last_four_digits_card`;

-- changeset marme1ad:20230818-001
ALTER TABLE `ein_orders`
ADD COLUMN `envelope_id` varchar(36) DEFAULT NULL AFTER `status`;

-- changeset marme1ad:20230907-001
ALTER TABLE `ein_orders`
ADD COLUMN `refund_date` timestamp NULL DEFAULT NULL AFTER `customer_ip_address`,
ADD COLUMN `last_four_digits_card` varchar(10) DEFAULT NULL AFTER `refund_date`,
ADD COLUMN `card_brand` varchar(10) DEFAULT NULL AFTER `last_four_digits_card`,
ADD COLUMN `order_id` varchar(100) DEFAULT NULL AFTER `amount`;

-- changeset marme1ad:20230914-001
CREATE TABLE `alert_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ALERT_CONFIG_NAME` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

INSERT INTO `alert_config` VALUES (1,'notifications.refund.manual','joe@97tax.com',1);

-- changeset marme1ad:20230921-001
ALTER TABLE `user_record`
ADD COLUMN `manual_status_change` tinyint(1) DEFAULT '1' AFTER `automated_schedule_access`;

UPDATE `user_record` SET `manual_status_change` = 0 where `user_type` != 'admin';

-- changeset marme1ad:20230930-001
INSERT INTO `alert_config` VALUES (2,'notifications.refund.manual.secondary','support@97tax.com',0);

-- changeset marme1ad:20231110-001
ALTER TABLE `ein_orders`
DROP COLUMN `shipengine_status_code`,
DROP COLUMN `tracking_number`,
DROP COLUMN `tracking_number_entry`;

-- changeset marme1ad:20231129-001
INSERT INTO `alert_config` VALUES (3,'ein.fulfillment.auto','',1);

-- changeset marme1ad:20231129-002
ALTER TABLE `ein_orders`
ADD COLUMN `manually_run` bit(1) DEFAULT b'0' AFTER `status`;

-- changeset marme1ad:20231130-001
ALTER TABLE `ein_orders`
MODIFY COLUMN `status` enum('Processing','Completed','Incomplete','Failed','On Hold','Awaiting Docusign','Charge Back','Cancelled','Deleted','Bot Error') DEFAULT NULL;

-- changeset marme1ad:20231208-001
ALTER TABLE `ein_bot_log`
ADD COLUMN `screenshot_base64` longtext DEFAULT NULL AFTER `is_error`;

-- changeset marme1ad:20240123-001
ALTER TABLE `ein_orders`
ADD COLUMN `f8821_s3_path` varchar(256) DEFAULT NULL AFTER `envelope_id`;

-- changeset marme1ad:20240203-001
INSERT INTO `sales_list` VALUES (6,'Illinois Payment Plan','2024-02-03 09:00:00','2024-02-03 13:00:00','333-222-1111',0,NULL),
  (7,'Michigan Payment Plan','2024-02-03 09:00:00','2024-02-03 13:00:00','333-222-1111',0,NULL),
  (8,'Penalty Waiver','2024-02-03 09:00:00','2024-02-03 13:00:00','333-222-1111',0,NULL),
  (9,'EIN Application','2024-02-03 09:00:00','2024-02-03 13:00:00','333-222-1111',0,NULL);

-- changeset marme1ad:20240204-001
INSERT INTO `product_prices` VALUES (16,'EIN Application','',49,39,'2024-02-04 09:00:00','2024-02-04 13:00:00',NULL,1,NULL);

-- changeset marme1ad:20240301-001
ALTER TABLE `ein_orders`
ADD COLUMN `business_type` enum('ACCOMMODATIONS', 'CONSTRUCTION', 'FINANCE', 'FOOD_SERVICE', 'HEALTH_CARE', 'INSURANCE',
  'MANUFACTURING', 'REAL_ESTATE', 'RENTAL_AND_LEASING', 'RETAIL', 'SOCIAL_ASSISTANCE', 'TRANSPORTATION', 'WAREHOUSING',
  'WHOLESALE', 'OTHER') DEFAULT NULL AFTER `llc_number_members`,
ADD COLUMN `business_sub_type` enum(
  'CASINO', 'HOTEL', 'MOTEL', 'OTHER', --
  'YES', 'NO', --
  'COMMODITIES_BROKER', 'CREDIT_CARD_ISSUING', 'INVESTMENT_ADVICE', 'INVESTMENT_CLUB', 'INVESTMENT_HOLDING', 'MORTGAGE_AGENT',
  'MORTGAGE_COMPANY', 'PORTFOLIO_MANAGEMENT', 'SALES_FINANCING', 'SECURITIES_BROKER', 'TRUST_ADMIN', 'VENTURE_CAPITAL_COMPANY', -- 'OTHER',
  'BAR', 'BAR_AND_RESTAURANT', 'CATERING_SERVICE', 'COFFEE_SHOP', 'FAST_FOOD_RESTAURANT', 'FULL_SERVICE_RESTAURANT',
  'ICE_CREAM_SHOP', 'MOBILE_FOOD_SERVICE', -- 'OTHER'
  'INSURANCE_CARRIER', 'INSURANCE_AGENT', -- 'OTHER'
  'RENT_PROPERTY', 'CAPITAL_BUILD', 'SELL_PROPERTY', 'MANAGE_PROPERTY', -- 'OTHER'
  'RENT_ESTATE', 'RENT_GOODS', 'MANAGE_ESTATE', --
  'SELLING_GOODS', 'STOREFRONT_SALES', 'DIRECT_SALES', 'AUCTION_HOUSE', -- 'OTHER'
  'NURSING_HOME', 'SHELTER', 'YOUTH_SERVICES', -- 'OTHER'
  'CARGO', 'PASSENGERS', 'PROVIDE_SUPPORT', --
  'CONSULTING', 'MANUFACTURING', 'ORGANIZATION', 'RENTAL', 'REPAIR', 'OTHER_SELL', 'OTHER_SERVICE', -- 'OTHER',
  ''
) DEFAULT NULL AFTER `business_type`,
ADD COLUMN `business_details` varchar(50) DEFAULT NULL AFTER `business_sub_type`;

-- changeset marme1ad:20240306-001
ALTER TABLE `ein_orders`
ADD COLUMN `is_previous_ein` bit(1) DEFAULT b'0' AFTER `state_incorporated`,
ADD COLUMN `previous_ein` varchar(32) DEFAULT NULL AFTER `is_previous_ein`;

-- changeset marme1ad:20240319-001
ALTER TABLE `ein_orders`
ADD COLUMN `is_employment_tax_liability` bit(1) DEFAULT b'0' AFTER `max_ees_next12mos_other`;

-- changeset marme1ad:20240321-001
ALTER TABLE `user_record`
ADD COLUMN `bot_logs_access` tinyint(1) DEFAULT '1' AFTER `manual_status_change`;

UPDATE `user_record` SET `bot_logs_access` = 0 where `user_type` != 'admin';

-- changeset marme1ad:20240322-001
ALTER TABLE `ein_bot_log`
ADD COLUMN `error_code` int(11) DEFAULT NULL AFTER `is_error`;

ALTER TABLE `ein_orders`
ADD COLUMN `error_code` int(11) DEFAULT NULL AFTER `status`;

-- changeset marme1ad:20240324-001
ALTER TABLE `ein_orders`
ADD COLUMN `last_saved_page` varchar(50) DEFAULT NULL AFTER `manually_run`,
ADD COLUMN `is_same_physical_address` tinyint(1) DEFAULT '0' AFTER `state_incorporated`;

-- changeset marme1ad:20240417-001
ALTER TABLE `penalty_order`
DROP COLUMN `penalty_wavie_amt`,
DROP COLUMN `penalty_tax_year`;

ALTER TABLE `penalty_order`
ADD COLUMN `penalty_waived_year` varchar(8) DEFAULT NULL AFTER `penalty_amount_waived`,
ADD COLUMN `penalty_waived_type` varchar(64) DEFAULT NULL AFTER `penalty_waived_year`;

-- changeset marme1ad:20240425-001
ALTER TABLE `ein_orders`
ADD COLUMN `start_after_millis` bigint(20) DEFAULT NULL AFTER `bot_id`,
ADD COLUMN `restarts_count` tinyint(3) DEFAULT '0' AFTER `start_after_millis`;

-- changeset marme1ad:20240427-001
ALTER TABLE `ein_orders`
ADD COLUMN `business_sub_type_2` enum(
  'CONSTRUCT_RESIDENTIAL', 'CONSTRUCT_REMODELING', 'CONSTRUCT_NON_RESIDENTIAL', 'CONSTRUCT_OTHER', --
  'MEDICAL_DOCTOR', 'PSYCHIATRIST', 'OTHER', --
  'CHIROPRACTOR', 'DENTIST', 'HMO_MC', 'HOSPITAL', 'DIALYSIS_CENTER', 'OPTOMETRIST', 'CARE_CENTER',
  'PODIATRIST', 'PSYCHOLOGIST', 'OTHER_MENTAL', -- 'OTHER'
  'RENT_RESIDENTIAL', 'RENT_OTHER', -- 'OTHER'
  'YES', 'NO', --
  'REAL_ESTATE_AGENT', 'RENT_OWN_RESIDENTIAL', 'RENT_OWN_NON_RESIDENTIAL', --
  'AIR', 'RAIL', 'TRUCKING', 'WATER', -- 'OTHER'
  'LIMOUSINE_SERVICE', 'SHUTTLE_BUS', 'TAXI_SERVICE', -- 'OTHER'
  'ATHLETIC', 'CONSERVATION', 'ENVIRONMENTAL', 'FUNDRAISING', 'HOMEOWNERS_ASSOCIATION', 'RELIGIOUS', 'SOCIAL_CIVIC', -- 'OTHER'
  'RENT_LEASE_REAL_ESTATE', 'RENT_LEASE_GOODS', --
  'RETAIL', 'WHOLESALE', --
  ''
) DEFAULT NULL AFTER `business_sub_type`,
ADD COLUMN `business_sub_type_3` enum(
  'CONSTRUCT_RESIDENTIAL', 'CONSTRUCT_REMODELING', 'CONSTRUCT_NON_RESIDENTIAL', 'CONSTRUCT_OTHER', --
  'YES', 'NO', --
  'REAL_ESTATE_AGENT', 'RENT_OWN_RESIDENTIAL', 'RENT_OWN_NON_RESIDENTIAL', --
  'SELLING_GOODS', 'STOREFRONT_SALES', 'DIRECT_SALES', 'AUCTION_HOUSE', 'OTHER', --
  ''
) DEFAULT NULL AFTER `business_sub_type_2`,
ADD COLUMN `business_sub_type_4` enum(
  'YES', 'NO', --
  ''
) DEFAULT NULL AFTER `business_sub_type_3`;

-- changeset marme1ad:20240525-001
ALTER TABLE `ein_orders`
ADD COLUMN `access_denied_count` tinyint(3) DEFAULT '0' AFTER `restarts_count`;

INSERT INTO `alert_config` VALUES (4,'ein.extension.redeploy','',0);

-- changeset marme1ad:20240612-001
ALTER TABLE `ein_orders`
MODIFY COLUMN `order_type` enum('SoleProprietor','Partnership','Corporation','LLC','Estate','Trust') DEFAULT NULL,
MODIFY COLUMN `sub_type` enum('Partnership','Joint Venture', --
    'Corporation','S Corporation','Personal Service Corporation','REIT','RIC','Settlement Fund', --
    'Bankruptcy Estate','Charitable Lead Annuity','Charitable Lead Uni','Charitable Remainder Annuity','Charitable Remainder Uni',
    'Conservatorship','Custodianship','Escrow','FNMA','GNMA','Guardianship','Irrevocable Trust','Pooled Income Fund',
    'Qualified Funeral','Receivership','Revocable Trust','Trust (Others)', -- 'Settlement Fund',
    '') DEFAULT NULL;

ALTER TABLE `ein_orders`
ADD COLUMN `title` enum('Administrator', 'Executor', 'Personal Representative', --
  ''
) DEFAULT NULL AFTER `last_name`,
ADD COLUMN `is_sec645` tinyint(3) unsigned DEFAULT 0 AFTER `title`;

-- changeset marme1ad:20240620-001
ALTER TABLE `ein_orders`
DROP COLUMN `care_of_name`,
DROP COLUMN `trade_name`;

-- changeset marme1ad:20240625-001
ALTER TABLE `ein_orders`
MODIFY COLUMN `business_type` enum('ACCOMMODATIONS', 'CONSTRUCTION', 'FINANCE', 'FOOD_SERVICE', 'HEALTH_CARE', 'INSURANCE',
  'MANUFACTURING', 'REAL_ESTATE', 'RENTAL_AND_LEASING', 'RETAIL', 'SOCIAL_ASSISTANCE', 'TRANSPORTATION', 'WAREHOUSING',
  'WHOLESALE', 'OTHER', --
  '') DEFAULT NULL;

-- changeset marme1ad:20240626-001
ALTER TABLE `ein_orders`
ADD COLUMN `no_middle_name` bit(1) DEFAULT b'0' AFTER `middle_name`;

-- changeset marme1ad:20240721-001
ALTER TABLE `sales_list`
ADD COLUMN `start_daily` datetime DEFAULT NULL AFTER `end_time`,
ADD COLUMN `end_daily` datetime DEFAULT NULL AFTER `start_daily`;

-- changeset marme1ad:20240919-001
ALTER TABLE `ein_orders`
ADD COLUMN `envelope_created_at` bigint(20) DEFAULT NULL AFTER `envelope_id`;

-- changeset marme1ad:20240920-001
ALTER TABLE `ein_orders`
ADD COLUMN `secondary_first_name` varchar(32) DEFAULT NULL AFTER `ssn`,
ADD COLUMN `secondary_middle_name` varchar(32) DEFAULT NULL AFTER `secondary_first_name`,
ADD COLUMN `secondary_no_middle_name` bit(1) DEFAULT b'0' AFTER `secondary_middle_name`,
ADD COLUMN `secondary_last_name` varchar(32) DEFAULT NULL AFTER `secondary_no_middle_name`,
ADD COLUMN `secondary_suffix` enum('DDS','MD','PHD','JR','SR','I','II','III','IV','V','VI') DEFAULT NULL AFTER `secondary_last_name`,
ADD COLUMN `secondary_ssn` varchar(16) DEFAULT NULL AFTER `secondary_suffix`;

-- changeset marme1ad:20240922-001
ALTER TABLE `ein_orders`
MODIFY COLUMN `status` enum('Processing','Completed','Incomplete','Failed','On Hold','Awaiting Docusign','Signed','Charge Back','Cancelled','Deleted','Bot Error') DEFAULT NULL;

-- changeset marme1ad:20240804-001
ALTER TABLE `orders`
ADD COLUMN `f9465_status` varchar(64) DEFAULT NULL AFTER `notes_entry`,
ADD COLUMN `f9465_manifest` text DEFAULT NULL AFTER `f9465_status`,
ADD COLUMN `f9465_submission` text DEFAULT NULL AFTER `f9465_manifest`,
ADD COLUMN `f9465_ack_response` text DEFAULT NULL AFTER `f9465_submission`;

-- changeset marme1ad:20241205-001
ALTER TABLE `orders`
ADD COLUMN `f9465_submission_id` varchar(20) DEFAULT NULL AFTER `f9465_ack_response`,
ADD COLUMN `f9465_submission_date` varchar(64) DEFAULT NULL AFTER `f9465_submission_id`,
ADD COLUMN `f9465_error_codes` varchar(64) DEFAULT NULL AFTER `f9465_submission_date`;

-- changeset marme1ad:20241214-001
ALTER TABLE `orders`
MODIFY COLUMN `f9465_error_codes` text DEFAULT NULL;

-- changeset marme1ad:20241216-001
ALTER TABLE `orders`
ADD COLUMN `honeypot_millis` bigint(20) DEFAULT NULL AFTER `f9465_error_codes`;

-- changeset bcarnes:20241216-002
CREATE TABLE `email_to_user_id_map` (
    `email` VARCHAR(255) NOT NULL,
    `user_id` BIGINT NOT NULL,
    PRIMARY KEY (`email`),
    UNIQUE (`user_id`)
);

-- changeset bcarnes:20241231-001
ALTER TABLE `ein_orders`
DROP COLUMN `card_number`;

-- changeset bcarnes:20250101-001
CREATE TABLE `order_locks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL,
  `locked_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_id` (`order_id`)
) ENGINE=InnoDB;

-- changeset marme1ad:20250113-001
INSERT INTO `alert_config` VALUES (5,'ein.service.maintenance','0',1);

-- changeset bcarnes:20250114-001
ALTER TABLE `ein_orders`
MODIFY COLUMN `status` enum('Processing','Completed','Incomplete','Failed','On Hold','Awaiting Docusign','Awaiting Signature','Signed','Charge Back','Cancelled','Deleted','Bot Error') DEFAULT NULL;
UPDATE `ein_orders` SET `status` = 'Awaiting Signature' WHERE `status` = 'Awaiting Docusign';
ALTER TABLE `ein_orders`
MODIFY COLUMN `status` enum('Processing','Completed','Incomplete','Failed','On Hold','Awaiting Signature','Signed','Charge Back','Cancelled','Deleted','Bot Error') DEFAULT NULL;

-- changeset bcarnes:20250115-001
ALTER TABLE `ein_orders`
ADD COLUMN `apt_suite` varchar(72) DEFAULT NULL AFTER `address`;

-- changeset bcarnes:20250116-001
ALTER TABLE `ein_orders`
ADD COLUMN `mailing_apt_suite` varchar(72) DEFAULT NULL AFTER `mailing_address`;

-- changeset marme1ad:20250118-001
UPDATE `ein_orders` SET `envelope_id` = NULL WHERE `status` = 'Awaiting Signature';
