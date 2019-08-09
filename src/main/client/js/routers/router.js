import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from '../components/Home';
import Settings from '../components/Settings';

Vue.use(VueRouter);

const routes = [
    {path: '/', component: Home},
    {path: '/settings', component: Settings}
];

export default new VueRouter({mode: 'history', routes});
