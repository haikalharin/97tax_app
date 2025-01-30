<template>
  <div>
    <h1>Penalty Order History</h1>
    <h2 class="mt-4">Search Filters</h2>
    <div class="row mb-2">
      <div class="col-6 col-sm-3">
        <div class="form-group">
          <label> Email </label>
          <input class="form-control" v-model="request.query.email" />
        </div>
      </div>
      <div class="col-6 col-sm-3">
        <div class="form-group">
          <label> Phone </label>
          <input class="form-control" v-model="request.query.phone" />
        </div>
      </div>
      <div class="col-6 col-sm-3">
        <div class="form-group">
          <label> Order Number </label>
          <input class="form-control" v-model="request.query.orderId" />
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
            </b-dropdown>
          </div>
        </div>
      </div>
      <div class="col-6 col-sm-3">
        <div class="form-group">
          <label> First Name </label>
          <input class="form-control" v-model="request.query.firstName" />
        </div>
      </div>

      <div class="col-6 col-sm-3">
        <div class="form-group">
          <label> Last Name </label>
          <input class="form-control" v-model="request.query.lastName" />
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col-6 col-sm">
        <button class="btn btn-secondary" @click="resetSearch()">Search</button>
      </div>
    </div>
    <h6 class="mt-4">Results ({{ searchResult.numResults }})</h6>

    <div class="row" v-if="searchResult.rows.length === 0">
      <div class="col">
        <div class="font-weight-bold">No orders match the given criteria.</div>
      </div>
    </div>
    <div class="text-center">
      <span v-for="(page, idx) in pages" :key="idx">
        <span class="btn" v-if="page.current">{{ page.pageNumber + 1 }}</span>
        <button class="btn btn-link" href="#" v-else @click="loadPage(page)">
          {{ page.pageNumber + 1 }}
        </button>
      </span>
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
            <input type="checkbox" v-model="order.checked" />
          </td>
          <td>{{ order.status_last_changed }}</td>
          <td>{{ order.id }}</td>
          <td>{{ order.status }}</td>
          <td>{{ order.fname }}</td>
          <td>{{ order.lname }}</td>
          <td>{{ order.email }}</td>
          <td>{{ order.phone }}</td>
          <td>
            <button class="btn btn-secondary" @click="gotoDetails(order.id)">
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
  </div>
</template>

<script>
import { AXIOS } from "../scripts/http-common";
import moment from "moment-timezone";
import querystring from "querystring";
import format from "date-fns/format";
import omit from "lodash/omit";

export default {
  components: {},
  data() {
    return {
      searchResult: {
        numResults: 0,
        resultsInPage: 0,
        rows: [],
      },
      request: {
        pageNumber: 0,
        pageSize: 20,
        orderBy: "status_last_changed",
        orderDirection: "DESC",
        query: {
          firstName: "",
          lastName: "",
          email: "",
          phone: "",
          status: {
            any: false,
            incomplete: false,
            cancelled: false,
            processing: true,
            onHold: false,
            failed: false,
            chargeback: false,
            completed: false,
          },
          orderId: "",
        },
      },
      pages: [
        {
          pageNumber: 0,
          current: true,
        },
      ],
    };
  },

  mounted() {
    this.doSearch();
  },

  methods: {
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

    handleStatusChange() {
      this.request.query.status.any = false;
    },
    handleAnyStatusChange() {
      if (this.request.query.status.any) {
        this.request.query.status.incomplete = false;
        this.request.query.status.cancelled = false;
        this.request.query.status.processing = false;
        this.request.query.status.onHold = false;
        this.request.query.status.failed = false;
        this.request.query.status.completed = false;
        this.request.query.status.chargeback = false;
      }
    },
    handleDataRangeChange() {
      if (this.request.query.dateRange !== "CUSTOM") {
        this.request.query.createdAt.start = "";
        this.request.query.createdAt.end = "";
      }
    },
    clearDates() {
      (this.request.query.createdAt.start = ""),
        (this.request.query.createdAt.end = "");
    },
    doSearch() {
      let requestToSearch = _.cloneDeep(this.request);

      requestToSearch.query.firstName = requestToSearch.query.firstName.trim();
      requestToSearch.query.lastName = requestToSearch.query.lastName.trim();
      requestToSearch.query.email = requestToSearch.query.email.trim();
      requestToSearch.query.phone = requestToSearch.query.phone.trim();
      requestToSearch.query.orderId = requestToSearch.query.orderId.trim();

      AXIOS.post(`/admin/penalty-orders`, requestToSearch)
        .then((response) => {
          this.$set(this, "searchResult", response.data);
          for (let row of this.searchResult.rows) {
            row.checked = false;
          }
          let numPages =
            this.searchResult.numResults / this.searchResult.pageSize;
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
    loadPage(page) {
      this.request.pageNumber = page.pageNumber;
      this.doSearch();
    },
    resetSearch() {
      this.request.pageNumber = 0;
      this.$store.dispatch("setSearch", this.request);
      this.doSearch();
    },
    gotoDetails(id) {
      console.log(id);
      this.$router.push(`/admin/penalty-order/${id}`);
    },
  },
};
</script>
<style scoped>
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

