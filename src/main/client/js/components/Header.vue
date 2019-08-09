<template>
    <div class="header">
        <h1>Tweetalyzer - {{activeModule}}</h1>
    </div>
</template>

<script>
export default {
    computed: {
        activeModule: function () {
            return this.$store.getters.activeModule
        }
    }
}
</script>

<style lang="scss" scoped="true">
    .header {
        h1 {
            margin: 0;
        }
    }
</style>
