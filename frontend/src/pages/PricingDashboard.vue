<template>
  <div class="container mt-2">
    <div>
      <div class="inner-page-title mb-2 mt-4">
        Pricing Dashboard
      </div>
      <div class="inner-panel p-3">
        <table class="table table-bordred pricing-table">
            <thead>
                <th>Product</th>
                <th>Current Price</th>
                <th>Change Price To</th>
                <th>Time Period (From)</th>
                <th>Time Period (Through)</th>
                <th>Additional Timeframes</th>
                <th>Price Updating</th>
            </thead>
            <tbody>
                <tr v-for="(element, index) in priceList" :key="index">
                    <td :class="['text-nowrap', element.isPrimary===true ? 'font-weight-bold' : '' ]">
                        <template v-if="element.isPrimary === true">
                            {{ element.product }}
                        </template>
                        <template v-else>
                            {{ getSubOptionName(element.subOptions) }}
                        </template>
                    </td>
                    <td>
                        <div class="price-input-wrapper">
                            $<input type="number" class="change-price-item" v-model="element.currentPrice" />
                        </div> 
                    </td>
                    <td>
                        <div class="price-input-wrapper">
                            $<input type="number" class="change-price-item" v-model="element.changePrice" />
                        </div>
                    </td>
                    <td>
                        <date-range-picker
                            ref="picker"
                            :opens="'false'"
                            :locale-data="{ firstDay: 1, format: 'dd-mm-yyyy HH:mm:ss' }"
                            :singleDatePicker="'single'"
                            :timePicker="true"
                            :timePicker24Hour="true"
                            :ranges="false"
                            v-model="element.timeperiodsForStart"
                            @update="isValid"
                        >
                            <div slot="input" slot-scope="picker">
                                <template v-if="picker.startDate">
                                    {{ formateDate(picker.startDate) }}
                                    <!-- <span class="btn-times" @click.prevent="element.timeperiodsForStart.startDate=null">&times;</span> -->
                                </template>
                                <template v-else>
                                    Select Date
                                </template>
                            </div>
                        </date-range-picker>
                    </td>
                    <td>
                        <date-range-picker
                            ref="picker"
                            :opens="'false'"
                            :locale-data="{ firstDay: 1, format: 'dd-mm-yyyy HH:mm:ss' }"
                            :singleDatePicker="'single'"
                            :timePicker="true"
                            :timePicker24Hour="true"
                            :ranges="false"
                            v-model="element.timeperiodsForEnd"
                            @update="isValid"
                        >
                            <div slot="input" slot-scope="picker">
                                <template v-if="picker.startDate">
                                    {{ formateDate(picker.startDate) }}
                                    <!-- <span class="btn-times" @click.stop="element.timeperiodsForEnd.startDate=null">&times;</span> -->
                                </template>
                                <template v-else>
                                    Select Date
                                </template>
                            </div>
                        </date-range-picker>
                    </td>
                    <td>
                        <button type="button" class="btn btn-add-timeframe text-nowrap" @click="openAdditionalFrames(element)">
                            +Add {{ element.additionalTimeframes ? JSON.parse(element.additionalTimeframes).length : 0 }}
                            <font-awesome-icon :icon="['fa', 'pencil-alt']" class="ml-2" />

                        </button>
                    </td>
                    <td class="text-nowrap">
                        <button type="button" class="btn btn-update-price" @click="updatePrice(element)">
                            Update Price
                            <font-awesome-icon :icon="['fa', 'cog']" class="ml-2" />
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
        <div class="button-wrap">
            <div :class="'btn-custome-rounded ' + (saveAll ? 'disabled' : '')" @click="onClickSaveAll()">
                <!--<font-awesome-icon :icon="['fa', 'lock']" class="ml-2" />-->
                <img src="@/assets/lock.png">
                SAVE CHANGES
            </div>
        </div>
      </div>
    </div>

    <b-modal
        ref="additionaTimeframe"
        id="additionaTimeframe"
        title="Additional Timeframes"
        hide-footer
        hide-header
        no-close-on-backdrop
        no-close-on-esc

    >
        <AdditionalTimeframes :dataList="dataList" v-if="dataList" @saveFrames="onSaveFrames" @cancelFramesModal="hideModal"  />
    </b-modal>
  </div>
</template>

<script>
import {AXIOS}   from '../scripts/http-common';
import moment from 'moment';
import DateRangePicker from 'vue2-daterange-picker';
import format from "date-fns/format";
import AdditionalTimeframes from "./AdditionalTimeframes.vue";
import 'vue2-daterange-picker/dist/vue2-daterange-picker.css';
import { Datetime } from 'vue-datetime';
import 'vue-datetime/dist/vue-datetime.css'

export default {
    name: 'PricingDashboard',
    components: {
        DateRangePicker,
        AdditionalTimeframes,
        datetime: Datetime
    },
    data() {
        return {
            priceList: [],
            selectedElement: null,
            saveAll: true,
            timeperiods: {
                startDate: null,
                endDate: null
            },
            dataList: [],
            dateFormat: "MM/DD/YYYY",
        }
    },
    mounted() {
        AXIOS.get(`/admin/productPrices`).then(res=> {
            if (res.status == 200) {
                this.priceList = res.data;
                this.priceList.map((it, index) => {
                    it.timeperiodsForStart = {
                        startDate: it.timePeriodsStart,
                        endDate: null
                    };
                    it.timeperiodsForEnd = {
                        startDate: it.timePeriodsEnd,
                        endDate: null
                    };
                });
            }
            this.isValid()
        });
    },
    methods: {
        isValid() {
            console.log(this.priceList)
            this.saveAll = !this.priceList.reduce((acc, ele)=> {
                return ele.timeperiodsForEnd.startDate != null &&  ele.timeperiodsForStart.startDate != null && acc
            }, true);
            console.log(this.saveAll);
        },
        openAdditionalFrames(element) {
            let additionalTimeframes = element.additionalTimeframes;
            let timeFramesList = additionalTimeframes ? JSON.parse(additionalTimeframes) : [];
            // console.log(timeFramesList);
            this.dataList = [];
            timeFramesList ? timeFramesList.forEach(element => {
                this.dataList.push({
                    timeperiodsForStart: {
                        startDate: element.startDate,
                        endDate: null
                    },
                    timeperiodsForEnd: {
                        startDate: element.endDate,
                        endDate: null
                    },
                    price: element.price
                });
            }) : this.dataList = [];
            this.selectedElement = element;
            this.$refs.additionaTimeframe.show();
            
        },
        getSubOptionName(subOption) {
            let subOptions = [
                {
                    key: 'express',
                    label: 'Deluxe Processing'
                }, {
                    key: 'change_of_address',
                    label: 'Change Of Address'
                }, {
                    key: 'payroll',
                    label: 'Payroll Option'
                }
            ];
            let index = subOptions.findIndex(it => {
                return it.key == subOption
            });
            return index == -1 ? "" : subOptions[index].label;
        },
        formateDate(value){
            if (value) {
                return moment(String(value)).format('MM/DD/YYYY HH:mm')
            }
        },
        hideModal(data) {
            this.$refs.additionaTimeframe.hide();
        },
        onSaveFrames(timeframes) {
            if (this.selectedElement) {
                let index = this.priceList.findIndex(it => {
                    return it.id == this.selectedElement.id
                });
                if (index > -1) {
                    this.priceList[index].additionalTimeframes = JSON.stringify(timeframes);
                }
            }
            this.$refs.additionaTimeframe.hide();
        },
        updatePrice(element) {
            let requestBody = _.cloneDeep(element);
            delete requestBody.timeperiodsForStart;
            delete requestBody.timeperiodsForEnd;
            requestBody.timePeriodsStart = moment.utc(element.timeperiodsForStart.startDate).format();
            requestBody.timePeriodsEnd = moment.utc(element.timeperiodsForEnd.startDate).format();
            console.log(requestBody);
            // console.log(element);
            AXIOS.post(`/admin/updatePrice`, requestBody).then(res=>{
                if (res.status == 200) {
                    alert('Updated successfully!')
                }
            }).catch(err => {
                alert('Error occured while updating prices');
                console.log(err);
            });
        },

        onClickSaveAll() {
            if(this.saveAll)
                return;

            let requestBodyList = [];
            this.priceList.forEach(element => {
                let requestBody = _.cloneDeep(element);
                delete requestBody.timeperiodsForStart;
                delete requestBody.timeperiodsForEnd;
                requestBody.timePeriodsStart = moment.utc(element.timeperiodsForStart.startDate).format();
                requestBody.timePeriodsEnd = moment.utc(element.timeperiodsForEnd.startDate).format();
                requestBodyList.push(requestBody);
            });
            AXIOS.post(`/admin/updateAllPrice`, requestBodyList).then(res => {
                if (res.status == 200) {
                    alert('Updated Successfully!');
                }
            }).catch(err => {
                alert('Error occured while updating prices');
                console.log(err);
            });
        }

    }
}
</script>

<style>

    .pricing-table th, td {
        font-family: "Archivo", sans-serif;
    }

    input[type=number]::-webkit-inner-spin-button, 
    input[type=number]::-webkit-outer-spin-button {
        opacity: 1;
    }

    .table th {
        border-top: none;
    }

    table.pricing-table {
        font-size: 14px !important;
    }

    .price-input-wrapper {
        display: flex;
        padding: 6px;
        background: #E8F7FD;
        border-radius: 8px;
        background: #E8F7FD;
        border-radius: 8px;
    }

    .daterangepicker td.active.in-range,
    .daterangepicker td.active {
        background-color: #00a699 !important;
        color: white !important;
    }

    .daterangepicker.show-calendar .drp-buttons .applyBtn {
        background-color: #00a699 !important;
    }

    .daterangepicker td.in-range {
        background-color: #66e2da !important;
        color: white !important;
    }

    table.pricing-table td {
        vertical-align: middle !important;
    }

    table.pricing-table .btn-default {
        border: 1px solid #CCC;
    }

    .btn-add-timeframe {
        color: #333333 !important;
        font-weight: 600 !important;
        border: 2px solid #2AC44C!important;
        border-radius: 8px!important;
        font-size: 14px !important;

        font-family: 'Archivo', sans-serif;
        font-style: normal;
        font-weight: 700;
        font-size: 14px;
        line-height: 17px;
    }

    .btn-update-price {
        color: #333333 !important;
        font-weight: 600 !important;
        border: 2px solid #D1D1D1!important;
        border-radius: 8px!important;
        font-size: 14px !important;

        font-family: 'Archivo', sans-serif;
        font-style: normal;
        font-weight: 700;
        font-size: 14px;
        line-height: 17px;
    }

    table.pricing-table .vue-daterange-picker .reportrange-text {
        width: 150px;
        padding: 5px 10px;
        background-color: #ECECEC;
        border: none;
        text-align: left;
        height: unset;
        position: relative;
        font-size: 14px;
    }

    .vue-daterange-picker .calendars {
        width: 300px !important;
    }

    .vue-daterange-picker .reportrange-text .btn-times {
        position: absolute;
        right: 5px;
        top: 50%;
        transform: translateY(-50%);
        display: flex;
        align-items: center;
        color: #dc3545;
        font-weight: bold;
        line-height: 100%;
    }

    .change-price-item {
        border: none;
        width: 75px;
        padding-left: 1px !important;
        background: #E8F7FD;
    }

    input.change-price-item:focus {
        border: none!important;
        outline: none;
        background-color: #ECECEC;
    }

    .reportrange-text {
        background: #F5F5F5!important;
        border-radius: 4px!important;
        color: #B7B7B7;
    }

    .button-wrap {
        text-align: right;
    }

</style>