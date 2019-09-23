<template>
    <div class="job-info">
        <transition name="job-info__animation" mode="out-in">
            <span :key="readableJobName" class="job-info__name">{{readableJobName}}</span>
        </transition>
        <transition name="job-info__animation" mode="out-in">
            <span :key="description" class="job-info__description">{{description}}</span>
        </transition>
        <transition name="job-info__animation" mode="out-in">
            <ProgressBar :key="completion" class="job-info__progress" :completion="completion"/>
        </transition>
        <transition name="job-info__animation" mode="out-in">
            <span :key="status" class="job-info__status">{{status}}</span>
        </transition>
    </div>
</template>

<script>
import ProgressBar from "./ProgressBar";

export default {
    components: {ProgressBar},
    props: {
        readableJobName: String,
        description: String,
        status: String,
        completion: Number
    }
}
</script>

<style lang="scss" scoped="true">
    @import "../../css/mixins";

    .job-info {
        text-align: left;
        width: 100%;

        @include vertical-anchor();

        &__name {
            display: inline-block;
            vertical-align: middle;
            width: 23%;
        }

        &__description {
            display: inline-block;
            vertical-align: middle;
            width: 28%;
        }

        &__progress {
            display: inline-block;
            vertical-align: middle;
            width: 37%;
        }

        &__status {
            display: inline-block;
            vertical-align: middle;
            width: 10%;
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
