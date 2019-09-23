<template>
    <div class="progress-bar">
        <div class="progress-bar__bar" :style="{'--progress': completion}"></div>
    </div>
</template>

<script>
export default {
    props: {
        completion: Number,
    }
}
</script>

<style lang="scss" scoped="true">
    .progress-bar {
        background-color: white;
        border: 1px solid black;
        height: 20px;
        width: 100%;

        &__bar {
            background-color: green;
            height: 100%;
            width: calc(var(--progress) * 100%);
        }
    }
</style>
