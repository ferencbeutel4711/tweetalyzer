<template>
    <div class="job-info" v-if="enriched">
        <span>{{readableJobName}}</span>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    props: {
        jobName: String,
        postEndpoint: String,
        readableJobName: String,
        description: String,
        status: String,
        completion: Number
    },
    data() {
        return {
            enriched: false
        }
    },
    mounted() {
        axios.get(`/admin/job/${this.jobName}`)
            .then((response) => {
                console.log(response);
                this.enriched = true;
            })
            .catch((error) => {
                if(error.response && error.response.status === 404) {

                }
                console.log(error);
                this.$store.commit('notificationCenter/addNotification', {
                    id: `settings_jobInfo--${this.jobName}`,
                    status: 'ERROR',
                    message: `There was an error accessing the job info endpoint for ${this.jobName}. Further information in the browser logs.`
                })
            })
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
