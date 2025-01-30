<template>
    <div class="container mt-2">
        <div>
            <div class="inner-page-title mb-2 mt-4">
                Sales Dashboard
            </div>
            <div class="inner-panel p-3">
                <table class="table table-bordred sales-list-table">
                    <thead>
                        <th>Product</th>
                        <th>Phone Number:</th>
                        <th>
                          Display Time<br/>
                          (From/Through):
                        </th>
                        <th>
                          Daily Time<br/>
                          (From/Through):
                        </th>
                        <th>On\Off:</th>
                    </thead>
                    <tbody>
                        <tr v-for="(element, index) in salesList" :key="index">
                            <td :class="['text-nowrap', element.isPrimary === true ? 'font-weight-bold' : '']">
                                {{ element.product }}
                            </td>
                            <td>
                                <input type="text" v-mask="'###-###-####'" v-model="element.phoneNumber" class="form-control" />
                                <!-- {{ element.phoneNumber }} -->
                            </td>
                            <td>
                                <date-range-picker ref="picker" :opens="'false'"
                                    :locale-data="{ firstDay: 1, format: 'dd-mm-yyyy HH:mm:ss' }" :singleDatePicker="'single'"
                                    :timePicker="true" :timePicker24Hour="true" :ranges="false" v-model="element.timeperiodsForStart"
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
                                <br/>
                                <date-range-picker ref="picker" :opens="'false'"
                                    :locale-data="{ firstDay: 1, format: 'dd-mm-yyyy HH:mm:ss' }" :singleDatePicker="'single'"
                                    :timePicker="true" :timePicker24Hour="true" :ranges="false" v-model="element.timeperiodsForEnd"
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
                                <datetime input-class="custom-time-input" type="time"
                                    value-zone="America/New_York" zone="America/New_York" v-model="element.timeStart"></datetime>
                                <datetime input-class="custom-time-input" type="time"
                                    value-zone="America/New_York" zone="America/New_York" v-model="element.timeEnd"></datetime>
                            </td>
                            <!-- <td>
                                <date-range-picker ref="picker" :opens="'false'"
                                    :locale-data="{ firstDay: 1, format: 'dd-mm-yyyy HH:mm:ss' }" :singleDatePicker="'range'"
                                    :timePicker="true" :timePicker24Hour="true" :ranges="false" v-model="element.timeperiods">
                                    <div slot="input" slot-scope="picker">
                                        <template v-if="picker.startDate">
                                            {{ formateDate(picker.startDate) }} - {{ formateDate(picker.endDate) }}
                                        </template>
                                        <template v-else>
                                            Select Date
                                        </template>
                                    </div>
                                </date-range-picker>
                            </td> -->
                            <td>
                                <toggle-button :width="40" :height="20" :font-size="18" v-model="element.onOff" color="#00d35f"
                                    :sync="true" :labels="false" @change="''" />
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div class="button-wrap">
                    <div class="btn-custome-rounded" @click="onClickSaveAll()">
                        <!--<font-awesome-icon :icon="['fa', 'lock']" class="ml-2" />-->
                        <img src="@/assets/lock.png">
                        SAVE CHANGES
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { AXIOS } from '../scripts/http-common';
import moment from 'moment';
import DateRangePicker from 'vue2-daterange-picker';
import 'vue2-daterange-picker/dist/vue2-daterange-picker.css';
import SalesWidget from "./../components/SalesWidget.vue";
import { Datetime } from 'vue-datetime';
import 'vue-datetime/dist/vue-datetime.css'

export default {
    name: 'PricingDashboard',
    components: {
        DateRangePicker,
        SalesWidget,
        datetime: Datetime
    },
    data() {
        return {
            salesList: [],
            selectedElement: null,
            dataList: [],
            dateFormat: "MM/DD/YYYY",
        }
    },
    mounted() {
        AXIOS.get(`/admin/salesList`).then(res => {
            if (res.status == 200) {
                this.salesList = res.data;
                this.salesList.map((it, index) => {
                    it.timeperiodsForStart = {
                        startDate: it.startTime,
                        endDate: null
                    };
                    it.timeperiodsForEnd = {
                        startDate: it.endTime,
                        endDate: null
                    };
                    it.timeStart = !!it.startDaily ? moment(it.startDaily).tz("America/New_York").format() : "";
                    it.timeEnd = !!it.endDaily ? moment(it.endDaily).tz("America/New_York").format() : "";
                    // it.timeperiods = {
                    //     startDate: it.startTime,
                    //     endDate: it.endTime
                    // };
                });
            }
        });
    },
    methods: {
        formateDate(value) {
            if (value) {
                return moment(String(value)).format('MM/DD/YYYY HH:mm')
            }
        },

        onClickSaveAll() {
            let requestBodyList = [];
            this.salesList.forEach(element => {
                let requestBody = _.cloneDeep(element);
                delete requestBody.timeperiodsForStart;
                delete requestBody.timeperiodsForEnd;
                delete requestBody.timeStart;
                delete requestBody.timeEnd;

                requestBody.startTime = element.timeperiodsForStart.startDate;
                requestBody.endTime = element.timeperiodsForEnd.startDate;

                requestBody.startDaily = element.timeStart;
                requestBody.endDaily = element.timeEnd;
                requestBodyList.push(requestBody);
            });
            AXIOS.post(`/admin/updateAllSales`, requestBodyList).then(res => {
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
.sales-list-table th, td {
    font-family: "Archivo", sans-serif;
}

.table th {
    border-top: none;
}

table.sales-list-table td {
    vertical-align: middle !important;
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

table.sales-list-table .vue-daterange-picker .reportrange-text {
    width: 150px;
    padding: 5px 10px;
    background-color: #ECECEC;
    border: none;
    font-size: 14px;
    text-align: left;
    height: unset;
}

.reportrange-text {
    background: #F5F5F5!important;
    border-radius: 4px!important;
    color: #B7B7B7;
}

.button-wrap {
    text-align: right;
}

.custom-time-input {
    max-width: 100px;
    margin-bottom: 10px;
    background: #F5F5F5 !important;
    border-radius: 4px !important;
    border: none;
    color: #B7B7B7;
}
</style>
