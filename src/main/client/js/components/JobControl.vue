<template>
    <div class="job-control">
        <button :disabled="isDisabled" v-on:click="startJob">Start</button>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    props: {
        jobName: String,
        disabled: Boolean
    },
    data() {
        return {
            isDisabled: this.disabled
        }
    },
    methods: {
        startJob() {
            this.isDisabled = true;
            axios.post(`/admin/job/${this.jobName}/start`)
                .then((response) => {
                    if (response.status !== 200) {
                        console.error(`error during job starting: ${response.status} body: ${response.data}`);
                        if (response.status === 423) {
                            this.$store.commit('notificationCenter/addNotification', {
                                id: `job-control__status--${this.jobName}`,
                                status: 'ERROR',
                                message: `The Job ${this.jobName} is already running`
                            });
                        } else if (response.status === 409) {
                            this.$store.commit('notificationCenter/addNotification', {
                                id: `job-control__status--${this.jobName}`,
                                status: 'ERROR',
                                message: `Another Job is running that has to run mutually exclusive to ${this.jobName}`
                            });
                        } else {
                            this.$store.commit('notificationCenter/addNotification', {
                                id: `job-control__status--${this.jobName}`,
                                status: 'ERROR',
                                message: `The Job with the id ${this.jobName} could not be started: unknown error`
                            });
                        }
                    }

                    this.$store.commit('notificationCenter/addNotification', {
                        id: `job-control__status--${this.jobName}`,
                        status: 'OK',
                        message: `The Job with the id "${this.jobName}" is started successfully`
                    });
                })
                .catch((error) => {
                    console.error('error during requesting the job starting api: ' + error.toString());
                    this.$store.commit('notificationCenter/addNotification', {
                        id: `job-control__status--${this.jobName}`,
                        status: 'ERROR',
                        message: `There was an error while requesting of the job starting endpoint for ${this.jobName}`
                    });
                })
        }
    }
}
</script>

<style lang="scss" scoped="true">
</style>
