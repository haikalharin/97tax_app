<template>
  <page logoHref="/admin/orders" class="home-page">
    <loader :isLoaderOn="loaderOn"></loader>
    <div class="container">
      <b-modal
          content-class="m-0 p-0" body-class="p-0"
          size="xl"
          ref="screenshotModal"
          id="screenshotModal"
          hide-footer
          hide-header
      >
        <div class="row">
          <div class="col-12">
            <img
              style="width: 100%!important;"
              v-bind:src="theScreenshot" />
          </div>
        </div>
      </b-modal>

      <div
        class="d-flex flex-row justify-content-start mt-4 align-middle"
        style="gap: 32px"
      >
        <div>
          <button
            class="btn btn-secondary font-weight-bold"
            v-on:click="backToOrder">
            &lt; Back to Order
          </button>
        </div>

        <div class="d-flex flex-column" style="font-size: 20px; color: black">
          <div><b>Order #: </b> {{ $route.params.id }}</div>
        </div>
      </div>
      <hr />

      <div class="mb-2" v-if="totalCount > 0">
        <div class="text-center">
          <span v-for="index in totalPages" :key="index">
            <span class="btn" v-if="currentPage == index - 1">{{ index }}</span>
            <button class="btn btn-link" href="#" v-else @click="loadPage(index - 1)">
              {{ index }}
            </button>
          </span>
        </div>
      </div>

      <table class="table table-bordered">
        <thead>
          <tr>
            <th>#</th>
            <th>Date</th>
            <th>Status</th>
            <th>URL</th>
            <th>Screenshot</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(log, idx) in botLogs"
            :key="idx"
          >
            <td>
              <span style="width: 30px; max-width: 30px;">
                {{ currentPage * PAGE_SIZE + idx + 1 }}
              </span>
            </td>
            <td>
              {{ log.created_date }}
            </td>
            <td :style="statusColumnStyle(log)">
              <b>
                {{ statusColumnText(log) }}
              </b>
            </td>
            <td>
              <div style="inline-size: 450px; overflow-wrap: break-word;">
                {{ log.url }}
              </div>
            </td>
            <td>
              <img
                v-if="!!log.screenshot_base64"
                style="width: 250px; max-width: 250px; cursor: pointer;"
                v-bind:src="log.screenshot_base64"
                v-on:click="showImage(log.screenshot_base64)" />
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="totalCount > 0">
        <div class="text-center">
          <span v-for="index in totalPages" :key="index">
            <span class="btn" v-if="currentPage == index - 1">{{ index }}</span>
            <button class="btn btn-link" href="#" v-else @click="loadPage(index - 1)">
              {{ index }}
            </button>
          </span>
        </div>
      </div>
    </div>

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
      ((o = new Date()).setTime(o.getTime() + r),
      (l = "; expires=" + o.toUTCString())),
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
import { AXIOS } from "../scripts/http-common";
import { methods } from "../shared/methods";
import moment from "moment";

export default {
  data() {
    return {
      botLogs: [],
      totalCount: 0,
      totalPages: 0,
      currentPage: 0,
      theScreenshot: "",
      PAGE_SIZE: 25,
      dataLoaded: false
    };
  },

  created() {
  },

  methods: {
      statusColumnStyle(log) {
        if (log.is_error == 1) {
          return 'color: red';
        }
        if (!!log.error_code && log.error_code > 0) {
          return 'color: orange';
        }
        return 'color: green';
      },
      statusColumnText(log) {
        let isErrorCode = !!log.error_code && log.error_code > 0;
        if (log.is_error == 1)  {
          return isErrorCode ? "Error (" + log.error_code + ")" : "Error";
        }
        return isErrorCode ? "Warning (" + log.error_code + ")" : "Success";
      },

      showImage(image_base64) {
        this.theScreenshot = image_base64;
        this.$refs.screenshotModal.show();
      },

      backToOrder() {
        let orderURI = `/admin/ein/${this.$route.params.id}`;
        this.$router.push(orderURI);
      },

      loadPage(page) {
        this.currentPage = page;
        AXIOS.get(`/ein/bot/logs/${this.$route.params.id}?page=${this.currentPage}&size=${this.PAGE_SIZE}`)
          .then((response) => {
            let result = response.data;
            this.botLogs = result.rows;
            this.totalCount = result.numResults;
            this.totalPages = Math.floor((this.totalCount + this.PAGE_SIZE - 1) / this.PAGE_SIZE);

            this.dataLoaded = true;
            console.log(this.botLogs.length);
          })
          .catch((e) => {
            this.$router.push("/login");
          });
      },
  },

  mounted() {
    this.loadPage(0);
  },
};
</script>

<style scoped>
.aside-item h2 {
  font-size: 3em;
}
.aside-item p {
  font-size: 1.5em;
}
.home-page {
  /* background-image: url(./../assets/banner.jpg); */
  background-size: cover;
}

table,
th,
td {
  border: 2px solid lightgrey;
  border-collapse: collapse;
}
th,
td {
  padding: 5px;
  text-align: left;
  width: 50%;
}

hr {
  border: 0;
  clear: both;
  display: block;
  width: 100%;
  background-color: lightgray;
  height: 1px;
}
.hide-br {
  display: none;
}

@media (min-width: 64em) {
  .hide-br {
    display: initial;
  }
}

.business-address label {
  font-weight: 700 !important;
  margin-top: 0.25rem !important;
  margin-bottom: 0 !important;
}
</style>
