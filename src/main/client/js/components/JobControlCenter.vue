<template>
    <div class="job-control-center">
        <div class="job" v-for="job in jobs" :key="job.jobName">
            <JobInfo class="job__info" :readableJobName="job.readableJobName" :description="job.description"
                     :status="job.status" :completion="job.completion"/>
            <JobControl class="job__control" :disabled="job.status === 'running'" :jobName="job.jobName"/>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import JobInfo from "./JobInfo";
import JobControl from "./JobControl";

export default {
    components: {JobControl, JobInfo},
    data() {
        return {
            jobNames: ['IMPORT_GRAPH_USERS_JOB', 'IMPORT_GRAPH_TWEETS_JOB', 'IMPORT_GRAPH_RETWEETS_JOB', 'IMPORT_GRAPH_REPLIES_JOB', 'RAW_DATA_IMPORT_JOB', 'IMPORT_GRAPH_QUOTES_JOB'],
            jobs: []
        }
    },
    mounted() {
        this.fetchJobInfo();

        setInterval(this.fetchJobInfo, 3000);
    },
    methods: {
        fetchJobInfo() {
            const requests = [];
            this.jobNames.forEach((jobName) => {
                requests.push(axios.get(`/admin/job/${jobName}`));
            });

            Promise.all(requests)
                .then((responses) => {
                    let successful = true;
                    const newJobs = [];
                    responses.forEach((response => {
                        if (response.status !== 200) {
                            successful = false;
                            console.error(`bad response from job info api: ${response.status} body: ${response.data}`);
                        }
                        newJobs.push({
                            jobName: response.data.jobName,
                            readableJobName: response.data.readableJobName,
                            description: response.data.description,
                            status: response.data.jobStatus,
                            completion: response.data.completion
                        })
                    }));
                    if (successful) {
                        this.jobs = newJobs;
                    }
                })
                .catch((error) => {
                    console.error(error);
                    this.$store.commit('notificationCenter/addNotification', {
                        id: `jobControl--jobInfo--${jobName}`,
                        status: 'ERROR',
                        message: `There was an error accessing the job info endpoint for ${jobName}. Further information in the browser logs.`
                    })
                });

        }
    }
}
</script>

<style lang="scss" scoped="true">
    .job {
        border: 1px solid black;
        margin: 4px;
        text-align: center;
        width: calc(100% - 10px);

        &__info {
            padding: 8px;
        }

        &__control {
            padding: 8px;
        }
    }
</style>
