import Vue from 'vue'
import Vuex from 'vuex'
import uuidv1 from 'uuid/v1'

Vue.use(Vuex);

const notificationCenterModule = {
    namespaced: true,
    state: {
        notifications: {}
    },
    mutations: {
        addNotification(state, notification) {
            Vue.set(state.notifications, notification.id, notification);
        },
        deleteNotification(state, id) {
            Vue.delete(state.notifications, id);
        }
    },
    getters: {
        isActive: state => Object.keys(state.notifications).length > 0,
        notification: state => notificationId => state.notifications[notificationId],
        notifications: state => state.notifications
    },
    actions: {
        publishNotification(context, {status, message}) {
            context.commit('addNotification', {
                id: uuidv1(),
                status,
                message
            });
        }
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
