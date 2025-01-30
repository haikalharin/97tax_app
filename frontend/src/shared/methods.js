export const methods = {
  /**
   * Opens a new browser window to follow USPS Tracking
   * @param  {string} trackingNumber - This is the USPS Tracking code
   * @return {void}   Opens the tracking window
   */
  uspsTracking(trackingNumber) {
    const URL = `https://tools.usps.com/go/TrackConfirmAction?qtc_tLabels1=${trackingNumber}`;
    const win = window.open(URL, '_blank');
    win.focus();
  }
}