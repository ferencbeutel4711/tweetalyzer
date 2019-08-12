import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex);

const notificationCenterModule = {
    namespaced: true,
    state: {
        notifications: {}
    },
    mutations: {
        addNotification(state, {id, status, message}) {
            const existingNotification = state.notifications[id];
            if (existingNotification) {
                state.notifications[id] = {
                    id,
                    status,
                    message,
                    count: existingNotification.count + 1
                }
            } else {
                Vue.set(state.notifications, id, {
                    id,
                    status,
                    message,
                    count: 1
                });
            }
        },
        deleteNotification(state, id) {
            Vue.delete(state.notifications, id);
        }
    },
    getters: {
        notification: state => notificationId => state.notifications[notificationId],
        notifications: state => state.notifications
    }
};

const sideBarModule = {
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

export default new Vuex.Store({
    modules: {
        notificationCenter: notificationCenterModule,
        sideBar: sideBarModule
    }
});
