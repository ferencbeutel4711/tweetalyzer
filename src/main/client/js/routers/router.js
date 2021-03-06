import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from '../components/Home';
import Graph from '../components/Graph';
import Settings from '../components/Settings';

Vue.use(VueRouter);

const routes = [
    {path: '/', component: Home},
    {path: '/graph', component: Graph},
    {path: '/settings', component: Settings}
];

export default new VueRouter({routes});
