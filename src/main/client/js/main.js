import Vue from 'vue'
import App from './App.vue'
import store from './stores/store';
import router from './routers/router';

new Vue({
    el: '#app',
    store,
    router,
    render: h => h(App)
});
