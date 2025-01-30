<template>
  <page logoHref="/admin/orders" class="home-page">
    <loader :isLoaderOn="loaderOn"></loader>
    <div class="container">
      <div class="mt-5">
        <button
          class="btn btn-outline-secondary float-right"
          style="z-index: 2;"
          @click="gotoAddUser()"
          v-if="loggedInUser.userType == 'admin'"
        >
          Manage Users
        </button>
      </div>

      <div>
        <b-tabs content-class="mt-3" class="myTabList">
          <b-tab title="Order History" active>
            <h1 class="text-center">Order History</h1>
            <h2 class="mt-4">Search Filters</h2>
            <div class="row mb-2">
              <div class="col-6 col-sm-3">
                <div class="form-group">
                  <label> Email </label>
                  <input class="form-control" v-model="request.query.email"/>
                </div>
              </div>
              <div class="col-6 col-sm-3">
                <div class="form-group">
                  <label> Phone </label>
                  <input class="form-control" v-model="request.query.phone"/>
                </div>
              </div>
              <div class="col-6 col-sm-3">
                <div class="form-group">
                  <label> Product </label>
                  <select class="form-control" v-model="request.query.product">
                    <option value>Any</option>
                    <option value="PaymentPlan">IRS Payment Plan</option>
                    <option value="CaliforniaPayPlan">California Pay Plan</option>
                    <option value="NewJerseyPayPlan">New Jersey Pay Plan</option>
                    <option value="GeorgiaPayPlan">Georgia Pay Plan</option>
                    <option value="IllinoisPayPlan">Illinois Pay Plan</option>
                    <option value="MichiganPayPlan">Michigan Pay Plan</option>
                    <!--option value="TaxLienRemoval">Tax Lien Removal</option-->
                    <option value="PenaltyWaiver">Penalty Waiver</option>
                    <option value="EIN">EIN Applications</option>
                  </select>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-6 col-sm-3">
                <div class="form-group">
                  <label> Order Number </label>
                  <input class="form-control" v-model="request.query.orderId"/>
                </div>
              </div>
              <div class="col-6 col-sm-3">
                <div class="row">
                  <div class="form-group col-6 select-status">
                    <label> Status </label>
                    <b-dropdown :style="{ width: '100%' }">
                      <template slot="button-content">
                        Select
                        <font-awesome-icon
                          :icon="['fa', 'caret-down']"
                        ></font-awesome-icon>
                      </template>
                      <label class="d-block pl-4 pr-4">
                        <input
                          type="checkbox"
                          v-model="request.query.status.any"
                          @change="handleAnyStatusChange"
                        />
                        Any
                      </label>
                      <label class="d-block pl-4 pr-4">
                        <input
                          type="checkbox"
                          v-model="request.query.status.incomplete"
                          @change="handleStatusChange"
                        />
                        Incomplete
                      </label>
                      <label class="d-block pl-4 pr-4">
                        <input
                          type="checkbox"
                          v-model="request.query.status.cancelled"
                          @change="handleStatusChange"
                        />
                        Cancelled
                      </label>
                      <label class="d-block pl-4 pr-4">
                        <input
                          type="checkbox"
                          v-model="request.query.status.processing"
                          @change="handleStatusChange"
                        />
                        Processing
                      </label>
                      <label class="d-block pl-4 pr-4" style="width: 200px;">
                        <input
                          type="checkbox"
                          v-model="request.query.status.awaitingSignatureService"
                          @change="handleStatusChange"
                        />
                        Awaiting Signature
                      </label>
                      <label class="d-block pl-4 pr-4" style="width: 200px;">
                        <input
                          type="checkbox"
                          v-model="request.query.status.signed"
                          @change="handleStatusChange"
                        />
                        Signed
                      </label>
                      <label class="d-block pl-4 pr-4">
                        <input
                          type="checkbox"
                          v-model="request.query.status.onHold"
                          @change="handleStatusChange"
                        />
                        On Hold
                      </label>
                      <label class="d-block pl-4 pr-4">
                        <input
                          type="checkbox"
                          v-model="request.query.status.failed"
                          @change="handleStatusChange"
                        />
                        Failed
                      </label>
                      <label class="d-block pl-4 pr-4">
                        <input
                          type="checkbox"
                          v-model="request.query.status.botError"
                          @change="handleStatusChange"
                        />
                        EIN Error
                      </label>
                      <label class="d-block pl-4 pr-4">
                        <input
                          type="checkbox"
                          v-model="request.query.status.completed"
                          @change="handleStatusChange"
                        />
                        Completed
                      </label>
                      <label class="d-block pl-4 pr-4">
                        <input
                          type="checkbox"
                          v-model="request.query.status.chargeback"
                          @change="handleStatusChange"
                        />
                        Charge Back
                      </label>
                      <label class="d-block pl-4 pr-4">
                        <input
                          type="checkbox"
                          v-model="request.query.status.deleted"
                          @change="handleStatusChange"
                        />
                        Deleted
                      </label>
                    </b-dropdown>
                  </div>
                </div>
              </div>
              <div class="col-6 col-sm-3">
                <div class="form-group">
                  <label> First Name </label>
                  <input class="form-control" v-model="request.query.firstName"/>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-6 col-sm-3">
                <div class="form-group">
                  <label> Time Period </label>
                  <select
                    class="form-control"
                    v-model="request.query.dateRange"
                    @change="handleDataRangeChange()"
                  >
                    <option value="TODAY">Today</option>
                    <option value="YESTERDAY">Yesterday</option>
                    <option value="SEVEN_DAYS">Last 7 Days</option>
                    <option value="THIRTY_DAYS">Last 30 Days</option>
                    <option value="ALL">ALL</option>
                    <option value="CUSTOM">CUSTOM</option>
                  </select>
                </div>
              </div>
              <div class="col-6 col-sm-3">
                <!-- <datepicker :clear-button="true" :bootstrap-styling="true" placeholder="Select Search Date" v-model="request.query.createdAt" id="createdAt"></datepicker> -->
                <div class="form-group select-date">
                  <input
                    placeholder="Select Date"
                    class="form-control"
                    id="searchDate"
                    :value="
                      formatDates(
                        request.query.createdAt.start,
                        request.query.createdAt.end
                      )
                    "
                    :disabled="request.query.dateRange !== 'CUSTOM'"
                  />
                  <span
                    id="cleardates"
                    @click="clearDates()"
                    :disabled="request.query.dateRange !== 'CUSTOM'"
                  >X</span
                  >
                  <AirbnbStyleDatepicker
                    style="position: absolute; width: 350px; left: 15px"
                    :showShortcutsMenuTrigger="false"
                    :trigger-element-id="'searchDate'"
                    :monthsToShow="1"
                    :mode="'range'"
                    :fullscreen-mobile="false"
                    :date-one="request.query.createdAt.start"
                    :date-two="request.query.createdAt.end"
                    @date-one-selected="
                      (val) => {
                        request.query.createdAt.start = val;
                      }
                    "
                    @date-two-selected="
                      (val) => {
                        request.query.createdAt.end = val;
                      }
                    "
                  />
                </div>
              </div>
              <div class="col-6 col-sm-3">
                <div class="form-group">
                  <label> Last Name </label>
                  <input class="form-control" v-model="request.query.lastName"/>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-6 col-sm">
                <button class="btn btn-secondary" @click="resetSearch()">Search</button>
              </div>
            </div>
            <h6 class="mt-4">Results ({{ searchResult.numResults }})</h6>
            <div class="text-center">
              <span v-for="(page, idx) in pages" :key="idx">
                <span class="btn" v-if="page.current">{{ page.pageNumber + 1 }}</span>
                <button class="btn btn-link" href="#" v-else @click="loadPage(page)">
                  {{ page.pageNumber + 1 }}
                </button>
              </span>
            </div>

            <div class="mt-4"></div>

            <div class="row" v-if="searchResult.rows.length === 0">
              <div class="col">
                <div class="font-weight-bold">No orders match the given criteria.</div>
              </div>
            </div>

            <table class="table table-bordered">
              <thead>
              <tr>
                <th></th>
                <th>Date</th>
                <th>Order #</th>
                <th>Status</th>
                <th>First</th>
                <th>Last</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Product</th>
                <th></th>
              </tr>
              </thead>
              <tbody>
              <tr
                v-for="(order, idx) in searchResult.rows"
                :key="idx"
                :style="order.status == 'Charge Back' ? 'color: red' : 'color:black'"
              >
                <td>
                  <input type="checkbox" v-model="order.checked"/>
                </td>
                <td>
                  {{ timetoEst(order.created_date) }}
                </td>
                <td>{{ order.id }}</td>
                <td>{{ order.status == "Bot Error" ? "EIN Error" : order.status }}</td>
                <td>{{ order.fname }}</td>
                <td>{{ order.lname }}</td>
                <td>{{ order.email }}</td>
                <td>{{ order.phone }}</td>
                <td>
                    <span v-if="order.product === 'PaymentPlan'">
                      <span v-if="order.is_california">California Payment Plan</span>
                      <span v-else-if="order.is_new_jersey">New Jersey Payment Plan</span>
                      <span v-else-if="order.is_georgia">Georgia Payment Plan</span>
                      <span v-else-if="order.is_illinois">Illinois Payment Plan</span>
                      <span v-else-if="order.is_michigan">Michigan Payment Plan</span>
                      <span v-else>IRS Payment Plan</span>
                    </span>
                  <span v-if="order.product === 'EIN'">EIN Application</span>
                  <span v-if="order.product !== 'PaymentPlan' && order.product !== 'EIN'">{{
                      order.product
                    }}</span>
                </td>
                <!-- <td><a class="btn btn-secondary" :href="`/admin/order/${order.orderNum}`">Details</a></td> -->
                <td>
                  <button
                    class="btn btn-secondary"
                    @click="gotoDetails(order.id, order.order_type)"
                  >
                    Details
                  </button>
                </td>
              </tr>
              </tbody>
            </table>

            <div class="row">
              <div class="col text-center">
                <span v-for="(page, idx) in pages" :key="idx" class="p-2">
                  <span v-if="page.current">{{ page.pageNumber + 1 }}</span>
                  <button class="btn btn-link" href="#" v-else @click="loadPage(page)">
                    {{ page.pageNumber + 1 }}
                  </button>
                </span>
              </div>
            </div>

            <div class="row" v-if="searchResult.rows.length > 0">
              <div class="col">
                <button class="btn btn-danger" @click="deleteButtonConfirmation()">
                  Delete
                </button>
              </div>
            </div>
          </b-tab>
          <b-tab
            title="Fulfillment"
            v-if="
              loggedInUser.userType == 'admin' ||
              (loggedInUser.userType == 'StandardUser' && loggedInUser.fulfilmentAccess)
            "
          >
            <h1 class="text-center">Fulfillment</h1>
            <div class="row" v-if="processOrderWarning">
              <div class="processordOrderAlert mb-4 mx-auto">
                <span class="font-weight-bold">{{ processOrderWarning }}</span>
              </div>
              <br/>
            </div>
            <div class="row">
              <div class="col-3 mb-4" style="text-align: center">
                <div class="col-12">
                  <div class="mb-4">
                    <b-btn
                      class="font-weight-bold"
                      :disabled="loaderOn"
                      @click="processOrders()"
                    >(Re)Process Orders >
                    </b-btn
                    >
                  </div>
                  <div>
                    <b-btn class="font-weight-bold" href="/admin/mailroom"
                    >Internal Mailroom
                    </b-btn
                    >
                  </div>
                </div>
              </div>

              <div class="col-6 mb-4">
                <div class="row mb-4" style="display: none; justify-content: center">
                  WARNING: 70 orders in batch and 71 orders processing status.<br/>
                </div>

                <div class="row">
                  <div
                    class="container testimonial-group"
                    style="display: flex; justify-content: center"
                  >
                    <div
                      class="row text-center flex-nowrap"
                      v-if="allProcessedOrders.length"
                    >
                      <label style="color: green">Ready to send</label>
                      <div
                        class="col content-center"
                        v-for="order in allProcessedOrders"
                        :key="order.versionId"
                      >
                        <div class="text-center mb-4">
                          <div class="d-flex justify-content-around mt-4">
                            <div class="">
                              <div
                                class="cursor-pointer"
                                @click="downloadProcessedPdf(order)"
                              >
                                <img class="download-icon" src="@/assets/download.png"/>
                                <img class="file-icons-size" src="@/assets/pdf.png"/>
                              </div>
                              <span style="font-size: 10pt">Portfolio.pdf</span>
                            </div>
                            <div class="">
                              <div
                                class="cursor-pointer"
                                @click="downloadProcessedLabelsFile(order)"
                              >
                                <img class="download-icon" src="@/assets/download.png"/>
                                <img class="file-icons-size" src="@/assets/pdf.png"/>
                              </div>
                              <span style="font-size: 10pt">Labels.pdf</span>
                            </div>
                            <div class="" style="color: grey">
                              {{ order.startOrderNumber }} -
                              {{ order.endOrderNumber }}
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="d-flex mb-4 ml-12" style="gap: 12px">
                        <button
                          :disabled="loaderOn"
                          class="btn btn-danger mt-2 font-weight-bold"
                          @click="deleteProcessingOrder"
                        >
                          Delete
                        </button>
                        <button
                          :disabled="loaderOn"
                          class="btn btn-secondary mt-2 font-weight-bold"
                          @click="moveToMailRoom"
                        >
                          Send To Mailroom >
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-3 mb-4">
                <div class="row mb-3" style="display: flex; align-items: center">
                  <div class="font-weight-bold mb-4">Upload Tracking CSV</div>
                  <div class="mb-4">
                    <input
                      type="file"
                      id="file"
                      ref="file"
                      v-on:change="handleFileUpload()"
                    />
                  </div>
                  <div class="mb-4">
                    <button
                      class="btn btn-secondary font-weight-bold"
                      v-on:click="submitFile()"
                    >
                      Submit
                    </button>
                  </div>
                </div>

                <div v-if="fileUploadResult.length > 0">
                  <h3 class="mt-2">Results</h3>
                  <table class="table table-bordered">
                    <thead>
                    <tr>
                      <th>Order Number</th>
                      <th>Message</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="(res, idx) in fileUploadResult" :key="idx">
                      <td>{{ res.orderNum }}</td>
                      <td>{{ res.message }}</td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
            <div
              v-if="
                loggedInUser.userType == 'admin' ||
                (loggedInUser.userType == 'StandardUser' &&
                  loggedInUser.automatedScheduleAccess)
              "
            >
              <hr/>
              <div class="row" style="justify-content: space-around">
                <div class="col-12 col-sm-4 d-flex" style="justify-content: space-around">
                  <div class="d-flex">
                    <toggle-button
                      :width="80"
                      :height="32"
                      :font-size="18"
                      :disabled="isAutomatedScheduleLoading"
                      v-model="isAutomatedSchedule"
                      color="#00d35f"
                      :sync="true"
                      :labels="true"
                      @change="onAutomatedScheduleChange"
                    />
                  </div>

                  <div class="d-flex flex-column">
                    <div class="d-flex">
                      <h5>Automated Schedule</h5>
                    </div>
                    <div class="d-flex">
                      <span>Monday - Friday</span>
                    </div>
                    <div class="d-flex">
                      <select
                        class="form-control ml-2"
                        v-model="automatedScheduleTime"
                        @change="onAutomatedScheduleChange"
                        :disabled="isAutomatedScheduleLoading"
                      >
                        <option
                          v-bind:key="time.value"
                          v-for="time in times"
                          :value="time.value"
                        >
                          {{ time.label }}
                        </option>
                      </select>
                    </div>
                  </div>
                </div>

                <div class="d-flex flex-column">
                  <div class="d-flex align-items-center">
                    <div>
                      <h5>Holiday Exclusions:</h5>
                    </div>
                    <div style="position: relative">
                      <button
                        class="btn btn-secondary ml-2 font-weight-bold"
                        id="add-holiday"
                        :disabled="isAutomatedScheduleLoading"
                      >
                        + Add
                      </button>
                      <AirbnbStyleDatepicker
                        style="position: absolute; width: 350px; left: 100px; top: 0"
                        :show-shortcuts-menu-trigger="false"
                        :trigger-element-id="'add-holiday'"
                        :months-to-show="1"
                        :min-date="currentDate()"
                        :fullscreen-mobile="false"
                        :mode="'single'"
                        :show-action-buttons="true"
                        :close-after-select="false"
                        :disabled-dates="excludedHolidays"
                        @date-one-selected="addExcludedHoliday"
                      />
                    </div>
                  </div>
                  <div
                    class="d-flex align-items-center"
                    v-for="(holiday, index) in excludedHolidays"
                    :key="index"
                  >
                    <div>
                      <button
                        @click="removeExcludedHoliday(index)"
                        style="color: darkred; border: 0; background: none; outline: none"
                        :disabled="isAutomatedScheduleLoading"
                      >
                        <font-awesome-icon :icon="['fas', 'times']"/>
                      </button>
                    </div>
                    <div class="ml-2">
                      <span>{{ formateDate(holiday) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="row">
              <!--   <div class="col-12">
                <div >
          <div class=" content-center row" v-for="order in allProcessedOrders" :key="order.versionId">
          <div class="text-center mb-4">
            <label style="color:green">Ready to send </label><br>
            <div class="d-flex  content-center alighn-right">
              <div class="col-md-6" >
                <div class="cursor-pointer" @click="downloadProcessedPdf(order)">

                <img class="download-icon" src="@/assets/download.png">
              <img class="file-icons-size" src="@/assets/pdf.png">
                </div>
              <span style="font-size:10pt">Portfolio.pdf</span></div>

              <div class="col-md-6" >
                <div class="cursor-pointer"  @click="downloadProcessedCsv(order)">
              <img class="download-icon"  src="@/assets/download.png">
              <img class="file-icons-size" src="@/assets/csv.png">
                </div>
              <span style="font-size:10pt">CurrentOrders.csv</span></div>
            </div>
            <button class="btn btn-secondary mt-2" @click="moveToMailRoom(order)">Send To MailRoom ></button>
          </div>
          </div>
          </div>
              </div>-->
            </div>
            <div class="row">
              <div class="col mt-2">
                <!-- <a
                  class="btn btn-secondary"
                  :href="'/api/admin/orders/paymentPlan_export.xlsx?' + searchParams"
                >Export Payment Plans XLSX</a>-->
                <!-- K1124 -->
                <!-- <a
                  class="btn btn-secondary"
                  :href="'/api/admin/processingorders/orders.xlsx?' + searchParams"
                >Export XLSX</a> -->
              </div>
              <!-- <div class="col mt-2">
                <a
                  class="btn btn-secondary"
                  :href="'/api/admin/orders/oic_export.xlsx?' + searchParams"
                >Export Offer In Compromise XLSX</a>
              </div>-->
              <!-- <div class="col mt-2">
                <a
                  class="btn btn-secondary"
                  :href="'/api/admin/orders/taxLien_export.xlsx?' + searchParams"
                >Export Tax Liens XLSX</a>
              </div>-->
            </div>

            <div class="row">
              <div class="col mt-2">
                <a
                  class="btn btn-secondary"
                  :href="'/api/admin/orders/paymentPlan_export.csv?' + searchParams"
                >Export Payment Plans CSV &nbsp;</a>
                K1124
                <a
                  class="btn btn-secondary"
                  :href="'/api/admin/processorders/orders.csv?' + searchParams">
                  Export CSV &nbsp;</a>
              </div>
              <!-- <div class="col mt-2">
                <a
                  class="btn btn-secondary"
                  :href="'/api/admin/orders/oic_export.csv?' + searchParams"
                >Export Offer In Compromise CSV &nbsp;</a>
              </div>-->
              <!-- <div class="col mt-2">
                <a
                  class="btn btn-secondary"
                  :href="'/api/admin/orders/taxLien_export.csv?' + searchParams"
                >Export Tax Liens CSV &nbsp;</a>
              </div>-->
            </div>
          </b-tab>
          <b-tab title="Partner Codes" v-if="loggedInUser.userType === 'admin'">
            <h1 class="text-center">Partner Codes</h1>
            <div v-if="!!partnerCodesError">
              <div class="alert alert-danger">{{ partnerCodesError }}</div>
            </div>
            <div v-if="!partnerCodesError">
              <b-row>
                <b-col md="6" class="my-1">
                  <b-form-group horizontal label="Filter" class="mb-0">
                    <b-input-group>
                      <b-form-input
                        v-model="partner_codes_table.filter"
                        placeholder="Type to Search"
                      />
                      <b-btn
                        :disabled="!partner_codes_table.filter"
                        @click="partner_codes_table.filter = ''"
                      >Clear
                      </b-btn
                      >
                    </b-input-group>
                  </b-form-group>
                </b-col>
                <b-col md="6" class="my-1">
                  <b-form-group horizontal label="Per page" class="mb-0">
                    <b-form-select
                      :options="partner_codes_table.pageOptions"
                      v-model="partner_codes_table.perPage"
                    />
                  </b-form-group>
                </b-col>
              </b-row>

              <!-- Main table element -->
              <b-table
                show-empty
                stacked="md"
                :items="partner_codes_table.items"
                :fields="partner_codes_table.fields"
                :current-page="partner_codes_table.currentPage"
                :per-page="partner_codes_table.perPage"
                :filter="partner_codes_table.filter"
                @filtered="onPartnerCodeFiltered"
              >
                <template slot="actions" slot-scope="row">
                  <b-button
                    size="sm"
                    @click.stop="editPartnerCode(row.item, row.index, $event.target)"
                    class="mr-1"
                  >Edit
                  </b-button
                  >
                  <b-button
                    size="sm"
                    @click.stop="deletePartnerCode(row.item, row.index, $event.target)"
                  >Delete
                  </b-button
                  >
                </template>
              </b-table>
              <b-row>
                <b-col md="6" class="my-1">
                  <b-pagination
                    :total-rows="partner_codes_table.totalRows"
                    :per-page="partner_codes_table.perPage"
                    v-model="partner_codes_table.currentPage"
                    class="my-0"
                  />
                </b-col>
              </b-row>
              <b-row>
                <b-col md="6">
                  <b-button size="sm" @click.stop="addPartnerCode()"
                  >Add Partner Code
                  </b-button
                  >
                  <b-button size="sm" @click.stop="usageReport()">Usage Report</b-button>
                </b-col>
              </b-row>
              <div v-if="!!partnerCodeOperationError">
                <div class="alert alert-danger">
                  {{ partnerCodeOperationError }}
                </div>
              </div>
            </div>

            <h1 class="text-center">Coupon Codes</h1>
            <div v-if="!!couponCodesError">
              <div class="alert alert-danger">{{ couponCodesError }}</div>
            </div>
            <div v-if="!couponCodesError">
              <b-row>
                <b-col md="6" class="my-1">
                  <b-form-group horizontal label="Filter" class="mb-0">
                    <b-input-group>
                      <b-form-input
                        v-model="coupon_codes_table.filter"
                        placeholder="Type to Search"
                      />
                      <b-btn
                        :disabled="!coupon_codes_table.filter"
                        @click="coupon_codes_table.filter = ''"
                      >Clear
                      </b-btn
                      >
                    </b-input-group>
                  </b-form-group>
                </b-col>
                <b-col md="6" class="my-1">
                  <b-form-group horizontal label="Per page" class="mb-0">
                    <b-form-select
                      :options="coupon_codes_table.pageOptions"
                      v-model="coupon_codes_table.perPage"
                    />
                  </b-form-group>
                </b-col>
              </b-row>
              <!-- Main table element -->
              <b-table
                show-empty
                stacked="md"
                :items="coupon_codes_table.items"
                :fields="coupon_codes_table.fields"
                :current-page="coupon_codes_table.currentPage"
                :per-page="coupon_codes_table.perPage"
                :filter="coupon_codes_table.filter"
                @filtered="onCouponCodeFiltered"
              >
                <template slot="actions" slot-scope="row">
                  <b-button
                    size="sm"
                    @click.stop="editCouponCode(row.item, row.index, $event.target)"
                    class="mr-1"
                  >Edit
                  </b-button
                  >
                  <b-button
                    size="sm"
                    @click.stop="deleteCouponCode(row.item, row.index, $event.target)"
                  >Delete
                  </b-button
                  >
                </template>
              </b-table>
              <b-row>
                <b-col md="6" class="my-1">
                  <b-pagination
                    :total-rows="coupon_codes_table.totalRows"
                    :per-page="coupon_codes_table.perPage"
                    v-model="coupon_codes_table.currentPage"
                    class="my-0"
                  />
                </b-col>
              </b-row>
              <b-row>
                <b-col md="6">
                  <b-button size="sm" @click.stop="addCouponCode()"
                  >Add Coupon Code
                  </b-button
                  >
                </b-col>
              </b-row>
              <div v-if="!!couponCodeOperationError">
                <div class="alert alert-danger">
                  {{ couponCodeOperationError }}
                </div>
              </div>
            </div>
          </b-tab>
          <b-tab title="Sales and Pricing" v-if="loggedInUser.userType === 'admin'">
            <PricingDashboard/>
            <SalesDashboard/>
          </b-tab>
          <b-tab title="Admin Controls" v-if="loggedInUser.userType === 'admin'">
            <AdminControls/>
          </b-tab>
        </b-tabs>
      </div>
    </div>

    <b-modal ref="confirmDeletion" title="Confirm Delete" hide-footer>
      <p class="my-4">Are you sure you would like to mark these orders as Deleted?</p>
      <b-btn class="mt-3" variant="outline-danger" block @click="deleteSelectedRows()"
      >Delete
      </b-btn
      >
    </b-modal>

    <b-modal
      ref="editPartnerCode"
      hide-footer
      :title="this.editedPartnerCode.update ? 'Edit Partner Code' : 'Add Partner Code'"
    >
      <div class="row mb-3">
        <div class="col-6">
          <span class="ml-1">Partner Name</span>
          <input
            type="text"
            :class="['form-control', errors.partnerName ? 'is-invalid' : '']"
            v-model="editedPartnerCode.partnerName"
          />
          <p>{{ errors.partnerName }}</p>
        </div>
        <div class="col-6">
          <span class="ml-1">Contact Name</span>
          <input
            type="text"
            :class="['form-control', errors.contactName ? 'is-invalid' : '']"
            v-model="editedPartnerCode.contactName"
          />
          <p>{{ errors.contactName }}</p>
        </div>
        <div class="col-6">
          <span class="ml-1">Commission</span>
          <input
            type="number"
            :class="['form-control', errors.commission ? 'is-invalid' : '']"
            v-model="editedPartnerCode.commission"
            min="0"
            max="100"
          />
          <p>{{ errors.commission }}</p>
        </div>
        <div class="col-6">
          <span class="ml-1">Commission Type</span>
          <div class="form-group">
            <select class="form-control" v-model="editedPartnerCode.commissionTypeId">
              <option value="1">Percentage</option>
              <option value="2">Flat</option>
            </select>
          </div>
        </div>
        <div class="col-6">
          <span class="ml-1">Code</span>
          <input
            type="text"
            :class="['form-control', errors.code ? 'is-invalid' : '']"
            placeholder="Code"
            v-model="editedPartnerCode.code"
            maxlength="8"
          />
          <p>{{ errors.code }}</p>
        </div>
        <div class="col-6">
          <span class="ml-1">Street Address</span>
          <input
            type="text"
            :class="['form-control', errors.streetAddress ? 'is-invalid' : '']"
            v-model="editedPartnerCode.streetAddress"
          />
          <p>{{ errors.streetAddress }}</p>
        </div>
        <div class="col-3">
          <span class="ml-1">City</span>
          <input
            type="text"
            :class="['form-control', errors.city ? 'is-invalid' : '']"
            v-model="editedPartnerCode.city"
          />
          <p>{{ errors.city }}</p>
        </div>
        <div class="col-3">
          <span class="ml-1">State</span>
          <input
            type="text"
            :class="['form-control', errors.state ? 'is-invalid' : '']"
            v-model="editedPartnerCode.state"
          />
          <p>{{ errors.state }}</p>
        </div>
        <div class="col-3">
          <span class="ml-1">Zip</span>
          <input
            type="text"
            :class="['form-control', errors.zip ? 'is-invalid' : '']"
            v-model="editedPartnerCode.zip"
          />
          <p>{{ errors.zip }}</p>
        </div>
        <div class="col-12">
          <span class="ml-1">Notes</span>
          <textarea
            type="text"
            rows="4"
            cols="50"
            :class="['form-control', errors.notes ? 'is-invalid' : '']"
            v-model="editedPartnerCode.notes"
          ></textarea>
          <p>{{ errors.notes }}</p>
        </div>
        <div class="col-6">
          <span class="ml-1">Discount</span>
          <input
            type="number"
            :class="['form-control', errors.percentageOff ? 'is-invalid' : '']"
            placeholder="Percentage Off"
            v-model="editedPartnerCode.percentageOff"
            min="0"
            max="100"
          />
          <p>{{ errors.percentageOff }}</p>
        </div>
      </div>
      <div class="mb-3">
        <div class="col">
          <button class="btn btn-primary" @click="savePartnerCode()">Submit</button>
          <button class="btn btb-primary" @click="hideEditPartnerCodeModal()">
            Close
          </button>
        </div>
      </div>
    </b-modal>

    <b-modal
      ref="editCouponCode"
      hide-footer
      :title="this.editedCouponCode.update ? 'Edit Coupon Code' : 'Add Coupon Code'"
    >
      <div class="row mb-3">
        <div class="col-6">
          <span class="ml-1">Partner Name</span>
          <input
            type="text"
            :class="['form-control', errors.partnerName ? 'is-invalid' : '']"
            v-model="editedCouponCode.partnerName"
          />
          <p>{{ errors.partnerName }}</p>
        </div>
        <div class="col-6">
          <span class="ml-1">Contact Name</span>
          <input
            type="text"
            :class="['form-control', errors.contactName ? 'is-invalid' : '']"
            v-model="editedCouponCode.contactName"
          />
          <p>{{ errors.contactName }}</p>
        </div>
        <div class="col-6">
          <span class="ml-1">Commission</span>
          <input
            type="number"
            :class="['form-control', errors.commission ? 'is-invalid' : '']"
            v-model="editedCouponCode.commission"
            min="0"
            max="100"
          />
          <p>{{ errors.commission }}</p>
        </div>
        <div class="col-6">
          <span class="ml-1">Commission Type</span>
          <div class="form-group">
            <select class="form-control" v-model="editedCouponCode.commissionTypeId">
              <option value="1">Percentage</option>
              <option value="2">Flat</option>
            </select>
          </div>
        </div>
        <div class="col-6">
          <span class="ml-1">Code</span>
          <input
            type="text"
            :class="['form-control', errors.code ? 'is-invalid' : '']"
            placeholder="Code"
            v-model="editedCouponCode.code"
            maxlength="8"
          />
          <p>{{ errors.code }}</p>
        </div>
        <div class="col-6">
          <span class="ml-1">Street Address</span>
          <input
            type="text"
            :class="['form-control', errors.streetAddress ? 'is-invalid' : '']"
            v-model="editedCouponCode.streetAddress"
          />
          <p>{{ errors.streetAddress }}</p>
        </div>
        <div class="col-3">
          <span class="ml-1">City</span>
          <input
            type="text"
            :class="['form-control', errors.city ? 'is-invalid' : '']"
            v-model="editedCouponCode.city"
          />
          <p>{{ errors.city }}</p>
        </div>
        <div class="col-3">
          <span class="ml-1">State</span>
          <input
            type="text"
            :class="['form-control', errors.state ? 'is-invalid' : '']"
            v-model="editedCouponCode.state"
          />
          <p>{{ errors.state }}</p>
        </div>
        <div class="col-3">
          <span class="ml-1">Zip</span>
          <input
            type="text"
            :class="['form-control', errors.zip ? 'is-invalid' : '']"
            v-model="editedCouponCode.zip"
          />
          <p>{{ errors.zip }}</p>
        </div>
        <div class="col-12">
          <span class="ml-1">Notes</span>
          <textarea
            type="text"
            rows="4"
            cols="50"
            :class="['form-control', errors.notes ? 'is-invalid' : '']"
            v-model="editedCouponCode.notes"
          ></textarea>
          <p>{{ errors.notes }}</p>
        </div>
        <div class="col-6">
          <span class="ml-1">Discount</span>
          <input
            type="number"
            :class="['form-control', errors.percentageOff ? 'is-invalid' : '']"
            placeholder="Percentage Off"
            v-model="editedCouponCode.percentageOff"
            min="0"
            max="100"
          />
          <p>{{ errors.percentageOff }}</p>
        </div>
      </div>
      <div class="mb-3">
        <div class="col">
          <button class="btn btn-primary" @click="saveCouponCode()">Submit</button>
          <button class="btn btb-primary" @click="hideEditCouponCodeModal()">
            Close
          </button>
        </div>
      </div>
    </b-modal>
    <b-modal ref="usageReportModal" hide-footer title="Usage Report">
      <div class="form-group">
        <input
          placeholder="Select Date"
          class="form-control"
          id="usageReportDate"
          :value="formatDates(usageReportDate.start, usageReportDate.end)"
        />
        <span class="mt-1" id="cleardates" @click="clearDates()">X</span>
        <AirbnbStyleDatepicker
          style="position: absolute; width: 350px; left: 15px"
          :showShortcutsMenuTrigger="false"
          :trigger-element-id="'usageReportDate'"
          :monthsToShow="1"
          :mode="'range'"
          :fullscreen-mobile="false"
          :date-one="usageReportDate.start"
          :date-two="usageReportDate.end"
          @date-one-selected="
            (val) => {
              usageReportDate.start = val;
            }
          "
          @date-two-selected="
            (val) => {
              usageReportDate.end = val;
            }
          "
        />
      </div>
      <div class="mb-3">
        <div class="col">
          <button class="btn btn-primary" @click="submitUsageReport()">Submit</button>
          <button class="btn btb-primary" @click="hideUsageReportModal()">Close</button>
        </div>
      </div>
    </b-modal>
  </page>
</template>

<!-- sharea sale tag -->
<script>
var shareasaleSSCID = shareasaleGetParameterByName("sscid");

function shareasaleSetCookie(e, a, r, s, t) {
  if (e && a) {
    var o,
      n = s ? "; path=" + s : "",
      i = t ? "; domain=" + t : "",
      l = "";
    r &&
    ((o = new Date()).setTime(o.getTime() + r), (l = "; expires=" + o.toUTCString())),
      (document.cookie = e + "=" + a + l + n + i);
  }
}

function shareasaleGetParameterByName(e, a) {
  a || (a = window.location.href), (e = e.replace(/[\[\]]/g, "\\$&"));
  var r = new RegExp("[?&]" + e + "(=([^&#]*)|&|#|$)").exec(a);
  return r ? (r[2] ? decodeURIComponent(r[2].replace(/\+/g, " ")) : "") : null;
}

shareasaleSSCID &&
shareasaleSetCookie("shareasaleSSCID", shareasaleSSCID, 94670778e4, "/");
</script>

<script>
import {AXIOS} from "../scripts/http-common";
import moment from "moment-timezone";
import querystring from "querystring";
import Datepicker from "vuejs-datepicker";
import format from "date-fns/format";
import omit from "lodash/omit";
import Axios from "axios";
import _ from "lodash";
import PricingDashboard from "./PricingDashboard.vue";
import SalesDashboard from "./SalesDashboard.vue";
import AdminControls from "./AdminControls.vue";
import PenaltyOrderHistory from "./PenaltyOrderSearch.vue";

export default {
  components: {
    Datepicker,
    PricingDashboard,
    SalesDashboard,
    AdminControls,
    PenaltyOrderHistory,
  },
  data() {
    return {
      isAutomatedScheduleLoading: false,
      isAutomatedSchedule: false,
      automatedScheduleTime: "08:00:00",
      excludedHolidays: [],
      excludedHoliday: null,
      times: [
        {label: "12:00 AM", value: "00:00:00"},
        {label: "12:30 AM", value: "00:30:00"},
        {label: "1:00 AM", value: "01:00:00"},
        {label: "1:30 AM", value: "01:30:00"},
        {label: "2:00 AM", value: "02:00:00"},
        {label: "2:30 AM", value: "02:30:00"},
        {label: "3:00 AM", value: "03:00:00"},
        {label: "3:30 AM", value: "03:30:00"},
        {label: "4:00 AM", value: "04:00:00"},
        {label: "4:30 AM", value: "04:30:00"},
        {label: "5:00 AM", value: "05:00:00"},
        {label: "5:30 AM", value: "05:30:00"},
        {label: "6:00 AM", value: "06:00:00"},
        {label: "6:30 AM", value: "06:30:00"},
        {label: "7:00 AM", value: "07:00:00"},
        {label: "7:30 AM", value: "07:30:00"},
        {label: "8:00 AM", value: "08:00:00"},
        {label: "8:30 AM", value: "08:30:00"},
        {label: "9:00 AM", value: "09:00:00"},
        {label: "9:30 AM", value: "09:30:00"},
        {label: "10:00 AM", value: "10:00:00"},
        {label: "10:30 AM", value: "10:30:00"},
        {label: "11:00 AM", value: "11:00:00"},
        {label: "11:30 AM", value: "11:30:00"},
        {label: "12:00 PM", value: "12:00:00"},
        {label: "12:30 PM", value: "12:30:00"},
        {label: "1:00 PM", value: "13:00:00"},
        {label: "1:30 PM", value: "13:30:00"},
        {label: "2:00 PM", value: "14:00:00"},
        {label: "2:30 PM", value: "14:30:00"},
        {label: "3:00 PM", value: "15:00:00"},
        {label: "3:30 PM", value: "15:30:00"},
        {label: "4:00 PM", value: "16:00:00"},
        {label: "4:30 PM", value: "16:30:00"},
        {label: "5:00 PM", value: "17:00:00"},
        {label: "5:30 PM", value: "17:30:00"},
        {label: "6:00 PM", value: "18:00:00"},
        {label: "6:30 PM", value: "18:30:00"},
        {label: "7:00 PM", value: "19:00:00"},
        {label: "7:30 PM", value: "19:30:00"},
        {label: "8:00 PM", value: "20:00:00"},
        {label: "8:30 PM", value: "20:30:00"},
        {label: "9:00 PM", value: "21:00:00"},
        {label: "9:30 PM", value: "21:30:00"},
        {label: "10:00 PM", value: "22:00:00"},
        {label: "10:30 PM", value: "22:30:00"},
        {label: "11:00 PM", value: "23:00:00"},
        {label: "11:30 PM", value: "23:30:00"},
      ],
      partner_codes_table: {
        filter: null,
        perPage: 5,
        pageOptions: [5, 10, 15],
        items: [],
        fields: [
          {
            key: "partnerName",
            label: "Business Name",
            sortable: true,
            class: "text-center",
          },
          {
            key: "contactName",
            label: "Contact",
            sortable: true,
            class: "text-center",
          },
          {
            key: "commission",
            label: "Partner Commission",
            sortable: true,
            class: "text-center",
          },
          {key: "code", label: "Code", sortable: true, class: "text-center"},
          {
            key: "percentageOff",
            label: "Percentage Off",
            sortable: true,
            class: "text-center",
          },
          {key: "actions", label: "Actions"},
        ],
        totalRows: 1,
        currentPage: 1,
      },
      partnerCodesError: null,
      partnerCodeOperationError: null,
      coupon_codes_table: {
        filter: null,
        perPage: 5,
        pageOptions: [5, 10, 15],
        items: [],
        fields: [
          {key: "code", label: "Code", sortable: true, class: "text-center"},
          {
            key: "percentageOff",
            label: "Percentage Off",
            sortable: true,
            class: "text-center",
          },
          {key: "actions", label: "Actions"},
        ],
        totalRows: 1,
        currentPage: 1,
      },
      couponCodeOperationError: null,
      couponCodesError: null,
      editedPartnerCode: {
        codeNum: 0,
        code: "",
        percentageOff: 0,
        partnerName: "",
        contactName: "",
        commission: "",
        commissionTypeId: 1,
        streetAddress: "",
        city: "",
        state: "",
        zip: "",
        notes: "",
        update: false,
      },
      editedCouponCode: {
        codeNum: 0,
        code: "",
        percentageOff: 0,
        update: false,
      },
      usageReportDate: {
        start: "",
        end: "",
      },
      errors: {
        code: null,
        percentageOff: null,
        partnerName: null,
        contactName: null,
        commission: null,
        commissionTypeId: null,
        streetAddress: null,
        city: null,
        state: null,
        zip: null,
        notes: null,
      },
      file: "",
      fileUploadResult: [],
      request: {
        pageNumber: 0,
        pageSize: 20,
        orderBy: "createdDate",
        orderDirection: "DESC",
        query: {
          firstName: "",
          lastName: "",
          email: "",
          phone: "",
          product: "",
          isCalifornia: false,
          isNewJersey: false,
          isGeorgia: false,
          isIllinois: false,
          isMichigan: false,
          status: {
            any: false,
            incomplete: false,
            cancelled: false,
            processing: true,
            onHold: true,
            failed: false,
            chargeback: false,
            completed: false,
            awaitingSignatureService: false,
            signed: false,
            botError: true,
          },
          orderId: "",
          dateRange: "THIRTY_DAYS",
          // createdAt: ""
          createdAt: {
            start: "",
            end: "",
          },
        },
      },
      searchResult: {
        numResults: 0,
        resultsInPage: 0,
        rows: [],
      },
      allProcessedOrders: [],
      processOrderWarning: "",
      pages: [
        {
          pageNumber: 0,
          current: true,
        },
      ],
      loggedInUser: "",
      dateFormat: "MM/DD/YYYY",
      loaderOn: false,
    };
  },
  computed: {
    searchParams() {
      return querystring.encode(this.toParams(this.request.query));
    },
  },
  methods: {
    addExcludedHoliday(holiday) {
      this.excludedHoliday = holiday;
      if (holiday && this.excludedHolidays.findIndex((x) => x == holiday) == -1) {
        this.excludedHolidays.push(holiday);
        this.onAutomatedScheduleChange();
      }
    },
    removeExcludedHoliday(index) {
      const excludedItems = [...this.excludedHolidays];
      excludedItems.splice(index, 1);
      this.excludedHolidays = excludedItems;
      this.onAutomatedScheduleChange();
    },
    clearExcludedHolidays() {
      this.excludedHolidays = [];
      this.onAutomatedScheduleChange();
    },
    onAutomatedScheduleChange() {
      const excludedHolidays = this.excludedHolidays.join(",");
      // const dateStr = moment()
      // .subtract(1, "days")
      //.format("YYYY-MM-DD");
      const dateStr = moment().format("YYYY-MM-DD");
      const scheduledDateTimeStr = `${dateStr} ${this.automatedScheduleTime}`;
      const scheduledDateTime = moment.tz(scheduledDateTimeStr, "America/New_York");
      this.isAutomatedScheduleLoading = true;
      AXIOS.put(`/v2/admin/processordersschedule/current`, {
        id: 1,
        isAutomatic: this.isAutomatedSchedule,
        automatedTime: scheduledDateTime.toDate(),
        excludedHolidays,
      })
        .then((res) => {
          console.log("---Schedule Automation settings saved successfully!!!---");
          this.isAutomatedScheduleLoading = false;
        })
        .catch((err) => {
          console.log(err);
          this.isAutomatedScheduleLoading = false;
        });
    },
    currentDate() {
      return `${moment().format("YYYY-MM-DD")}`;
    },
    timetoEst(datestring) {
      const yymmdd = moment(datestring).format("YYYY-MM-DD");

      if (yymmdd < "2023-04-26") {
        return moment
          .utc(datestring)
          .tz("America/New_York")
          .format("M/D/YYYY hh:mm:ss A");
      } else {
        return datestring;
      }
    },
    getAutomatedSchedule() {
      this.isAutomatedScheduleLoading = true;
      AXIOS.get(`/admin/processordersschedule/current`)
        .then((res) => {
          const {isAutomatic, automatedTime, excludedHolidays} = res.data;
          this.isAutomatedSchedule = isAutomatic;
          this.automatedScheduleTime = moment(automatedTime)
            .tz("America/New_York")
            .format("HH:mm:ss");
          this.excludedHolidays = excludedHolidays.split(",");
          const today = new Date().setHours(0, 0, 0, 0);
          const futureHolidays = this.excludedHolidays.filter((value) => {
            return new Date(value).setHours(23, 59, 59, 999) >= today;
          });
          if (
            this.excludedHolidays &&
            futureHolidays.length !== this.excludedHolidays.length
          ) {
            this.excludedHolidays = futureHolidays;
            this.onAutomatedScheduleChange();
          }
          this.isAutomatedScheduleLoading = false;
        })
        .catch((err) => {
          console.log(err);
          this.isAutomatedScheduleLoading = false;
        });
    },
    deleteProcessingOrder() {
      this.loaderOn = true;
      let deleteProcessingOrderPromises = [];
      const versionIds = _.join(_.map(this.allProcessedOrders, "versionId"), ",");

      AXIOS.delete(`/processedorders/deleteProcessedOrder/${versionIds}`)
        .then((res) => {
          this.loaderOn = false;
          alert("Deleted Successfully");
          this.getAllProcessedOrders();
        })
        .catch((err) => {
          this.loaderOn = false;
          alert("Error" + err);
        });
    },
    moveToMailRoom() {
      this.loaderOn = true;
      let moveToMaiolroomPromises = [];
      const versionIds = _.join(_.map(this.allProcessedOrders, "versionId"), ",");
      AXIOS.post(`/admin/uploadingmailroom/${versionIds}`)
        .then((res) => {
          this.loaderOn = false;
          alert("Moved to MailRoom Successfully");
          this.getAllProcessedOrders();
        })
        .catch((er) => {
          this.loaderOn = false;
          alert("Error" + err);
        });
    },
    downloadProcessedPdf(order) {
      window.location = "/api/processedorders/downloadpdf/" + order.versionId;
    },
    downloadProcessedLabelsFile(order) {
      window.location = "/api/processedorders/downloadLabelPdf/" + order.versionId;
    },
    getAllProcessedOrders() {
      AXIOS.get("/processedorders/allprocessedorders").then((res) => {
        this.allProcessedOrders = [...res.data.processedOrders];
        this.processOrderWarning = res.data.warningMessage;
        // this.allProcessedOrders = [...res.data];
        // console.log(res)
        // console.log("saved");
      });
    },
    processOrders() {
      // let self=this;
      this.loaderOn = true;
      AXIOS.get("/v2/admin/processorders")
        .then((res) => {
          this.loaderOn = false;
          this.allProcessedOrders = [...res.data.processedOrders];
          this.processOrderWarning = res.data.warningMessage;
        })
        .catch((err) => {
          this.loaderOn = false;
          if (err.response && err.response.status == 404) {
            alert("There is not any processing order for mail room");
          } else {
            alert("error" + err);
          }
        });
    },
    usageReport() {
      this.$refs.usageReportModal.show();
    },
    hideUsageReportModal() {
      this.$refs.usageReportModal.hide();
    },
    submitUsageReport() {
      // AXIOS.get("/admin/partnercodes/usagereport")
      //   .then(res => {
      //     console.log(res.data);
      //   })
      //   .catch(err => {
      //     this.loaderOn = false;
      //     if (err.response && err.response.status == 404) {
      //       alert("There is not any processing order for mail room");
      //     } else {
      //       alert("error" + err);
      //     }
      //   });

      if (this.usageReportDate.start == "" || this.usageReportDate.end == "") {
        alert("Please select the Date first!");
      } else {
        window.location.href =
          "/api/admin/partnercodes/usagereport.xlsx?startAt=" +
          this.usageReportDate.start +
          "&endAt=" +
          this.usageReportDate.end;
      }
    },
    editPartnerCode(item, index, target) {
      this.$refs.editPartnerCode.show();
      this.editedPartnerCode.codeNum = item.codeNum;
      this.editedPartnerCode.code = item.code;
      this.editedPartnerCode.percentageOff = item.percentageOff;
      this.editedPartnerCode.partnerName = item.partnerName;
      this.editedPartnerCode.contactName = item.contactName;
      this.editedPartnerCode.commission = item.commission;
      this.editedPartnerCode.streetAddress = item.streetAddress;
      this.editedPartnerCode.city = item.city;
      this.editedPartnerCode.state = item.state;
      this.editedPartnerCode.zip = item.zip;
      this.editedPartnerCode.notes = item.notes;
      this.editedPartnerCode.commissionTypeId = item.commissionTypeId;
      this.editedPartnerCode.update = true;
    },
    addPartnerCode() {
      this.$refs.editPartnerCode.show();
      this.editedPartnerCode.codeNum = 0;
      this.editedPartnerCode.code = "";
      this.editedPartnerCode.percentageOff = 0;
      this.editedPartnerCode.partnerName = "";
      this.editedPartnerCode.contactName = "";
      this.editedPartnerCode.commission = 0;
      this.editedPartnerCode.streetAddress = "";
      this.editedPartnerCode.city = "";
      this.editedPartnerCode.state = "";
      this.editedPartnerCode.zip = "";
      this.editedPartnerCode.notes = "";
      this.editedPartnerCode.commissionTypeId = 1;
      this.editedPartnerCode.update = false;
    },
    deletePartnerCode(item, index, target) {
      AXIOS.delete(`/admin/partnercodes/${item.codeNum}`)
        .then(() => {
          this.partner_codes_table.items = this.partner_codes_table.items.filter(
            (i) => i.codeNum !== item.codeNum
          );
          this.partnerCodeOperationError = null;
        })
        .catch((e) => {
          this.partnerCodeOperationError = e.message;
        });
    },
    hideEditPartnerCodeModal() {
      this.$refs.editPartnerCode.hide();
    },
    savePartnerCode() {
      let hasErrors = false;
      let required = ["code", "percentageOff"];
      required.forEach((p) => {
        if (
          !this.editedPartnerCode[p]
          // this.editedPartnerCode[p].trim().length === 0
        ) {
          this.errors[p] = "This field is required";
          hasErrors = true;
        } else {
          this.errors[p] = null;
        }
      });

      if (hasErrors) {
        return;
      }

      const percentageOffInput = this.editedPartnerCode.percentageOff;
      const percentageOffValue = parseFloat(percentageOffInput);

      if (isNaN(percentageOffInput)) {
        hasErrors = true;
        this.errors.percentageOff = "This field should be a number";
      } else if (percentageOffValue < 0 || percentageOffValue > 100) {
        hasErrors = true;
        this.errors.percentageOff = "This value should be between 0 and 100";
      }

      if (hasErrors) {
        return;
      }

      this.hideEditPartnerCodeModal();

      const {update: updateMode, codeNum} = this.editedPartnerCode;
      const payload = omit(this.editedPartnerCode, ["codeNum", "update"]);
      const requestPromise = updateMode
        ? AXIOS.put(`/admin/partnercodes/${codeNum}`, payload)
        : AXIOS.post(`/admin/partnercodes`, payload);

      requestPromise
        .then((response) => {
          const foundIndex = this.partner_codes_table.items.findIndex(
            (i) => i.codeNum === response.data.codeNum
          );
          if (foundIndex > -1) {
            this.partner_codes_table.items[foundIndex].code = response.data.code;
            this.partner_codes_table.items[foundIndex].percentageOff =
              response.data.percentageOff;
            this.partner_codes_table.items[foundIndex].partnerName =
              response.data.partnerName;
            this.partner_codes_table.items[foundIndex].contactName =
              response.data.contactName;
            this.partner_codes_table.items[foundIndex].commission =
              response.data.commission;
            this.partner_codes_table.items[foundIndex].commissionTypeId =
              response.data.commissionTypeId;
            this.partner_codes_table.items[foundIndex].streetAddress =
              response.data.streetAddress;
            this.partner_codes_table.items[foundIndex].city = response.data.city;
            this.partner_codes_table.items[foundIndex].state = response.data.state;
            this.partner_codes_table.items[foundIndex].zip = response.data.zip;
            this.partner_codes_table.items[foundIndex].notes = response.data.notes;
          } else {
            this.partner_codes_table.items.push({
              ...response.data,
            });
          }
          this.partnerCodeOperationError = null;
        })
        .catch((e) => {
          this.partnerCodeOperationError = e.message;
        });
    },
    editCouponCode(item, index, target) {
      this.$refs.editCouponCode.show();
      this.editedCouponCode.codeNum = item.codeNum;
      this.editedCouponCode.code = item.code;
      this.editedCouponCode.percentageOff = item.percentageOff;
      this.editedCouponCode.partnerName = item.partnerName;
      this.editedCouponCode.contactName = item.contactName;
      this.editedCouponCode.commission = item.commission;
      this.editedCouponCode.streetAddress = item.streetAddress;
      this.editedCouponCode.city = item.city;
      this.editedCouponCode.state = item.state;
      this.editedCouponCode.zip = item.zip;
      this.editedCouponCode.notes = item.notes;
      this.editedCouponCode.commissionTypeId = item.commissionTypeId;
      this.editedCouponCode.update = true;
    },
    addCouponCode() {
      this.$refs.editCouponCode.show();
      this.editedCouponCode.codeNum = 0;
      this.editedCouponCode.code = "";
      this.editedCouponCode.percentageOff = 0;
      this.editedCouponCode.partnerName = "";
      this.editedCouponCode.contactName = "";
      this.editedCouponCode.commission = 0;
      this.editedCouponCode.streetAddress = "";
      this.editedCouponCode.city = "";
      this.editedCouponCode.state = "";
      this.editedCouponCode.zip = "";
      this.editedCouponCode.notes = "";
      this.editedCouponCode.commissionTypeId = 1;
      this.editedCouponCode.update = false;
    },
    deleteCouponCode(item, index, target) {
      AXIOS.delete(`/admin/couponcodes/${item.codeNum}`)
        .then(() => {
          this.coupon_codes_table.items = this.coupon_codes_table.items.filter(
            (i) => i.codeNum !== item.codeNum
          );
          this.couponCodeOperationError = null;
        })
        .catch((e) => {
          this.couponCodeOperationError = e.message;
        });
    },
    hideEditCouponCodeModal() {
      this.$refs.editCouponCode.hide();
    },
    saveCouponCode() {
      let hasErrors = false;
      let required = ["code", "percentageOff"];
      required.forEach((p) => {
        if (
          !this.editedCouponCode[p]
          // || this.editedCouponCode[p].trim().length === 0
        ) {
          this.errors[p] = "This field is required";
          hasErrors = true;
        } else {
          this.errors[p] = null;
        }
      });

      if (hasErrors) {
        return;
      }

      const percentageOffInput = this.editedCouponCode.percentageOff;
      const percentageOffValue = parseFloat(percentageOffInput);

      if (isNaN(percentageOffInput)) {
        hasErrors = true;
        this.errors.percentageOff = "This field should be a number";
      } else if (percentageOffValue < 0 || percentageOffValue > 100) {
        hasErrors = true;
        this.errors.percentageOff = "This value should be between 0 and 100";
      }

      if (hasErrors) {
        return;
      }

      this.hideEditCouponCodeModal();

      const {update: updateMode, codeNum} = this.editedCouponCode;
      const payload = omit(this.editedCouponCode, ["codeNum", "update"]);
      const requestPromise = updateMode
        ? AXIOS.put(`/admin/couponcodes/${codeNum}`, payload)
        : AXIOS.post(`/admin/couponcodes`, payload);
      requestPromise
        .then((response) => {
          const foundIndex = this.coupon_codes_table.items.findIndex(
            (i) => i.codeNum === response.data.codeNum
          );
          if (foundIndex > -1) {
            this.coupon_codes_table.items[foundIndex].code = response.data.code;
            this.coupon_codes_table.items[foundIndex].percentageOff =
              response.data.percentageOff;
            this.coupon_codes_table.items[foundIndex].partnerName =
              response.data.partnerName;
            this.coupon_codes_table.items[foundIndex].contactName =
              response.data.contactName;
            this.coupon_codes_table.items[foundIndex].commission =
              response.data.commission;
            this.coupon_codes_table.items[foundIndex].commissionTypeId =
              response.data.commissionTypeId;
            this.coupon_codes_table.items[foundIndex].streetAddress =
              response.data.streetAddress;
            this.coupon_codes_table.items[foundIndex].city = response.data.city;
            this.coupon_codes_table.items[foundIndex].state = response.data.state;
            this.coupon_codes_table.items[foundIndex].zip = response.data.zip;
            this.coupon_codes_table.items[foundIndex].notes = response.data.notes;
          } else {
            this.coupon_codes_table.items.push({
              ...response.data,
            });
          }
          this.couponCodeOperationError = null;
        })
        .catch((e) => {
          this.couponCodeOperationError = e.message;
        });
    },
    handleStatusChange() {
      this.request.query.status.any = false;
    },
    handleAnyStatusChange() {
      if (this.request.query.status.any) {
        this.request.query.status.incomplete = false;
        this.request.query.status.cancelled = false;
        this.request.query.status.processing = false;
        this.request.query.status.awaitingSignatureService = false;
        this.request.query.status.signed = false;
        this.request.query.status.onHold = false;
        this.request.query.status.failed = false;
        this.request.query.status.botError = false;
        this.request.query.status.completed = false;
        this.request.query.status.chargeback = false;
        this.request.query.status.deleted = false;
      }
    },
    resetSearch() {
      this.request.pageNumber = 0;
      this.$store.dispatch("setSearch", this.request);
      this.doSearch();
    },
    gotoAddUser() {
      this.$router.push("/admin/register");
      // const data = this.allProcessedOrders
    },
    toParams(request, params = {}, prefix = null) {
      for (let [key, value] of Object.entries(request)) {
        let newKey = prefix ? `${prefix}.${key}` : key;
        if (value !== null && typeof value === "object") {
          this.toParams(value, params, newKey);
        } else {
          params[newKey] = value;
        }
      }
      return params;
    },
    formatDates(dateOne, dateTwo) {
      let formattedDates = "";
      if (dateOne) {
        formattedDates = format(dateOne, this.dateFormat);
      }
      if (dateTwo) {
        formattedDates += " - " + format(dateTwo, this.dateFormat);
      }
      return formattedDates;
    },
    formateDate(value) {
      if (value) {
        return moment(String(value)).format("MM/DD/YYYY");
      }
    },
    handleDataRangeChange() {
      if (this.request.query.dateRange !== "CUSTOM") {
        this.request.query.createdAt.start = "";
        this.request.query.createdAt.end = "";
      }
    },
    clearDates() {
      (this.request.query.createdAt.start = ""), (this.request.query.createdAt.end = "");
    },
    gotoDetails(id, order_type) {
      if (order_type == "ORDER") this.$router.push(`/admin/order/${id}`);
      else if (order_type == "PENALTY") this.$router.push(`/admin/penalty-order/${id}`);
      else if (order_type == "EIN") this.$router.push(`/admin/ein/${id}`);
    },
    doSearch() {
      let requestToSearch = _.cloneDeep(this.request);
      const isCalifornia = this.request.query.product === "CaliforniaPayPlan";
      const isNewJersey = this.request.query.product === "NewJerseyPayPlan";
      const isGeorgia = this.request.query.product === "GeorgiaPayPlan";
      const isIllinois = this.request.query.product === "IllinoisPayPlan";
      const isMichigan = this.request.query.product === "MichiganPayPlan";
      requestToSearch.query.firstName = requestToSearch.query.firstName.trim();
      requestToSearch.query.lastName = requestToSearch.query.lastName.trim();
      requestToSearch.query.email = requestToSearch.query.email.trim();
      requestToSearch.query.phone = requestToSearch.query.phone.trim();
      requestToSearch.query.isCalifornia = isCalifornia;
      requestToSearch.query.isNewJersey = isNewJersey;
      requestToSearch.query.isGeorgia = isGeorgia;
      requestToSearch.query.isMichigan = isMichigan;
      requestToSearch.query.isIllinois = isIllinois;
      requestToSearch.query.product =
        (isCalifornia || isNewJersey || isGeorgia || isIllinois || isMichigan)
          ? "PaymentPlan"
          : this.request.query.product;
      requestToSearch.query.orderId = requestToSearch.query.orderId.trim();

      let params = Object.assign({}, this.toParams(requestToSearch));

      if (this.request.query.dateRange == "TODAY") {
        this.request.query.createdAt = {
          start: moment().format("YYYY-MM-DD"),
          end: moment().format("YYYY-MM-DD"),
        };
      } else if (this.request.query.dateRange == "YESTERDAY") {
        this.request.query.createdAt = {
          start: moment().subtract(1, "days").format("YYYY-MM-DD"),
          end: moment().subtract(1, "days").format("YYYY-MM-DD"),
        };
      } else if (this.request.query.dateRange == "SEVEN_DAYS") {
        this.request.query.createdAt = {
          start: moment().subtract(7, "days").format("YYYY-MM-DD"),
          end: moment().format("YYYY-MM-DD"),
        };
      } else if (this.request.query.dateRange == "THIRTY_DAYS") {
        this.request.query.createdAt = {
          start: moment().subtract(30, "days").format("YYYY-MM-DD"),
          end: moment().format("YYYY-MM-DD"),
        };
      }

      // Convert time to local timezone
      if (this.request.query.createdAt.start && this.request.query.createdAt.end) {
        var timedifference = new Date().getTimezoneOffset() / 60;
        let startAt = this.request.query.createdAt.start + " 00:00:00";
        let endAt = this.request.query.createdAt.end + " 23:59:59";

        params["query.createdAt.start"] = moment(startAt)
          .add(timedifference, "hours")
          .format("YYYY-MM-DD HH:mm:ss");
        params["query.createdAt.end"] = moment(endAt)
          .add(timedifference, "hours")
          .format("YYYY-MM-DD HH:mm:ss");
        params["query.createdAt"] =
          "{'start':'" +
          params["query.createdAt.start"] +
          "','end':'" +
          params["query.createdAt.end"] +
          "'}";
      } else {
        params["query.createdAt"] = JSON.stringify(this.request.query.createdAt);
      }
      console.log(params);
      AXIOS.get(`/admin/orders`, {params})
        .then((response) => {
          this.$set(this, "searchResult", response.data);
          for (let row of this.searchResult.rows) {
            row.checked = false;
          }
          let numPages = this.searchResult.numResults / this.searchResult.pageSize;
          this.pages = [];
          for (let i = 0; i < numPages; i++) {
            this.pages.push({
              pageNumber: i,
              current: i === this.searchResult.pageNumber,
            });
          }
          this.$store.dispatch("setResult", this.searchResult);
        })
        .catch((e) => {
          this.$router.push("/login");
        });
    },
    handleFileUpload() {
      this.file = this.$refs.file.files[0];
    },
    submitFile() {
      let self = this;
      let formData = new FormData();
      formData.append("file", this.file);
      AXIOS.post("/admin/orders/tracking_upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })
        .then(function (response) {
          console.log("SUCCESS!!");
          self.fileUploadResult = response.data;
        })
        .catch(function () {
          alert("An error has occurred. Please double check the CSV file.");
        });
    },
    loadPage(page) {
      this.request.pageNumber = page.pageNumber;
      this.doSearch();
    },
    deleteButtonConfirmation() {
      this.$refs.confirmDeletion.show();
    },
    deleteSelectedRows() {
      this.$refs.confirmDeletion.hide();
      let orderNums = [];
      for (let row of this.searchResult.rows) {
        if (row.checked) {
          orderNums.push(row.id);
        }
      }
      AXIOS.post("/admin/orders/delete", {
        ids: orderNums,
      })
        .then(() => {
          this.doSearch();
        })
        .catch(() => {
          alert("Deletion failed!");
        });
    },
    onPartnerCodeFiltered(filteredItems) {
      this.partner_codes_table.totalRows = filteredItems.length;
      this.partner_codes_table.currentPage = 1;
    },
    onCouponCodeFiltered(filteredItems) {
      this.coupon_codes_table.totalRows = filteredItems.length;
      this.coupon_codes_table.currentPage = 1;
    },
    getPartnerCodes() {
      AXIOS.get(`/admin/partnercodes`)
        .then((response) => {
          console.log(response.data);
          response.data.forEach((p) => {
            this.partner_codes_table.items.push({
              codeNum: p.codeNum,
              code: p.code,
              percentageOff: p.percentageOff,
              contactName: p.contactName,
              partnerName: p.partnerName,
              commission: p.commission,
              commissionTypeId: p.commissionTypeId,
              streetAddress: p.streetAddress,
              city: p.city,
              state: p.state,
              zip: p.zip,
              notes: p.notes,
            });
          });
          this.partnerCodesError = null;
        })
        .catch((e) => {
          this.partnerCodesError = e.message;
        });
    },
    getCouponCodes() {
      AXIOS.get(`/admin/couponcodes`)
        .then((response) => {
          console.log(response.data);
          response.data.forEach((p) => {
            this.coupon_codes_table.items.push({
              codeNum: p.codeNum,
              code: p.code,
              percentageOff: p.percentageOff,
              contactName: p.contactName,
              partnerName: p.partnerName,
              commission: p.commission,
              commissionTypeId: p.commissionTypeId,
              streetAddress: p.streetAddress,
              city: p.city,
              state: p.state,
              zip: p.zip,
              notes: p.notes,
            });
          });
          this.couponCodesError = null;
        })
        .catch((e) => {
          this.couponCodesError = e.message;
        });
    },
  },
  mounted() {
    let userInStorage = JSON.parse(localStorage.getItem("loggedUser"));
    this.loggedInUser =
      this.$store.getters.getUser != ""
        ? this.$store.getters.getUser
        : userInStorage
          ? userInStorage
          : "";

    this.getPartnerCodes();
    this.getCouponCodes();
    this.getAllProcessedOrders();
    this.getAutomatedSchedule();
    this.request.pageNumber = this.$store.getters.getSearchValues.pageNumber
      ? this.$store.getters.getSearchValues.pageNumber
      : 0;
    this.request.pageSize = this.$store.getters.getSearchValues.pageSize
      ? this.$store.getters.getSearchValues.pageSize
      : 20;
    this.request.orderBy = this.$store.getters.getSearchValues.orderBy
      ? this.$store.getters.getSearchValues.orderBy
      : "createdDate";
    this.request.orderDirection = this.$store.getters.getSearchValues.orderDirection
      ? this.$store.getters.getSearchValues.orderDirection
      : "DESC";
    this.request.query.firstName = this.$store.getters.getSearchValues.query
      ? this.$store.getters.getSearchValues.query.firstName
      : "";
    this.request.query.lastName = this.$store.getters.getSearchValues.query
      ? this.$store.getters.getSearchValues.query.lastName
      : "";
    this.request.query.email = this.$store.getters.getSearchValues.query
      ? this.$store.getters.getSearchValues.query.email
      : "";
    this.request.query.phone = this.$store.getters.getSearchValues.query
      ? this.$store.getters.getSearchValues.query.phone
      : "";
    this.request.query.product = this.$store.getters.getSearchValues.query
      ? this.$store.getters.getSearchValues.query.product
      : "";
    this.request.query.status = this.$store.getters.getSearchValues.query
      ? this.$store.getters.getSearchValues.query.status
      : {
        any: false,
        incomplete: false,
        cancelled: false,
        processing: true,
        awaitingSignatureService: false,
        signed: false,
        onHold: true,
        failed: false,
        botError: true,
        completed: false,
        chargeback: false,
        deleted: false,
      };
    this.request.query.orderId = this.$store.getters.getSearchValues.query
      ? this.$store.getters.getSearchValues.query.orderId
      : "";
    this.request.query.dateRange = this.$store.getters.getSearchValues.query
      ? this.$store.getters.getSearchValues.query.dateRange
      : "THIRTY_DAYS";
    this.request.query.createdAt = this.$store.getters.getSearchValues.query
      ? this.$store.getters.getSearchValues.query.createdAt
      : {
        start: "",
        end: "",
      };
    if (this.$store.getters.getSearchResults != "") {
      this.searchResult = this.$store.getters.getSearchResults;
      let numPages = this.searchResult.numResults / this.searchResult.pageSize;
      this.pages = [];
      for (let i = 0; i < numPages; i++) {
        this.pages.push({
          pageNumber: i,
          current: i === this.searchResult.pageNumber,
        });
      }
    } else {
      this.doSearch();
    }
  },
};
</script>
<style scoped>
/* The heart of the matter */
.testimonial-group > .row {
  overflow-y: auto;
  display: grid !important;
  max-height: 22rem;
  max-width: 25rem;
}

.testimonial-group > .row > .col-sm-4 {
  display: inline-block;
  float: none;
}

.aside-item h2 {
  font-size: 3em;
}

.download-icon {
  width: 25px;
  margin-top: -48px;
}

.file-icons-size {
  width: 30px;
}

.ml-12 {
  margin-left: 3rem;
}

.cursor-pointer {
  cursor: pointer;
}

.aside-item p {
  font-size: 1.5em;
}

.content-center {
  justify-content: center;
}

.home-page {
  /* background-image: url(./../assets/banner.jpg); */
  background-size: cover;
}

#searchinput {
  width: 200px;
}

#cleardates {
  position: absolute;
  right: 5%;
  top: 12%;
  cursor: pointer;
  color: darkgrey;
}

.myTabList div ul {
  width: 100px !important;
}

.processordOrderAlert {
  background-color: #f27299;
  color: white;
  padding: 0.5rem 1.5rem;
  border-radius: 7px;
}

.select-date {
  padding-right: 0;
}

.select-date #searchDate {
  margin-top: 32px;
  border-radius: 999px;
}

.select-date #cleardates {
  top: 46%;
  right: 9%;
}

.select-status {
  padding-right: 0;
}
</style>
