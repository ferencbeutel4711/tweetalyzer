<template>
    <div class="app">
        <SideBar/>
        <div class="content-wrapper">
            <NotificationCenter/>
            <div class="content">
                <Header/>
                <router-view/>
            </div>
        </div>
    </div>
</template>

<script>
import Header from './components/Header';
import SideBar from "./components/SideBar";
import NotificationCenter from "./components/NotificationCenter";

export default {
    components: {
        NotificationCenter,
        SideBar,
        Header
    },
    mounted: function () {

    }
}
</script>

<style scoped="true" lang="scss">
    $sidebar-width: 100px;
    $content-padding: 16px;

    .app {
        font-family: 'IBM Plex Serif', serif;
        font-size: 0;
        height: 100%;
    }

    .content {
        padding: $content-padding;
    }

    .content-wrapper {
        background-color: rgb(240, 240, 240);
        box-shadow: 0 0 6px rgb(180, 180, 180);
        font-size: 16px;
        min-height: 100%;
        margin: 0 auto;
        max-width: 1000px;
        vertical-align: top;
        width: calc(100% - #{$sidebar-width} * 2);
    }
</style>

