import Vue from 'vue'

export default {
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
