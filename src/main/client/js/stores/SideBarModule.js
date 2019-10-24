export default {
    namespaced: true,
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
};
