<template>
  <page logoHref="/admin/getUsers">
    <div class="container" style="margin-top: 3%;">
      <button class="btn btn-secondary" @click="$router.push('/admin/orders')">&lt; Orders</button>
      <button class="btn btn-outline-secondary" @click="openAddDialog()">Add new User</button>
      <table class="table table-bordered">
        <thead>
          <tr>
            <th>User Name</th>
            <th>Email</th>
            <th>Order Status Change</th>
            <th>Void/Refund Payments</th>
            <th>Edit Order Data</th>
            <th>SSN Privacy</th>
            <th>Chargeback Authorization</th>
            <th>Fulfillment Access</th>
            <th>Automated Schedule Access</th>
            <th>Manual Status Change Ability</th>
            <th>Bot Logs Access</th>
            <th></th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(user,i) in filteredUsers" :key="i">
            <td>{{user.username}}</td>
            <td>{{user.email}}</td>
            <td>{{getAccessType(user.orderStatus)}}</td>
            <td>{{getAccessType(user.refundPayment)}}</td>
            <td>{{getAccessType(user.editRecord)}}</td>
            <td>{{user.ssnPrivacy}}</td>
            <td>{{user.chargebackAuth}}</td>
            <td>{{user.fulfilmentAccess}}</td>
            <td>{{user.automatedScheduleAccess}}</td>
            <td>{{user.manualStatusChange}}</td>
            <td>{{user.botLogsAccess}}</td>
            <td>
              <button
                class="btn btn-secondary"
                v-if="user.userType=='StandardUser'"
                @click="openEditDialog(user)"
              >Edit</button>
            </td>
            <td>
              <button
                class="btn btn-secondary"
                v-if="user.userType=='StandardUser'"
                @click="deleteUser(user)"
              >Delete</button>
            </td>
          </tr>
        </tbody>
      </table>

      <b-modal ref="userModal" title="User Details" hide-footer no-close-on-backdrop>
        <div class="row">
          <div class="col-6">
            <label>Username</label>
          </div>
          <div class="col-6">
            <input class="form-control" v-model="userData.username" :disabled="isEditMode" />
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <label>E-mail</label>
          </div>
          <div class="col-6">
            <input class="form-control" v-model="userData.email" autocomplete="off" />
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <label>Password</label>
          </div>
          <div class="col-6">
            <input
              class="form-control"
              type="password"
              v-model="userData.password"
              autocomplete="off" />
          </div>
        </div>
        <!-- <div class="row">
           <div class="col-4">
              <input type="radio" :value="accessTypes.fullAccess" :name="'options'" v-model="accessType">
              <label>Full Access</label>
           </div>
           <div class="col-4">
              <input type="radio" :value="accessTypes.customAccess" :name="'options'" v-model="accessType" @change="changeAccessType">
              <label>Custom Access</label>
           </div>
            <div class="col-4">
              <input type="radio" :value="accessTypes.noAccess" :name="'options'" v-model="accessType">
              <label>No Access</label>
           </div>
        </div>-->
        <div class="row">
          <div class="col-6">
            <label>Order status change</label>
          </div>
          <div class="col-6">
            <select class="form-control" v-model="userData.orderStatus">
              <option v-for="accessOption in accessOptions" :value="accessOption.value" v-bind:key="accessOption.value">
                {{ accessOption.name }}
              </option>
            </select>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <label>Void or Refund Payments</label>
          </div>
          <div class="col-6">
            <select class="form-control" v-model="userData.refundPayment">
              <option v-for="accessOption in accessOptions" :value="accessOption.value" v-bind:key="accessOption.value">
                {{ accessOption.name }}
              </option>
            </select>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <label>Edit Order</label>
          </div>
          <div class="col-6">
            <select class="form-control" v-model="userData.editRecord">
              <option v-for="accessOption in accessOptions" v-bind:value="accessOption.value" v-bind:key="accessOption.value">
                {{ accessOption.name }}
              </option>
            </select>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <label>SSN Privacy</label>
          </div>
          <div class="col-6">
            <input type="checkbox" v-model="userData.ssnPrivacy" />
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <label>Chargeback Authorization</label>
          </div>
          <div class="col-6">
            <input type="checkbox" v-model="userData.chargebackAuth" />
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <label>Allow Fulfilment Access</label>
          </div>
          <div class="col-6">
            <input type="checkbox" v-model="userData.fulfilmentAccess" />
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <label>Allow Automated Schedule Access</label>
          </div>
          <div class="col-6">
            <input type="checkbox" v-model="userData.automatedScheduleAccess" />
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <label>Manual Status Change Ability</label>
          </div>
          <div class="col-6">
            <input type="checkbox" v-model="userData.manualStatusChange" />
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <label>Bot Logs Access</label>
          </div>
          <div class="col-6">
            <input type="checkbox" v-model="userData.botLogsAccess" />
          </div>
        </div>
        <div class="row mt-2">
          <div class="col">
            <button
              :class="['btn', isEditMode ? 'btn-success' : 'btn-primary']"
              @click="registerUser()">
              {{isEditMode ? "Save" : "Register"}}
            </button>
            <button class="btn btn-outline-secondary" @click="closeDialog()">Close</button>
          </div>
        </div>
      </b-modal>
    </div>
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
import { AXIOS } from "../scripts/http-common";
import { uuid } from "vue-uuid";

export default {
  data() {
    return {
      userData: {
        username: "",
        email: "",
        password: "",
        orderStatus: 1,
        refundPayment: 1,
        editRecord: 1,
        ssnPrivacy: false,
        chargebackAuth: false,
        fulfilmentAccess: false,
        automatedScheduleAccess: false,
        manualStatusChange: false,
        botLogsAccess: false,
        userType: "StandardUser"
      },
      //accessTypes:Object.freeze({ fullAccess:"fullAccess", customAccess:"customAccess",noAccess:"noAccess" }),
      //accessType:"",
      fullAccessValue:4118,
      noAccessValue:0,
      customAccessDefaultValue:1,
      usersList: [],
      isEditMode: false,
      accessOptions:[
        {name:"Full Access",value:4118},
        {name:"No Access",value:0},
        {name:"1-Week",value:1},
        {name:"2-Weeks",value:2},
        {name:"3-Weeks",value:3},
        {name:"4-Weeks",value:4},
        {name:"12-Weeks",value:12}
      ]
    };
  },
  methods: {
    getUsers(callback) {
      AXIOS.get("/admin/usersList")
        .then(res => {
          if (res.data != null) {
            this.usersList = res.data;
            if (!!callback) {
              callback();
            }
          }
        })
        .catch(err => {
          alert(err);
        });
    },
    changeAccessType() {
      if(this.userData.orderStatus == this.fullAccessValue && this.userData.refundPayment == this.fullAccessValue && this.userData.editRecord == this.fullAccessValue){
        this.userData.orderStatus = this.customAccessDefaultValue;
        this.userData.refundPayment = this.customAccessDefaultValue;
        this.userData.editRecord = this.customAccessDefaultValue;
      }
      else if(this.userData.orderStatus == this.noAccessValue && this.userData.refundPayment == this.noAccessValue && this.userData.editRecord == this.noAccessValue){
        this.userData.orderStatus = this.customAccessDefaultValue;
        this.userData.refundPayment = this.customAccessDefaultValue;
        this.userData.editRecord = this.customAccessDefaultValue;
      }
    },
    getAccessType(accessTypeValue){
      if(accessTypeValue == this.fullAccessValue){
        return "Full Access";
      }else if(accessTypeValue == this.noAccessValue){
        return "No Access";
      }
      else if(accessTypeValue){
        return accessTypeValue +" Weeks";
      }
    },
    openAddDialog() {
      this.isEditMode = false;
      this.userData = {};
      this.$refs.userModal.show();
    },
    closeDialog() {
      if (this.userData) {
        this.resetDialog();
      }
      this.$refs.userModal.hide();
    },
    populateWeeksToAccessType(){
      if(this.userData.orderStatus == this.fullAccessValue && this.userData.refundPayment == this.fullAccessValue && this.userData.editRecord == this.fullAccessValue){
        this.accessType = this.accessTypes.fullAccess;
      }else if(this.userData.orderStatus == this.noAccessValue && this.userData.refundPayment == this.noAccessValue && this.userData.editRecord == this.noAccessValue){
        this.accessType = this.accessTypes.noAccess;
      }else{
        this.accessType = this.accessTypes.customAccess;
      }
    },
    populateAccessTypeToWeeks(){
      if(this.accessType == this.accessTypes.fullAccess){
        this.userData.orderStatus = this.fullAccessValue;
        this.userData.refundPayment = this.fullAccessValue;
        this.userData.editRecord = this.fullAccessValue;
      }else if(this.accessType == this.accessTypes.noAccess){
        this.userData.orderStatus = this.noAccessValue;
        this.userData.refundPayment = this.noAccessValue;
        this.userData.editRecord = this.noAccessValue;
      }
    },
    registerUser() {
      if (!this.isEditMode) {
        //this.populateAccessTypeToWeeks();
        let user = Object.assign({}, this.userData);
        AXIOS.put("/admin/registerUser", user)
          .then(res => {
            this.getUsers(() => {
              alert(this.userData.username + " added successfully!");
              this.$refs.userModal.hide();
            });
          })
          .catch(err => {
            alert(err);
          });
      } else {
        //this.populateAccessTypeToWeeks();
        let user = Object.assign({}, this.userData);
        AXIOS.put("/admin/updateUser", user)
          .then(res => {
            this.getUsers(() => {
              alert(this.userData.username + " updated successfully!");
              this.$refs.userModal.hide();
            });
          })
          .catch(err => {
            alert(err);
          });
      }
    },
    deleteUser(user) {
      let userTo = user;
      AXIOS.post("/admin/users/delete", user)
        .then(res => {
          if (res.data) {
            this.usersList = res.data;
            alert(userTo.username + " deleted successfully!");
            this.$refs.userModal.hide();
          }
        })
        .catch(err => {
          alert("User Not found");
        });
    },

    openEditDialog(user) {
      this.isEditMode = true;
      this.userData = { ...user };
      //this.populateWeeksToAccessType();
      this.$refs.userModal.show();
    },
    resetDialog() {
      this.userData.username = "";
      this.userData.email = "";
      this.userData.password = "";
      this.userData.orderStatus = 0;
      this.userData.refundPayment = 0;
      this.userData.editRecord = 0;
      this.userData.ssnPrivacy = false;
      this.userData.chargebackAuth = false;
      this.userData.fulfilmentAccess = false;
      this.userData.automatedScheduleAccess = false;
      this.userData.manualStatusChange = false;
      this.userData.botLogsAccess = false;
      this.userData.userType = "StandardUser";
    }
  },
  mounted() {
    this.getUsers();
  },
  computed: {
    filteredUsers() {
      let users = [];
      if (this.usersList && this.usersList.length > 0) {
        this.usersList.forEach(user => {
          if (user.userType != "admin") {
            users.push(user);
          }
        });
      }
      return users;
    }
  }
};
</script>
