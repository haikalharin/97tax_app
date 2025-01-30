<template>
  <page logoHref="/admin/orders" class="home-page">
   <loader :isLoaderOn="loaderOn"></loader>
    <div class="container">

      <div class="mt-3">
        <b-btn href="/admin/orders">&lt; Back to Fulfillment</b-btn>
        <!-- <auth-logout-button></auth-logout-button> -->
      </div>

      <h1 class="text-center">Internal Mailroom</h1>

      <h2 class="mt-4">Fulfillment Batches</h2>

      <div class="mb-3 mt-3" v-if="fileUploadResult.length > 0">
        <div class="alert alert-danger" v-for="(res,i) in fileUploadResult" :key="i">{{res.orderNum}} - {{res.message}}</div>
      </div>

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
                 :items="batches_table.items"
                 :fields="batches_table.fields"
                 :current-page="batches_table.currentPage"
                 :per-page="batches_table.perPage"
                 :filter="batches_table.filter"
                 :sort-compare="mySortCompare"
                 @filtered="onBatchesFiltered">

          <template slot="actions" slot-scope="row">
             <b-button size="sm" @click.stop="downloadShipingLabels(row.item, row.index, $event.target)">
              Download Labels
            </b-button>
            <b-button size="sm" @click.stop="downloadPDF(row.item, row.index, $event.target)">
              Download PDF
            </b-button>
            <b-button v-if="row.item && row.item.status == 'Mailed'"  size="sm" @click.stop="deleteBatch(row.item, row.index, $event.target)">
              Delete
            </b-button>
            <b-button  v-if="row.item && row.item.status == 'Ready'" class="btn btn-warning" size="sm" @click.stop="voidBatch(row.item, row.index, $event.target)">
              VOID
            </b-button>
          </template>
        </b-table>

        <b-row>
          <b-col md="6" class="my-1">
            <b-pagination :total-rows="batches_table.totalRows" :per-page="batches_table.perPage" v-model="batches_table.currentPage" class="my-0" />
          </b-col>
          <b-col md="6" class="my-1">
            <!-- <button class="btn btn-secondary float-md-right" @click="openAddBatchDocumentModal()" >+ Fulfillment Batch</button> -->
          </b-col>
        </b-row>

      </b-container>

      <h2 class="mt-4">External Mailroom Users</h2>

      <div class="mb-3 mt-3" v-if="addUserError.length > 0">
        <div class="alert alert-danger">{{addUserError}}</div>
      </div>

      <b-container fluid>
        <!-- User Interface controls -->
        <!-- <b-row>
          <b-col md="6" class="my-1">
            <b-form-group horizontal label="Filter" class="mb-0">
              <b-input-group>
                <b-form-input v-model="users_table.filter" autocomplete="off" placeholder="Type to Search" />
                <b-btn :disabled="!users_table.filter" @click="users_table.filter = ''">Clear</b-btn>
              </b-input-group>
            </b-form-group>
          </b-col>
          <b-col md="6" class="my-1">
            <b-form-group horizontal label="Per page" class="mb-0">
              <b-form-select :options="users_table.pageOptions" v-model="users_table.perPage" />
            </b-form-group>
          </b-col>
        </b-row> -->

        <!-- Main table element -->
        <b-table show-empty
                 stacked="md"
                 :items="users_table.items"
                 :fields="users_table.fields"
                 :current-page="users_table.currentPage"
                 :per-page="users_table.perPage"
                 :filter="users_table.filter"
                 @filtered="onFiltered">
          <template slot="actions" slot-scope="row">
            <!-- We use @click.stop here to prevent a 'row-clicked' event from also happening -->
            <b-button size="sm" @click.stop="editUser(row.item, row.index, $event.target)" class="mr-1">
              Edit
            </b-button>
            <b-button size="sm" @click.stop="deleteUser(row.item, row.index, $event.target)">
              Delete
            </b-button>
          </template>
        </b-table>

        <b-row>
          <b-col md="6" class="my-1">
            <b-pagination :total-rows="users_table.totalRows" :per-page="users_table.perPage" v-model="users_table.currentPage" class="my-0" />
          </b-col>
          <b-col md="6" class="my-1">
            <button class="btn btn-secondary float-md-right" @click="openAddMailRoomUserModal()" >+ Mailroom User</button>
          </b-col>
        </b-row>

      </b-container>
    </div>

    <b-modal ref="confirmDeletion" title="Confirm Delete" hide-footer>
      <p class="my-4">Are you sure you would like to mark these orders as Deleted?</p>
      <b-btn class="mt-3" variant="outline-danger" block @click="deleteSelectedRows()">Delete</b-btn>
    </b-modal>

    <b-modal ref="addMailRoomUser" hide-footer :title= "this.mailroomUser.update ? 'Edit MailRoom User' : 'Add MailRoom User'">
      <div class="mb-3">
        <div class="col-12">
          <input type="email" :class="['form-control', errors.email ? 'is-invalid' : '']" placeholder="Email" v-model="mailroomUser.email" >
        </div>
      </div>
      <div class="mb-3">
        <div class="col-12">
          <input type="text" :class="['form-control', errors.userName ? 'is-invalid' : '']" placeholder="Name" v-model="mailroomUser.userName" >
        </div>
      </div>
      <!-- <div class="mb-3">
        <div class="col-12">
          <input type="text" :class="['form-control', errors.phone ? 'is-invalid' : '']" v-mask="'###-###-####'" placeholder="Phone" v-model="mailroomUser.phone" >
        </div>
      </div> -->
      <div class="mb-3">
        <div class="col-12">
          <input type="password" :class="['form-control', errors.password ? 'is-invalid' : '']" placeholder="Password" v-model="mailroomUser.password" >
        </div>
      </div>
      <div class="mb-3">
        <div class="col">
          <button class="btn btn-primary" @click="saveMailRoomUser()">Submit</button>
          <button class="btn btb-primary" @click="hideAddMailRoomUserModal()">Close</button>
        </div>
      </div>
    </b-modal>

    <b-modal ref="addBatchDocument" hide-footer title="Upload Batch Document">
      <div class="mb-3">
        <div class="col-12">
          <label>CSV File</label>
          <input type="file" ref="file1" :class="['form-control', errors.file1 ? 'is-invalid' : '']">
        </div>
      </div>
      <div class="mb-3">
        <div class="col-12">
          <label>PDF File</label>
          <input type="file" ref="file2" :class="['form-control', errors.file2 ? 'is-invalid' : '']">
        </div>
      </div>
      <div class="mb-3">
        <div class="col">
          <button class="btn btn-primary" @click="uploadBatchDocument()">Submit</button>
          <button class="btn btb-primary" @click="hideAddBatchDocumentModal()">Close</button>
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
        users_table: {
          filter: null,
          perPage: 5,
          pageOptions: [5, 10, 15],
          items: [],
          fields: [
            {key: 'email', label: 'Email', sortable: true},
            {key: 'userName', label: 'Name', sortable: true, sortDirection: 'asc'},
            // {key: 'phone', label: 'Phone', sortable: true, 'class': 'text-center'},
            {key: 'actions', label: 'Actions'}
          ],
          totalRows: 1,
          currentPage: 1
        },
        batches_table: {
          filter: null,
          perPage: 5,
          pageOptions: [5, 10, 15],
          items: [],
          fields: [
            {key: 'status', label: 'Status', sortable: true, 'class': 'text-center'},
            {key: 'datetime', label: 'Date', sortable: true, 'class': 'text-center'},
            {key: 'orderNumbers', label: ' ', sortable: false, 'class': 'text-center'},
            {key: 'actions', label: 'Actions'}
          ],
          totalRows: 1,
          currentPage: 1
        },
        batches: {},
        addUserError: "",
        fileUploadResult: [],
        mailroomUser: {
          userNum: 0,
          userName: '',
          email: '',
          // phone: '',
          password: '',
          update: false
        },
        errors: {
          userName: false,
          email: false,
          // phone: false,
          password: false,
          file1: false,
          file2: false
        },
        fileUploadResult: [],
        loaderOn:false
      }
    },
    computed: {
      searchParams() {
        return querystring.encode(this.toParams(this.request.query));
      },
    },
    methods: {
      loaderStart(){
        this.loaderOn = true;
      },
      loaderEnd(){
        this.loaderOn = false;
      },
      editUser(item, index, target) {
        this.$refs.addMailRoomUser.show();
        this.mailroomUser.userNum = item.userNum;
        this.mailroomUser.userName = item.userName;
        this.mailroomUser.email = item.email;
        // this.mailroomUser.phone = item.phone;
        this.mailroomUser.password = "";
        this.mailroomUser.update = true;
      },
      mySortCompare(a, b, key) {
      if (key === 'datetime') {
        return new Date(a[key]) - new Date(b[key]);
      }
    },
      deleteUser(item, index, target) {
        AXIOS.post(`/admin/deleteMailRoomUser/` + item.userNum)
          .then(response => {
            if (response.data.code == 1) {

              for( var i = 0; i < this.users_table.items.length; i++){
                if ( this.users_table.items[i].userNum === item.userNum) {
                  this.users_table.items.splice(i, 1);
                }
              }

            }
          })
          .catch(e => {
            console.log(e);
          })
      },
      deleteBatch(item, index, target) {
        const ids = item.id.split(" - ");
        this.loaderStart();
        AXIOS.post(`/admin/deleteBatch/${ids[1]}`)
          .then(response => {
            if (response.data.code == 1) {

              for( var i = 0; i < this.batches_table.items.length; i++){
                if ( this.batches_table.items[i].id === item.id) {
                  this.batches_table.items.splice(i, 1);
                }
              }

            }
            this.loaderEnd();
          })
          .catch(e => {
            this.loaderEnd();
            console.log(e);
          })
      },
      voidBatch(item, index, target){
        this.loaderStart();
        const ids = item.id.split(" - ");
          AXIOS.post(`/admin/voidBatch/${ids[1]}`)
          .then(response => {
            this.deleteBatch(item, index, target)
          })
          .catch(e => {
             this.loaderEnd();
            console.log(e);
          })
      },
      downloadPDF(item, index, target) {
        var ids = item.id.split(" - ");
        window.location = `/api/admin/mailroom/downloadPDF?start=${ids[0]}&end=${ids[1]}`;
      },
      downloadShipingLabels(item, index, target){
       var ids = item.id.split(" - ");
       window.location.href = `/api/admin/mailroom/downloadLabels?start=${ids[0]}&end=${ids[1]}`;
      },
      onFiltered (filteredItems) {
        // Trigger pagination to update the number of buttons/pages due to filtering
        this.users_table.totalRows = filteredItems.length
        this.users_table.currentPage = 1
      },
      onBatchesFiltered(filteredItems){
          this.batches_table.totalRows=filteredItems.length
          this.batches_table.currentPage=1
      },
      openAddMailRoomUserModal() {
        this.$refs.addMailRoomUser.show();
        this.mailroomUser.userNum = 0;
        this.mailroomUser.userName = "";
        this.mailroomUser.email = "";
        // this.mailroomUser.phone = "";
        this.mailroomUser.password = "";
        this.mailroomUser.update = false;
      },
      hideAddMailRoomUserModal() {
        this.$refs.addMailRoomUser.hide();
      },
      openAddBatchDocumentModal() {
        this.$refs.addBatchDocument.show();
      },
      hideAddBatchDocumentModal() {
        this.$refs.addBatchDocument.hide();
      },
      saveMailRoomUser() {
        var hasErrors = false;
        let required =  ['userName', 'email'/*, 'phone'*/];
        required.forEach((p) => {
          if (!this.mailroomUser[p] || this.mailroomUser[p].trim().length === 0) {
            this.errors[p] = true;
            hasErrors = true;
          } else {
            this.errors[p] = false;
          }
        });

        if (!this.mailroomUser.update && this.mailroomUser.password.length == 0) {
          hasErrors = true;
          this.errors['password'] = true;
        }

        if (SiteUtils.validateEmail(this.mailroomUser.email)) {
          this.errors.email = true;
          hasErrors = true;
        }

        // if (SiteUtils.validatePhone(this.mailroomUser.phone)) {
        //   this.errors.phone = true;
        //   hasErrors = true;
        // }

        if (hasErrors) {
          return;
        }

        // this.mailroomUser.phone = SiteUtils.standardizePhone(this.mailroomUser.phone);
        this.hideAddMailRoomUserModal();
        AXIOS.post(`/admin/addMailRoomUser`, this.mailroomUser)
          .then(response => {

            if (response.data.code == 1) {
              var isExist = false;
              this.users_table.items.forEach((p) => {
                if (p.userNum == response.data.user.userNum) {
                  p.userNum = response.data.user.userNum;
                  p.userName = response.data.user.userName;
                  p.email = response.data.user.email;
                  // p.phone = response.data.user.phone;
                  isExist = true;
                }
              })

              if (!isExist) {
                this.users_table.items.push({
                  userNum: response.data.user.userNum,
                  userName: response.data.user.userName,
                  email: response.data.user.email,
                  // phone: response.data.user.phone
                })
              }

              this.addUserError = "";
            } else {
              this.addUserError = response.data.msg;
            }
          })
          .catch(e => {
            console.log(e);
          })
      },

      getMailRoomUsers() {
        AXIOS.get(`/admin/mailRoomUsers`)
          .then(response => {
            console.log(response);

            response.data.forEach((p) => {
              this.users_table.items.push({
                userName: p.userName,
                email: p.email,
                // phone: p.phone,
                userNum: p.userNum
              })
            });
          })
          .catch(e => {
            console.log(e)
            // this.$router.push('/login');
          })
      },
      getMailRoomBatches() {
        AXIOS.get(`/admin/mailRoomBatches`)
          .then(response => {
            response.data.forEach((p) => {
              var key = moment(p.createdAt).format('MMM DD, YYYY HH:mm:ss')
              if(this.batches[key] !== undefined){
                this.batches[key].push({
                  id: p.id,
                  startOrderNumber: p.startOrderNumber,
                  endOrderNumber: p.endOrderNumber,
                  datetime: p.createdAt,
                  status: p.status
                });
              }
              else{
                this.batches[key] = [{
                  id: p.id,
                  startOrderNumber: p.startOrderNumber,
                  endOrderNumber: p.endOrderNumber,
                  datetime: p.createdAt,
                  status: p.status
                }];
              }
            });
            for(var key in this.batches) {
              var chunk = 50;
              for (var i=0; i<this.batches[key].length; i+=chunk) {
                var temparray   = this.batches[key].slice(i,i+chunk);
                var firstBatch  = temparray[0];
                var lastBatch   = temparray[temparray.length - 1];
                const orderNums = firstBatch.endOrderNumber ? `${ firstBatch.endOrderNumber } - ${ firstBatch.startOrderNumber }` : '';

                this.batches_table.items.push({
                  id: firstBatch.id + ' - ' + lastBatch.id,
                  startOrderId: firstBatch.startOrderNumber,
                  endOrderId: firstBatch.endOrderNumber,
                  orderNumbers: orderNums,
                  datetime: firstBatch.datetime,
                  status: firstBatch.status
                })
              }
            }
            this.sortBatchDataDescendingOrder();
          })
          .catch(e => {
            this.$router.push('/login');
          })
      },
      sortBatchDataDescendingOrder(){
        this.batches_table.items.sort( ( a, b) => {
            return new Date(b.datetime) - new Date(a.datetime);
        });
        return this.batches_table.items;

      },
      uploadBatchDocument() {
        let file1 = this.$refs.file1.files[0];
        let file2 = this.$refs.file2.files[0];

        let hasErrors = false;
        if (!file1) {
          this.errors.file1 = true;
          hasErrors = true;
        }

        if (!file2) {
          this.errors.file2 = true;
          hasErrors = true;
        }

        if (hasErrors) {
          return;
        }

        let self = this;
        let formData = new FormData();
        formData.append('file1', file1);
        formData.append('file2', file2);

        this.hideAddBatchDocumentModal();
        AXIOS.post('/admin/upload_batch',
          formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
        ).then(function(response){
          self.fileUploadResult = response.data;
          if (self.fileUploadResult.length == 0) {
            window.location = "/admin/mailroom";
          }

        },function(err){
          alert("An error has occurred. Please double check the CSV file.");
          console.log(err)
        })
      }
    },
    mounted() {
      this.getMailRoomUsers();
      this.getMailRoomBatches();
    }
  }
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
  .fa-sort {
    margin-top: 4px;
  }
</style>
