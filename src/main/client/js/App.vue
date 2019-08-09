<template>
    <div class="app">
        <SideBar/>
        <div class="content-wrapper">
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

export default {
    components: {
        SideBar,
        Header
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
        display: inline-block;
        font-size: 16px;
        height: 100%;
        margin-left: $sidebar-width;
        vertical-align: top;
        width: calc(100% - #{$sidebar-width});
    }
</style>