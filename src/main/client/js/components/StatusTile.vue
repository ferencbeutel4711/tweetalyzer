<template>
    <div class="status-tile">
        <div class="status-tile__content">
            <span class="status-tile__description">{{description}}</span>
            <br/>
            <transition name="status-tile__animation" mode="out-in">
                <span :key="id">{{value}}</span>
            </transition>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        description: String,
        value: String,
        id: Number
    }
}
</script>

<style lang="scss" scoped="true">
    @import "../../css/mixins";

    .status-tile {
        border: 1px solid;
        display: inline-block;
        height: 100px;
        margin: 8px;
        width: 400px;
        @include vertical-anchor();

        &__content {
            display: inline-block;
            text-align: center;
            vertical-align: middle;
        }

        &__description {
            font-size: 12px;
            line-height: 12px;
        }

        span {
            margin: 0;
            vertical-align: middle;
        }

        &__animation {
            &-enter {
                opacity: 0;
            }

            &-enter-active {
                transition: opacity 1s ease-out;
            }

            &-enter-to {
                opacity: 1;
            }

            &-leave-active {
                visibility: hidden;
            }
        }
    }
</style>
