import { AXIOS } from "../scripts/http-common";

if (!String.prototype.startsWith) {
  String.prototype.startsWith = function (searchString, position) {
    position = position || 0;
    return this.indexOf(searchString, position) === position;
  };
}
if (!String.prototype.endsWith) {
  String.prototype.endsWith = function (searchString, position) {
    var subjectString = this.toString();
    if (
      typeof position !== "number" ||
      !isFinite(position) ||
      Math.floor(position) !== position ||
      position > subjectString.length
    ) {
      position = subjectString.length;
    }
    position -= searchString.length;
    var lastIndex = subjectString.indexOf(searchString, position);
    return lastIndex !== -1 && lastIndex === position;
  };
}

export const APPLICATION_SUMMARY_PAGE = "application-summary";
const SUCCESS_VALIDATION_MSG = "";
const BLANK_BOX_ERROR_MSG = "This box is required";

const BILLING_FIRST_NAME_LENGTH_ERROR_MSG = "Please enter your full first name in this box.";
const S_FIRST_NAME_LENGTH_ERROR_MSG = "Please enter your full spouse first name in this box.";

const BILLING_FIRST_NAME_WRONG_LETTER_ERROR_MSG = "Please only enter your first name in this box. If you need to list a spouse, please enter their name in the boxes at the bottom of this page.";
const S_FIRST_NAME_WRONG_LETTER_ERROR_MSG = "Please only enter your spouse first name in this box. If you need to list a spouse, please enter their name in the boxes at the bottom of this page.";

const BILLING_FIRST_NAME_WRONG_WORD_ERROR_MSG = "Please only enter your first name in this box. If you need to list a business, please enter it further down the page.";
const S_FIRST_NAME_WRONG_WORD_ERROR_MSG = "Please only enter your spouse first name in this box. If you need to list a business, please enter it further down the page.";

const BILLING_LAST_NAME_LENGTH_ERROR_MSG = "Please enter your full last name in this box.";
const S_LAST_NAME_LENGTH_ERROR_MSG = "Please enter your full spouse last name in this box.";

const BILLING_LAST_NAME_WRONG_LETTER_ERROR_MSG = "Please only enter your last name in this box. If you need to list a spouse, please enter their name in the boxes at the bottom of this page.";
const S_LAST_NAME_WRONG_LETTER_ERROR_MSG = "Please only enter your spouse last name in this box. If you need to list a spouse, please enter their name in the boxes at the bottom of this page.";

const BILLING_LAST_NAME_WRONG_WORD_ERROR_MSG = "Please only enter your last name in this box. If you need to list a business, please enter it further down the page.";
const S_LAST_NAME_WRONG_WORD_ERROR_MSG = "Please only enter your spouse last name in this box. If you need to list a business, please enter it further down the page.";

const BILLING_FIRST_NAME_WRONG_WORD_BUSINESS = "Please only enter your first name in this box. If you need to list a business, please enter it further down the page.";
const BILLING_LAST_NAME_WRONG_WORD_BUSINESS = "Please only enter your last name in this box. If you need to list a business, please enter it further down the page.";

const PHONE_NUMBER_LENGTH_ERROR_MSG = "Please only enter your phone number in this box.";
const EMAIL_ADDRESS_FORMAT_ERROR_MSG = "Please enter your email address in the correct format";
const CITY_FORMAT_ERROR_MSG = "Please enter your City.";
const ZIP_CODE_FORMAT_ERROR_MSG = "Please enter your ZipCode.";

const STREET_ADDRESS_INVALID_ERROR_MSG = "Please enter your street address in the correct format.";
const STREET_ADDRESS_PO_BOX_ERROR_MSG = "This box is for a street address, not a PO BOX.";
const STREET_ADDRESS_HASNUMBER_ERROR_MSG = "Please include your street address number here.";
const STREET_ADDRESS_MATCH_APARTMENT_ERROR_MSG = "This box is for a street address, not an apartment.";
const APT_UNIT_INVALID_ERROR_MSG = "Please enter correct apt/unit number.";
const SSN_INVALID_ERROR_MSG = "Your SSN/TIN is invalid.";

export const SiteUtils = {
  validateAll(refs) {
    let isValid = true;
    for (let name in refs) {
      let component = refs[name];
      if (component) {
        if (component.validate) {
          isValid = component.validate() && isValid;
        }
      }
    }
    return isValid;
  },

  calculatePaymentMonths: function (amountOwed, paymentAmount) {
    if (!amountOwed) {
      return 3;
    }

    let totalDebt;
    if (typeof amountOwed === "string") {
      totalDebt = parseInt(amountOwed.replace(/[^0-9\.]/g, ""), 10);
    } else if (typeof amountOwed === "number") {
      totalDebt = amountOwed;
    }

    return Math.ceil(totalDebt / paymentAmount);
  },
  parseTotalDebit(totalDebt) {
    if (typeof totalDebt === "string") {
      return parseFloat(totalDebt.replace(/[^0-9\.]/g, ""));
    } else if (typeof totalDebt === "number") {
      return totalDebt;
    }
  },
  validateInputNumber($event) {
    let inputValue = "";
    let currentValue = $event.target.value;
    if (currentValue.indexOf('.') == 0) {
      inputValue = "";
    } else if (currentValue.indexOf('.') > 0) {
      let splitValue = currentValue.split(".");
      let first = splitValue[0].replace(/\D/g, '');
      first = first.slice(0, 6)
      let second = splitValue[1].replace(/\D/g, '');
      second = second.substring(0, 2)
      inputValue = first.concat(".".concat(second));
    } else {
      inputValue = currentValue.replace(/\D/g, '');;
      inputValue = inputValue.slice(0, 6)
    }
    return inputValue;
  },
  isMobile: function () {
    return window.matchMedia("only screen and (max-width: 760px)").matches;
  },

  updateAddress(orderInfoTemp, orderInfo, addressType, shippingAddress) {
    orderInfoTemp = Object.assign({}, orderInfo);
    orderInfoTemp.shippingAddress1 = addressType
      ? "PO BOX " + orderInfo.shippingAddress1
      : orderInfo.shippingAddress1;
    orderInfoTemp.billingAddress1 = !shippingAddress
      ? orderInfoTemp.billingAddress1
      : orderInfoTemp.shippingAddress1;
    delete orderInfoTemp.oldAddressType;
    orderInfoTemp.hasOldAddress = orderInfoTemp.hasOldAddress === 'yes';
    return orderInfoTemp;
  },

  saveOicPage(existing, formBindings, callback) {
    let serviceRequest = SiteUtils.buildServiceRequest(existing, formBindings);
    AXIOS.put("/oic", serviceRequest)
      .then(response => {
        callback(response);
      })
      .catch(e => {
        alert(
          "Sorry, we are doing maintenance on the website right now. Please check again later."
        );
        console.log(e);
      });
  },

  prefillList(existingList, bindingList, refList) {
    for (let i = bindingList.length; i < existingList.length; i++) {
      let newItem = { _key: Math.random() };
      bindingList.push(newItem);
    }
    setTimeout(function () {
      if (existingList.length > 0) {
        for (let i = 0; i < existingList.length; i++) {
          refList[i].prefill(existingList[i]);
        }
      }
    }, 0);
  },

  prefillForm(self, existing, formBindings) {
    for (let k in formBindings) {
      let name = k;
      let v = formBindings[k];
      if (v) {
        if (v.name) {
          name = v.name;
        }
        if (!Array.isArray(v)) {
          self.$set(v, "value", existing[name]);
        }
      }
    }
  },

  buildServiceRequest(existing, formBindings) {
    for (let k in formBindings) {
      let name = k;
      let v = formBindings[k];
      if (v) {
        if (v.name) {
          name = v.name;
        }
        if (Array.isArray(v)) {
          let newArray = [];
          for (let i = 0; i < v.length; i++) {
            newArray.push(SiteUtils.buildServiceRequest({}, v[i]));
          }
          existing[name] = newArray;
        } else if (typeof v.value !== "undefined") {
          existing[name] = v.value;
        }
      }
    }
    return existing;
  },

  formErrors: function (fieldNames, dataObject, errorsObject) {
    let hasErrors = false;

    fieldNames.forEach(p => {
      if (p != "addressType") {
        let value = dataObject[p];
        console.log("Form Errors: " + p + " : " + value + ", ");
        if (value === null || typeof value === "undefined") {
          errorsObject[p] = true;
          hasErrors = true;
        } else if (typeof value === "boolean") {
          errorsObject[p] = false;
        } else if (typeof value === "string") {
          if (!value || value.trim().length === 0) {
            errorsObject[p] = true;
            hasErrors = true;
          } else {
            errorsObject[p] = false;
          }
        }
      }
    });

    return hasErrors;
  },

  clearErrors: function (fieldNames, errorsObject) {
    fieldNames.forEach(p => {
      errorsObject[p] = false;
    });
  },

  /*********************************************************************** */
  calculatePaymentPlan: function (
    amountOwed,
    months,
    { isCalifornia = false, isNewJersey = false, isGeorgia = false, isIllinois = false } = {}
  ) {
    if (!amountOwed) {
      return {
        valid: false,
        qualifies: false,
        paymentAmount: "N/A"
      };
    }

    let totalDebt;
    if (typeof amountOwed === "string") {
      totalDebt = Number(amountOwed.replace(/[^0-9\.]/g, ""));
    } else if (typeof amountOwed === "number") {
      totalDebt = amountOwed;
    }

    const paymentMonths = months || 72;
    let paymentAmount = Number((totalDebt / parseInt(paymentMonths, 10)).toFixed(2));
    // Math.ceil(totalDebt / parseInt(paymentMonths, 10));
    let qualifies = true;
    let valid = true;

    if (paymentAmount < 25) {
      paymentAmount = 25;
    }

    if (isNaN(paymentAmount)) {
      valid = false;
      paymentAmount = "N/A";
    }
    if (
      (!isCalifornia && !isNewJersey && !isGeorgia && !isIllinois && totalDebt < 500) ||
      (isCalifornia && (totalDebt < 500 || totalDebt > 25000)) ||
      (isNewJersey && (totalDebt < 500 || totalDebt > 75000)) ||
      (isGeorgia && (totalDebt < 500 || totalDebt > 75000)) ||
      (isIllinois && (totalDebt < 500 || totalDebt > 75000)) ||
      (!isCalifornia && !isNewJersey && !isGeorgia && !isIllinois && totalDebt > 150000)) {
      qualifies = false;
      paymentAmount = "N/A";
    }

    return {
      valid,
      totalDebt,
      qualifies,
      paymentAmount
    };
  },
  calculatePaymentMonthsWithAmount(amount, maxMonth) {
    let paymentMonths = maxMonth;
    let tempMonthPayment = amount / parseInt(maxMonth, 10);
    if (tempMonthPayment < 25 && tempMonthPayment !== 0) {
      paymentMonths = Math.floor(amount / 25);
    }
    return paymentMonths;
  },
  validateSpouseFirstName: function (str) {

    const lowerStr = str ? str.toLowerCase() : "";

    if (lowerStr.length === 0) {
      return BLANK_BOX_ERROR_MSG;
    } else if (
      lowerStr.includes("&") ||
      lowerStr.includes(" & ") ||
      lowerStr.startsWith("& ") ||
      lowerStr.endsWith(" &") ||
      lowerStr === "&"
    ) {
      return S_FIRST_NAME_WRONG_LETTER_ERROR_MSG;
    } else if (
      lowerStr.includes("llc") ||
      lowerStr === "llc" ||
      lowerStr.includes("inc") ||
      lowerStr === "inc"
    ) {
      return S_FIRST_NAME_WRONG_WORD_ERROR_MSG;
    } else if (lowerStr.length < 3) {
      return S_FIRST_NAME_LENGTH_ERROR_MSG;

    } else {
      return SUCCESS_VALIDATION_MSG;
    }
  },
  validateSpouseLastName: function (str) {

    const lowerStr = str ? str.toLowerCase() : "";

    if (lowerStr.length === 0) {
      return BLANK_BOX_ERROR_MSG;
    } else if (
      lowerStr.includes("&") ||
      lowerStr.includes(" & ") ||
      lowerStr.startsWith("& ") ||
      lowerStr.endsWith(" &") ||
      lowerStr === "&"
    ) {

      return S_LAST_NAME_WRONG_LETTER_ERROR_MSG;
    } else if (
      lowerStr.includes("llc") ||
      lowerStr === "llc" ||
      lowerStr.includes("inc") ||
      lowerStr === "inc"
    ) {
      return S_LAST_NAME_WRONG_WORD_ERROR_MSG;
    } else if (lowerStr.length < 3) {
      return S_LAST_NAME_LENGTH_ERROR_MSG;

    } else {
      return SUCCESS_VALIDATION_MSG;
    }

  },

  validateFirstName: function (str) {

    const lowerStr = str ? str.toLowerCase() : "",
      wrongWords = [' llc', ' inc', 'llc ', 'inc '],
      containsWrongWords = wrongWords.some(word => lowerStr.includes(word));

    if (lowerStr.length === 0) {
      return BLANK_BOX_ERROR_MSG;
    }
    else if (
      lowerStr.includes(" and ") ||
      lowerStr.startsWith("and ") ||
      lowerStr.endsWith(" and") ||
      lowerStr === "and" ||
      lowerStr.includes(" an ") ||
      lowerStr.startsWith("an ") ||
      lowerStr.endsWith(" an") ||
      lowerStr === "an" ||
      lowerStr.includes("&") ||
      lowerStr.includes(" & ") ||
      lowerStr.startsWith("& ") ||
      lowerStr.endsWith(" &") ||
      lowerStr === "&"
    ) {
      return BILLING_FIRST_NAME_WRONG_LETTER_ERROR_MSG;
    }
    else if (containsWrongWords || lowerStr === 'inc' || lowerStr === 'llc') {
      return BILLING_FIRST_NAME_WRONG_WORD_BUSINESS;
    }
    else if (lowerStr.length < 2) {
      return BILLING_FIRST_NAME_LENGTH_ERROR_MSG;
    }
    else {
      return SUCCESS_VALIDATION_MSG;
    }
  },

  validateLastName: function (str) {

    const lowerStr = str ? str.toLowerCase() : "",
      wrongWords = [' llc', ' inc', 'llc ', 'inc '],
      containsWrongWords = wrongWords.some(word => lowerStr.includes(word));

    if (lowerStr.length === 0) {
      return BLANK_BOX_ERROR_MSG;
    }
    else if (
      lowerStr.includes(" and ") ||
      lowerStr.startsWith("and ") ||
      lowerStr.endsWith(" and") ||
      lowerStr === "and" ||
      lowerStr.includes(" an ") ||
      lowerStr.startsWith("an ") ||
      lowerStr.endsWith(" an") ||
      lowerStr === "an" ||
      lowerStr.includes("&") ||
      lowerStr.includes(" & ") ||
      lowerStr.startsWith("& ") ||
      lowerStr.endsWith(" &") ||
      lowerStr === "&"
    ) {
      return BILLING_LAST_NAME_WRONG_LETTER_ERROR_MSG;
    }
    else if (containsWrongWords || lowerStr === 'inc' || lowerStr === 'llc') {
      return BILLING_LAST_NAME_WRONG_WORD_BUSINESS;
    }
    else {
      return SUCCESS_VALIDATION_MSG;
    }

  },

  validatePhone: function (phone) {

    let stripped = phone && phone.replace(/[^0-9]/g, "");
    if (!phone) {
      return BLANK_BOX_ERROR_MSG;
    } else if (stripped.length != 10) {
      return PHONE_NUMBER_LENGTH_ERROR_MSG;
    } else {
      return SUCCESS_VALIDATION_MSG;
    }

  },

  validationNumbers: function (t) {
    var regex = /\d/g;
    return regex.test(t);
  },

  eatFirstNumber: function (str) {

    if (!isNaN(str.charAt(0))) {
      var firstNumberStr = str.replace(/(^\d+)(.+$)/i, '$1');
      var newstr = str.slice(firstNumberStr.length);
      console.log(newstr);
      return newstr;
    } else {
      console.log(str);
      return str;
    }
  },

  validateEmail: function (email) {
    let emailStr = email ? email.toLowerCase() : "";

    //const emailRegex = /^([\w-]|(?<!\.)\.)+[a-zA-Z0-9]@[a-zA-Z0-9]([\w\-]+)((\.([a-zA-Z]){2,9})+)$/
    var emailRegex = /^(?:[a-z0-9!#$%&amp;'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&amp;'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])$/;

    console.log("RegEx: ", emailRegex.test(emailStr));

    if (emailStr.length === 0) {
      return BLANK_BOX_ERROR_MSG;
    } else if (
      emailStr.endsWith(".cim") ||
      emailStr.endsWith(".nt") ||
      emailStr.endsWith(".cm") ||
      emailRegex.test(emailStr) === false
    ) {
      return EMAIL_ADDRESS_FORMAT_ERROR_MSG;
    } else {
      let second_part = emailStr.split("@");
      var dot_num = second_part[1].match(/\./g).length;
      console.log(dot_num);

      if (dot_num !== 1 && dot_num !== 2)
        return EMAIL_ADDRESS_FORMAT_ERROR_MSG;

      console.log("second_part", second_part[1])
      let rmedFirstNumStr = this.eatFirstNumber(second_part[1])
      console.log("rmedFirstNumStr", rmedFirstNumStr)
      let suffix_list = rmedFirstNumStr.split(".");

      console.log("suffix list", suffix_list)

      if (suffix_list.length === 2) {
        if (this.hasNumbers(suffix_list[1]) === true || suffix_list[1].length > 3 || suffix_list[1].length < 2) {
          return EMAIL_ADDRESS_FORMAT_ERROR_MSG;
        }
      }
      if (suffix_list.length === 3) {
        if (this.hasNumbers(suffix_list[2]) === true || suffix_list[1].length !== 2 || suffix_list[2].length !== 3) {
          return EMAIL_ADDRESS_FORMAT_ERROR_MSG;
        }
      }

      return SUCCESS_VALIDATION_MSG;
    }
  },
  validateShippingCity: function (str) {

    const lowerStr = str ? str.toLowerCase() : "";

    if (lowerStr.length === 0) {
      return BLANK_BOX_ERROR_MSG;
    } else {
      return SUCCESS_VALIDATION_MSG;
    }
  },

  validateZipCode: function (zip) {

    const lowerStr = zip == null ? "" : zip.toString();

    if (lowerStr.length === 0) {
      return BLANK_BOX_ERROR_MSG;
    } else if (lowerStr.length < 5) {
      return ZIP_CODE_FORMAT_ERROR_MSG;
    } else {
      return SUCCESS_VALIDATION_MSG;
    }
  },

  validateAddressType: function (type, str) {
    let parsedStr = parseInt(str);
    if (type && isNaN(parsedStr)) {
      return false;
    } else {
      return true;
    }
  },

  validateAddress: function (str) {
    if (
      str &&
      (str.includes("@") ||
        str.includes(".com") ||
        str.includes(".net") ||
        str.includes(".org") ||
        str.includes(".gov"))
    ) {
      return false;
    } else {
      return true;
    }
  },

  validateStreetAddress: function (arg_str, apt_str) {

    let str = arg_str == null || arg_str == undefined ? "" : arg_str.toLowerCase();
    apt_str = apt_str ? apt_str.toLowerCase() : "";
    if (str.length === 0) {
      return BLANK_BOX_ERROR_MSG;
    } else if (str.includes('po box'))
      return STREET_ADDRESS_PO_BOX_ERROR_MSG;
    else if (!this.hasNumbers(str))
      return STREET_ADDRESS_HASNUMBER_ERROR_MSG;
    else if (apt_str === str)
      return STREET_ADDRESS_MATCH_APARTMENT_ERROR_MSG;
    else if (str && str.length > 0) {
      let regexToStreetAddress = /^(?=.*\d)(?=.*[a-zA-Z]).*$/;
      if (str.match(regexToStreetAddress)) {
        return SUCCESS_VALIDATION_MSG;
      } else {
        return STREET_ADDRESS_INVALID_ERROR_MSG;
      }
    }
    else
      return SUCCESS_VALIDATION_MSG;
  },
  validateAptUnit: function (str) {
    if (this.isEmptyString(str))
      return false;

    let regexToStreetAddress = /^[0-9]{1,12}$/;
    if (str.match(regexToStreetAddress)) {
      return false;
    } else {
      return APT_UNIT_INVALID_ERROR_MSG;
    }
  },

  standardizeAddress: function (type, str) {
    let parsedStr = parseInt(str);
    if (!type && isNaN(parsedStr)) {
      return str;
    } else {
      return "PO BOX " + parsedStr;
    }
  },

  validateCardNumber: function (str) {
    if (!str) return false;
    return !!str.match(/^[0-9 ]+$/);
  },

  hasNumbers: function (t) {
    var regex = /\d/g;
    return regex.test(t);
  },

  validateBusinessName: function (businessName) {
    if (!businessName || businessName.toString().length < 3) return false;
    return businessName;
  },
  validateEin: function (ein) {
    if (!ein || ein.toString().length < 10) return false;
    return !!ein.match(/^[0-9 \-]+$/);
  },

  standardizePhone: function (phone) {
    let stripped = phone.replace(/[^0-9]/g, "");
    if (stripped.length >= 10) {
      return (
        stripped.substring(0, 3) +
        "-" +
        stripped.substring(3, 6) +
        "-" +
        stripped.substring(6)
      );
    } else {
      return stripped;
    }
  },

  validateInteger: function (str) {
    if (!str) return false;
    if (typeof str === "number") return true;
    return !!str.match(/^[0-9]+$/);
  },

  validateDollarsCents: function (str) {
    // Remove commas and $ sign from string
    let commmaRemovedStr = str;
    if (typeof str === "string") {
      commmaRemovedStr = str.replace(/,/g, "");
      commmaRemovedStr = Number(commmaRemovedStr.replace(/\$/g, ""));
    }

    if (!commmaRemovedStr) return false;
    // validate after decimal
    if (this.validateDecimals(commmaRemovedStr) > 2) {
      return false;
    }
    if (typeof commmaRemovedStr === "number") return true;

    return !!commmaRemovedStr.match(/^[$]?[0-9]+[.]?[0-9]{0,2}$/);
  },

  validateDecimals(str) {
    const value = Number(str);
    if (value % 1 != 0) return value.toString().split(".")[1].length;
    return 0;
  },

  validateSsn: function (str) {
    if (!str) return false;
    return !!str.match(/^[0-9]{3}[-]?[0-9]{2}[-]?[0-9]{4}$/);
  },
  validateSpouseSsn: function (str) {
    if (str.length == 0) return true;
    return !!str.match(/^[0-9]{3}[-]?[0-9]{2}[-]?[0-9]{4}$/);
  },

  validateCardExpiry: function (month, year) {
    const curYear = parseInt(new Date().getFullYear());
    const curMonth = parseInt(new Date().getMonth());
    if ((year == curYear && month > curMonth) || (year > curYear)) {
      return true;
    } else {
      return false;
    }
  },

  standardizeSsn: function (str) {
    let stripped = str.replace(/[^0-9]/g, "");
    if (stripped.length >= 9) {
      return (
        stripped.substring(0, 3) +
        "-" +
        stripped.substring(3, 5) +
        "-" +
        stripped.substring(5)
      );
    } else {
      return stripped;
    }
  },

  // validateEin: function(str) {
  //   if (!str) return false;
  //   return !!str.match(/^[0-9]{2}[-]?[0-9]{7}$/);
  // },

  standardizeEin: function (str) {
    let stripped = str.replace(/[^0-9]/g, "");
    if (stripped.length >= 9) {
      return stripped.substring(0, 2) + "-" + stripped.substring(2);
    } else {
      return stripped;
    }
  },

  standardizeIllinoisId: function (str) {
    let stripped = str.replace(/[^0-9]/g, "");
    if (stripped.length >= 8) {
      return stripped.substring(0, 4) + "-" + stripped.substring(4, 8);
    } else {
      return stripped;
    }
  },

  isMobile: function () {
    if (window.innerWidth < 576) {
      return true;
    } else {
      return false;
    }
  },

  isEmptyString: function (value) {
    if (value == null || value == undefined || value == "")
      return true;
    return false;
  },

  isTrimEmptyString: function (value) {
    if (value == null || value == undefined || value == "" || value.trim() == "")
      return true;
    return false;
  },

  isNullOrUnd: function (value) {
    if (value == null || value == undefined)
      return true;
    return false;
  },

  validateAptUnit2: function (value) {
    if (this.isEmptyString(value))
      return BLANK_BOX_ERROR_MSG;
    else
      return false;
  },

  validateState: function (value) {
    return this.isEmptyString(value) ? BLANK_BOX_ERROR_MSG : false;
  },

  einCorporationSubTypeName(sub_type) {
    switch (sub_type) {
      case "Partnership":
      case "Joint Venture":
      case "Corporation":
      case "S Corporation":
      case "Personal Service Corporation":
        return sub_type;
      case "REIT":
        return "Real Estate Investment Trust (REIT)";
      case "RIC":
        return "Regulated Investment Conduit (RIC)";
      case "Settlement Fund":
        return "Settlement Funds";
    }
  },
  einTrustSubTypeName(sub_type) {
    switch (sub_type) {
       case "Bankruptcy Estate":
         return "Bankruptcy Estate (Individual)";
       case "Charitable Lead Annuity":
         return "Charitable Lead Annuity Trust";
       case "Charitable Lead Uni":
         return "Charitable Lead Unitrust";
       case "Charitable Remainder Annuity":
         return "Charitable Remainder Annuity Trust";
       case "Charitable Remainder Uni":
         return "Charitable Remainder Unitrust";
       case "FNMA":
         return "FNMA (Fannie Mae)";
       case "GNMA":
         return "GNMA (Ginnie Mae)";
       case "Qualified Funeral":
         return "Qualified Funeral Trust";
       case "Settlement Fund":
         return "Settlement Fund (under IRC Sec 468B)";
       case "Trust (Others)":
         return "Trust (All Others)";
       case "Conservatorship":
       case "Custodianship":
       case "Escrow":
       case "Guardianship":
       case "Irrevocable Trust":
       case "Pooled Income Fund":
       case "Receivership":
       case "Revocable Trust":
         return sub_type;
    }
  },
  monthDiff(d1, d2) {
    var months;
    months = (d2.getFullYear() - d1.getFullYear()) * 12;
    months -= d1.getMonth();
    months += d2.getMonth();
    return months;
  }
};
