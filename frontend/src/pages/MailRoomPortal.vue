<template>
  <page logoHref="/admin/orders" class="home-page">
    <div class="container py-3">

      <div class="mb-3">
        <auth-logout-button></auth-logout-button>
      </div>

      <h1 class="text-center">Mail Room</h1>

      <b-container fluid>
        <!-- User Interface controls -->
        <b-row>
          <b-col md="6" class="my-1">
            <b-form-group horizontal label="Filter" class="mb-0">
              <b-input-group>
                <b-form-input v-model="batches_table.filter" placeholder="Type to Search" />
                <b-btn :disabled="!batches_table.filter" @click="batches_table.filter = ''">Clear</b-btn>
              </b-input-group>
            </b-form-group>
          </b-col>
          <b-col md="6" class="my-1">
            <b-form-group horizontal label="Per page" class="mb-0">
              <b-form-select :options="batches_table.pageOptions" v-model="batches_table.perPage" />
            </b-form-group>
          </b-col>
        </b-row>

        <!-- Main table element -->
        <b-table show-empty
                 stacked="md"
                 class="batches-table"
                 :items="batches_table.items"
                 :fields="batches_table.fields"
                 :current-page="batches_table.currentPage"
                 :per-page="batches_table.perPage"
                 :filter="batches_table.filter"
                 @filtered="onFiltered">

          <template slot="actions" slot-scope="row">
            <b-button size="sm" @click.stop="downloadLabels(row.item, row.index, $event.target)">
              Download Labels
            </b-button>
            <b-button size="sm" @click.stop="downloadPDF(row.item, row.index, $event.target)">
              Download PDF
            </b-button>
            <b-button class="d-none" size="sm" @click.stop="showUploadTrackingModal(row.item, row.index, $event.target)">
              Upload Tracking
            </b-button>
          </template>
        </b-table>

        <b-row>
          <b-col md="6" class="my-1">
            <b-pagination :total-rows="batches_table.totalRows" :per-page="batches_table.perPage" v-model="batches_table.currentPage" class="my-0" />
          </b-col>
          <b-col md="6" class="my-1">
            <div class="d-flex align-items-start flex-column ml-auto" style="width: fit-content;">
              <span class="direction">Directions</span>
              <a href="@/assets/MailroomInstuctions.pdf" download class="small">mailroominstructions.PDF</a>
              <span class="small">UPDATED: 10/17/2020</span>
            </div>
          </b-col>
        </b-row>

      </b-container>

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
          <tr v-for="(res, index) in fileUploadResult" :key="index">
            <td>{{res.orderNum}}</td>
            <td>{{res.message}}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <b-modal ref="uploadTracking" hide-footer title="Upload Tracking CSV">
      <div class="mb-3">
        <div class="col-12">
          <label>Tracking CSV File</label>
          <input type="file" ref="file" :class="['form-control', errors.file ? 'is-invalid' : '']">
        </div>
      </div>
      <div class="mb-3">
        <div class="col">
          <button class="btn btn-primary" @click="uploadTracking()">Submit</button>
          <button class="btn btb-primary" @click="hideUploadTrackingModal()">Close</button>
        </div>
      </div>
    </b-modal>
  </page>
</template>
<!-- sharea sale tag -->
<script>
  var shareasaleSSCID=shareasaleGetParameterByName("sscid");
function shareasaleSetCookie(e,a,r,s,t){
      if(e&&a)
            {var o,n=s?"; path="+s:"",i=t?"; domain="+t:"",l="";r&&((o=new Date).setTime(o.getTime()+r),l="; expires="+o.toUTCString()),
        document.cookie=e+"="+a+l+n+i
     }}
        function shareasaleGetParameterByName(e,a)
        {a||(a=window.location.href),e=e.replace(/[\[\]]/g,"\\$&");
        var r=new RegExp("[?&]"+e+"(=([^&#]*)|&|#|$)").exec(a);
return r?r[2]?decodeURIComponent(r[2].replace(/\+/g," ")):"":null}
shareasaleSSCID&&shareasaleSetCookie("shareasaleSSCID",
shareasaleSSCID,94670778e4,"/");
</script>

<script>
  import {AXIOS} from '../scripts/http-common';
  import querystring from 'querystring'
  import {SiteUtils} from '../scripts/site-common'
  import moment from 'moment'

  export default {
    data() {
      return {
        batches_table: {
          filter: null,
          perPage: 5,
          pageOptions: [5, 10, 15],
          items: [],
          fields: [
            {key: 'status', label: 'Status', sortable: true, 'class': 'text-center'},
            {key: 'datetime', label: 'Date', sortable: true, 'class': 'text-center'},
            {key: 'batch', label: ' ', sortable: false, 'class': 'text-center'},
            {key: 'actions', label: 'Actions'}
          ],
          totalRows: 1,
          currentPage: 1
        },
        errors: {
          file: false
        },
        batches: {},
        batchId: 0,
        fileUploadResult: []
      }
    },
    computed: {
      searchParams() {
        return querystring.encode(this.toParams(this.request.query));
      }
    },
    methods: {
      downloadPDF(item, index, target) {
        window.location = "/api/mailroom/downloadPDF/" + item.id;
      },
      downloadLabels(item, index, target) {
        window.location.href = "/api/mailroom/downloadLabels/" + item.id
      },
      onFiltered (filteredItems) {
        // Trigger pagination to update the number of buttons/pages due to filtering
        this.batches_table.totalRows = filteredItems.length
        this.batches_table.currentPage = 1
      },
      showUploadTrackingModal(item, index, target) {
        this.batchId = item.id;
        this.$refs.uploadTracking.show();
      },
      hideUploadTrackingModal() {
        this.$refs.uploadTracking.hide();
      },
      sortBatchDataDescendingOrder(){
      this.batches_table.items.sort( ( a, b) => {
            return new Date(b.datetime) - new Date(a.datetime);
        });
      },
      getMailRoomBatches() {
        AXIOS.get(`/mailroom/mailRoomBatches`)
          .then(response => {
            response.data.forEach((p) => {
              console.log(p);
              this.batches_table.items.push({
                id: p.id,
                datetime: p.createdAt,
                status: p.status,
                batch: `${p.startOrderNumber} - ${p.endOrderNumber}`
              })
            });
            this.sortBatchDataDescendingOrder();
          })
          .catch(e => {
            this.$router.push('/login');
          })
      },
      uploadTracking() {
        let file = this.$refs.file.files[0];

        let hasErrors = false;
        if (!file) {
          this.errors.file = true;
          hasErrors = true;
        }

        if (hasErrors) return;

        let self = this;
        let formData = new FormData();
        formData.append('file', file);

        this.hideUploadTrackingModal();
        AXIOS.post('/mailroom/upload_tracking/' + self.batchId,
          formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
        ).then(function(response){
          self.fileUploadResult = response.data;
          for( var i = 0; i < self.batches_table.items.length; i++){
            if ( self.batches_table.items[i].id === self.batchId) {
              self.batches_table.items[i].status = "Mailed";
            }
          }

        }).catch(function(e) {
          alert("An error has occurred. Please double check the CSV file.")
        });
      }
    },
    mounted() {
      this.getMailRoomBatches();
    }
  }
</script>
<style scoped>
.container-fluid {
  padding: 20px 0px;
}
  .aside-item h2 {
    font-size: 3em;
  }
  .aside-item p {
    font-size: 1.5em;
  }
  .batches-table {
    table-layout: fixed;
  }
  .batches-table td,
  .batches-table th {
    text-align: left!important;
  }
  .home-page {
    /* background-image: url(./../assets/banner.jpg); */
    background-size: cover;
  }
  .fa-sort {
    margin-top: 4px;
  }
  .direction {
    font-size: 18px;
  }
  .small {
    font-size: 14px;

  }
</style>
