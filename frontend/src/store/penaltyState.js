import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    penalty: {
        penalties: [],
        fName: '',
        lName: '',
        email: ''
    }
  },
  mutations: {
    increment (state, payload) {
      state.penalty = payload;
    }
  }
})

export default store;