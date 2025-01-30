export const paymentPlansConfig = {
    newJerseyPaymentPlan: {
        minMonthlyPayment:25,
        minMonth:3,
        maxMonth:60,
        minAmount:500,
        maxAmount:75000,
    },
    californiaPaymentPlan: {
        minMonthlyPayment:25,
        minMonth:3,
        maxMonth:60,
        minAmount:500,
        maxAmount:25000,
    },
    georgiaPaymentPlan: {
        minMonthlyPayment:25,
        minMonth:3,
        maxMonth:60,
        minAmount:500,
        maxAmount:75000,
    },
    michiganPaymentPlan: {
      minMonthlyPayment:25,
      minMonth:3,
      maxMonth:24,
      minAmount:500,
      maxAmount:75000,
    },
    illinoisPaymentPlan:{
        minMonthlyPayment:25,
        minMonth:3,
        maxMonth:60,
        minAmount:500,
        maxAmount:75000,
    },
    irsPaymentPlan:{
        minMonthlyPayment:25,
        minMonth:3,
        maxMonth:72,
        minAmount:500,
        maxAmount:150000
    }
}

export const paymentPlansFlags = {
    isCalifornia: "newJerseyPaymentPlan",
    isGeorgia: "georgiaPaymentPlan",
    isMichigan: "michiganPaymenPlan",
    isIllinois: "illinoisPaymentPlan",
    isIrs: "irsPaymentPlan",
    isNewJersey: "newJerseyPaymentPlan"
}
