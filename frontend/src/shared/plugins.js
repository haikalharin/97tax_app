export const plugins = {
    install(Vue, options) {
        Vue.prototype.nFormatter = (num) => {
            if (num >= 1000000000) {
                return (num / 1000000000).toFixed(1).replace(/\.0$/, '') + 'G';
            }
            if (num >= 1000000) {
                return (num / 1000000).toFixed(1).replace(/\.0$/, '') + 'M';
            }
            if (num >= 1000) {
                return (num / 1000).toFixed(1).replace(/\.0$/, '') + 'K';
            }
            return num;
        }
        Vue.prototype.formatCurrencyWithCommas = (amount) => {
            return amount.toLocaleString();
        }
        Vue.prototype.formatCurrency = (amount) => {
            return `$${amount.toFixed(2).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')}`;
        }
    }
}
