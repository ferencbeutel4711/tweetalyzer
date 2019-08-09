import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        activeModule: ''
    },
    mutations: {
        changeActiveModule(state, activeModule) {
            state.activeModule = activeModule;
        }
    },
    getters: {
        activeModule: state => state.activeModule
    }
});
