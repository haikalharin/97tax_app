import * as crypto from 'crypto';

export function hashEmail(email) {
  const lowerCaseEmail = email.toLowerCase();
  const [localPart, domainPart] = lowerCaseEmail.split('@');
  let cleanedLocalPart = localPart;
  if (domainPart == 'gmail.com' || domainPart == "googlemail.com") {
    cleanedLocalPart = localPart.replace(/\./g, '');
  }
  const cleanedEmail = `${cleanedLocalPart}@${domainPart}`;
  const hash = crypto.createHash('sha256').update(cleanedEmail).digest('hex');
  return hash;
}

export function hashPhone(phoneNumber) {
  const cleanedNumber = phoneNumber.replace(/\D/g, '');
  const formattedNumber = `+1${cleanedNumber}`;
  const hash = crypto.createHash('sha256').update(formattedNumber).digest('hex');
  return hash;
}

export function cleanupAddress(addressComponent) {
  let cleanedAddress = addressComponent.replace(/[\d\W_]+/g, ' ');
  cleanedAddress = cleanedAddress.toLowerCase();
  cleanedAddress = cleanedAddress.replace(/\s+/g, ' ');
  cleanedAddress = cleanedAddress.trim();
  return cleanedAddress;
}

export function hashAddress(addressComponent) {
  const cleanedAddress = cleanupAddress(addressComponent);
  const hash = crypto.createHash('sha256').update(cleanedAddress).digest('hex');
  return hash;
}

export function cleanupPostalCode(postalCode) {
  let cleanedPostalCode = postalCode.replace(/[.~]/g, '');
  cleanedPostalCode = cleanedPostalCode.trim();
  return cleanedPostalCode;
}
