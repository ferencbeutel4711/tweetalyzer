<template>
    <h2>Home</h2>
</template>

<script>
export default {
    mounted: function () {
        this.$store.commit('changeActiveModule', 'Settings')
    }
}
</script>

<style lang="scss" scoped="true">

</style>
