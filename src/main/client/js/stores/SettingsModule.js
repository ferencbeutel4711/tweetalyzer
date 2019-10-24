export default {
    namespaced: true,
    state: {
        debugEnabled: false
    },
    mutations: {
        toggleDebug(state) {
            state.debugEnabled = !state.debugEnabled;
        },
    },
    getters: {
        isDebugEnabled: state => state.debugEnabled
    }
};
