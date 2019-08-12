<template>
    <div class="settings">
        <p>Here you can configure the application to your desire. Please note that the settings are stored in the local
            storage of your browser, so once that is cleared, the settings will revert back to the default.</p>
        <div class="import-control">
        </div>
    </div>
</template>

<script>
export default {
    mounted: function () {
        this.$store.commit('sideBar/changeActiveModule', 'Settings');
    }
}
</script>

<style lang="scss" scoped="true">

</style>
