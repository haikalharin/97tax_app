import Vue from 'vue'
import Vuex from 'vuex'
Vue.use(Vuex);

let store = new Vuex.Store({
  state: {
    searchValues: '',
    searchResult: '',
    logedUser: '',
    currentOrder: {},
  },
  mutations: {
    setSearchedValues(state, newValues) {
      state.searchValues = newValues;
    },
    setSearchedResults(state, newValues) {
      state.searchResult = newValues;
    },
    setLogedUser(state, newValues) {
      state.logedUser = newValues;
    },
    setOrderData(state, payload) {
      state.currentOrder = payload
    },
    emptyOrder(state, payload) {
      state.currentOrder = {}
    }
  },
  actions: {
    setSearch(state, newValues) {
      state.commit('setSearchedValues', newValues)
    },
    setResult(state, newValues) {
      state.commit('setSearchedResults', newValues)
    },
    setUser(state, newUser) {
      state.commit('setLogedUser', newUser)
    },
    SET_CURRENT_ORDER(context, payload) {
      context.commit("setOrderData", payload);
    },
    CLEAR_ORDER(context, payload) {
      context.commit("emptyOrder", payload);
    },
  },
  getters: {
    getSearchValues: state => {
      return state.searchValues;
    },
    getSearchResults: state => {
      return state.searchResult;
    },
    getUser: state => {
      return state.logedUser;
    },
    getCurrentOrder: state => {
      return state.currentOrder;
    },
  }
});

export default store;
