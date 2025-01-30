<template>
  <div class="additional-timeframes" v-if="timeFrames">
      <h3 class="pb-2">
        Additional Timeframes
      </h3>
      <ul>
          <li>
              <label> From: </label>
              <label> To: </label>
          </li>
          <li v-for="(element, index) in timeFrames" :key="index">
                <date-range-picker
                    ref="picker"
                    :opens="'false'"
                    :locale-data="{ firstDay: 1, format: 'dd-mm-yyyy HH:mm:ss' }"
                    :singleDatePicker="'single'"
                    :timePicker="true"
                    :timePicker24Hour="true"
                    :ranges="false"
                    v-model="element.timeperiodsForStart"
                >
                    <div slot="input" slot-scope="picker">
                        <template v-if="picker.startDate">
                            {{ formateDate(picker.startDate) }}
                            <span class="btn-times" @click.stop="element.timeperiodsForStart.startDate=null">&times;</span>
                        </template>
                        <template v-else>
                            Select Date
                        </template>
                    </div>
                </date-range-picker>
                <date-range-picker
                    ref="picker"
                    :opens="'false'"
                    :locale-data="{ firstDay: 1, format: 'dd-mm-yyyy HH:mm:ss' }"
                    :singleDatePicker="'single'"
                    :timePicker="true"
                    :timePicker24Hour="true"
                    :ranges="false"
                    v-model="element.timeperiodsForEnd"
                >
                    <div slot="input" slot-scope="picker">
                        <template v-if="picker.startDate">
                            {{ formateDate(picker.startDate) }}
                            <span class="btn-times" @click.stop="element.timeperiodsForEnd.startDate=null">&times;</span>
                        </template>
                        <template v-else>
                            Select Date
                        </template>
                    </div>
                </date-range-picker>
                <div class="additional-price-wrapper">
                    <b>$</b>
                    <input type="text" v-model="element.price" class="additional-price" :ref="'additionaL-price-' + index">
                    <span class="btn-icon-trash" @click="removeItem(index)">
                        <img src="@/assets/icon_trash.svg" />
                    </span>
                </div>
          </li>
      </ul>
      <div class="d-flex justify-content-end mt-4">
          <button type="button" class="btn btn-primary" @click="onAddTimeFrame()">Add</button>
          <button type="button" class="btn btn-danger ml-2" @click="onClickCancel()">Cancel</button>
          <button type="button" class="btn btn-success ml-2" @click="onClickSave()">Save</button>
      </div>
  </div>
</template>

<script>
import moment from 'moment';
import _ from "lodash";
import DateRangePicker from 'vue2-daterange-picker';
import 'vue2-daterange-picker/dist/vue2-daterange-picker.css';

export default {
    name: 'AdditionalTimeframe',
    components: {
        DateRangePicker
    },
    data() {
        return {
            timeFrames: null
        }
    },
    props: {
        dataList: {
            type: Array,
            required: true
        }
    },
    mounted() {
        this.timeFrames = [];
        this.timeFrames = _.cloneDeep(this.dataList);
    },
    methods: {
        removeItem(index) {
            this.timeFrames.splice(index, 1);
        },
        formateDate(value){
            if (value) {
                return moment(String(value)).format('MM/DD/YYYY')
            }
        },
        onAddTimeFrame() {
            this.timeFrames.push({
                timeperiodsForStart: {
                    startDate: null,
                    endDate: null
                },
                timeperiodsForEnd: {
                    startDate: null,
                    endDate: null
                },
                price: 0
            })
        },
        formateDate(value){
            console.log(value);
            if (value) {
                return moment(String(value)).format('MM/DD/YYYY HH:mm')
            }
        },
        onClickSave() {
            let items = [];
            this.timeFrames.forEach(element => {
                items.push({
                    startDate: element.timeperiodsForStart.startDate,
                    endDate: element.timeperiodsForEnd.startDate,
                    price: element.price
                });
            });
            this.$emit('saveFrames', items);
            this.timeFrames = [];
        },
        onClickCancel() {
            this.$emit("cancelFramesModal", 'cancel')
        }
    },
    watch: {
        dataList: function() {
            this.timeFrames = [];
            this.timeFrames = _.cloneDeep(this.dataList);
        }
    }
}
</script>

<style>

.additional-timeframes h3 {
    font-size: 24px;
}
.additional-timeframes ul {
    padding-left: 0px;
    list-style-type: none;
}

.additional-timeframes ul li {
    display: flex;
    flex-wrap: wrap;
    margin-bottom: 10px;;
}

.additional-timeframes ul li .vue-daterange-picker .reportrange-text {
    border: 1px solid #777;
    font-size: 14px;
    text-align: left;
    width: unset;
}

.additional-timeframes ul li > *:nth-child(1),
.additional-timeframes ul li > *:nth-child(2) {
    flex-basis: calc(40% - 40px);
    width: calc(40% - 40px);
    margin: 0px 10px;
}

.additional-timeframes ul li > *:nth-child(3) {
    flex: 1;
    width: 20%;
    margin: 0px 10px;
}
.additional-price-wrapper {
    /* background-color: red; */
    display: flex;
    align-items: center;
}
.additional-price-wrapper b {
    color: #04dd15;
}
.additional-price {
    width: 80px;
    padding: 5px 10px 5px 3px;
    border: none;
    box-shadow: none;
    color: #04dd15;
    font-weight: bold;
}

.additional-timeframes ul li .btn-icon-trash  {
    width: 20px;
    cursor: pointer;
}
</style>
