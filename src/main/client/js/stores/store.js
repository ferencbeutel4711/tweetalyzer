import Vue from 'vue'
import Vuex from 'vuex'

import NotificationCenterModule from "./NotificationCenterModule";
import SideBarModule from "./SideBarModule";
import SettingsModule from "./SettingsModule";

Vue.use(Vuex);

export default new Vuex.Store({
    modules: {
        notificationCenter: NotificationCenterModule,
        sideBar: SideBarModule,
        settings: SettingsModule
    }
});
