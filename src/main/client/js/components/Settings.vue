<template>
    <div class="settings">
        <p>Here you can configure the application to your desire. Please note that the settings are stored in the local
            storage of your browser, so once that is cleared, the settings will revert back to the default.</p>
        <div class="status-monitor">
            <h2>Application Status</h2>
            <h3>RawData</h3>
            <div class="status-tiles">
                <StatusTile v-for="statusTile in rawDataStatusTiles" :description="statusTile.description"
                            :value="statusTile.value" :key="statusTile.key" :id="statusTile.id"/>
            </div>
            <h3>Graph</h3>
            <div class="status-tiles">
                <StatusTile v-for="statusTile in graphStatusTiles" :description="statusTile.description"
                            :value="statusTile.value" :key="statusTile.key" :id="statusTile.id"/>
            </div>
        </div>
        <div class="job-control">
            <JobInfo v-for="job in jobs" :jobName="job.jobName" :key="job.jobName"
                     :readableJobName="job.readableJobName" :postEndpoint="job.postEndpoint"
                     :description="job.description" :status="job.status" :completion="job.completion"/>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import StatusTile from "./StatusTile";
import JobInfo from "./JobInfo";

export default {
    components: {StatusTile, JobInfo},
    data() {
        return {
            intervalId: null,
            rawDataStatusTiles: [
                {
                    key: 'rawDataUser',
                    id: Date.now(),
                    description: 'This value shows the amount of users extracted from the provided dataset.',
                    value: `Raw Users: calculating`
                },
                {
                    key: 'rawDataTweet',
                    id: Date.now(),
                    description: 'This value shows the amount of tweets extracted from the provided dataset.',
                    value: `Raw Tweets: calculating`
                },
                {
                    key: 'rawDataRetweet',
                    id: Date.now(),
                    description: 'This value shows the amount of retweets extracted from the provided dataset.',
                    value: `Raw Retweets: calculating`
                },
                {
                    key: 'rawDataReply',
                    id: Date.now(),
                    description: 'This value shows the amount of replies extracted from the provided dataset.',
                    value: `Raw Replies: calculating`
                },
                {
                    key: 'rawDataQuote',
                    id: Date.now(),
                    description: 'This value shows the amount of quotes extracted from the provided dataset.',
                    value: `Raw Quotes: calculating`
                }
            ],
            graphStatusTiles: [
                {
                    key: 'graphUserNode',
                    id: Date.now(),
                    description: 'This value shows the amount of user nodes in the graph database.',
                    value: `Graph User Nodes: calculating`
                },
                {
                    key: 'graphTweetNode',
                    id: Date.now(),
                    description: 'This value shows the amount of tweet nodes in the graph database.',
                    value: `Graph Tweet Nodes: calculating`
                },
                {
                    key: 'graphRepliesToRelCount',
                    id: Date.now(),
                    description: 'This value shows the amount of replies_to relationships in the graph database.',
                    value: `Graph Tweet Nodes: calculating`
                },
                {
                    key: 'graphMentionsRelCount',
                    id: Date.now(),
                    description: 'This value shows the amount of mentions relationships in the graph database.',
                    value: `Graph Tweet Nodes: calculating`
                },
                {
                    key: 'graphTweetsRelCount',
                    id: Date.now(),
                    description: 'This value shows the amount of tweets relationships in the graph database.',
                    value: `Graph Tweet Nodes: calculating`
                },
                {
                    key: 'graphRetweetsRelCount',
                    id: Date.now(),
                    description: 'This value shows the amount of retweets relationships in the graph database.',
                    value: `Graph Retweet Nodes: calculating`
                }
            ],
            jobs: [
                {
                    jobName: 'IMPORT_GRAPH_USERS_JOB',
                    postEndpoint: '/admin/import/graph/user/start',
                    readableJobName: '',
                    description: '',
                    status: '',
                    completion: 0
                },
                {
                    jobName: 'IMPORT_GRAPH_TWEETS_JOB',
                    postEndpoint: '/admin/import/graph/tweet/start',
                    readableJobName: '',
                    description: '',
                    status: '',
                    completion: 0
                },
                {
                    jobName: 'IMPORT_GRAPH_RETWEETS_JOB',
                    postEndpoint: '/admin/import/graph/reTweet/start',
                    readableJobName: '',
                    description: '',
                    status: '',
                    completion: 0
                },
                {
                    jobName: 'IMPORT_GRAPH_REPLIES_JOB',
                    postEndpoint: '/admin/import/graph/reply/start',
                    readableJobName: '',
                    description: '',
                    status: '',
                    completion: 0
                }
            ]
        }
    },
    mounted() {
        this.$store.commit('sideBar/changeActiveModule', 'Settings');
        this.fetchStatusEndpoints();
        this.intervalId = setInterval(this.fetchStatusEndpoints, 5000);
    },
    beforeDestroy() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
    },
    methods: {
        zeroValueCheck(objectToCheck) {
            Object.keys(objectToCheck).forEach(key => {
                if (objectToCheck[key] === 0) {
                    this.$store.commit('notificationCenter/addNotification', {
                        id: `settings_zeroValueCheck--${key}`,
                        status: 'ERROR',
                        message: 'The extracted ' + key + " is 0! Please provide a valid dataset to the application."
                    });
                }
            })
        },
        requestStatus({url, context}) {
            return new Promise((resolve) => {
                axios.get(url)
                    .then((response) => {
                        if (response.status !== 200) {
                            console.log('bad response: ' + response);
                            this.$store.commit('notificationCenter/addNotification', {
                                id: `settings_badResponse--${context}`,
                                status: 'ERROR',
                                message: `The ${context} status endpoint returned a bad response. Further information in the browser logs.`
                            })
                        } else {
                            this.zeroValueCheck(response.data);
                            resolve(response.data);
                        }
                    })
                    .catch((error) => {
                        console.log(error);
                        this.$store.commit('notificationCenter/addNotification', {
                            id: `settings_accessError--${context}`,
                            status: 'ERROR',
                            message: `There was an error accessing the ${context} status endpoint. Further information in the browser logs.`
                        })
                    })
            })
        },
        fetchGraphStatus() {
            this.requestStatus({url: '/admin/status/graph', context: 'graph'})
                .then(data => {
                    this.graphStatusTiles = [
                        {
                            key: 'graphUserNode',
                            id: Date.now(),
                            description: 'This value shows the amount of user nodes in the graph database.',
                            value: `Graph User Nodes: ${data.userNodeCount}`
                        },
                        {
                            key: 'graphTweetNode',
                            id: Date.now(),
                            description: 'This value shows the amount of tweet nodes in the graph database.',
                            value: `Graph Tweet Nodes: ${data.tweetNodeCount}`
                        },
                        {
                            key: 'graphRepliesToRelCount',
                            id: Date.now(),
                            description: 'This value shows the amount of replies_to relationships in the graph database.',
                            value: `Graph Tweet Nodes: ${data.repliesToRelCount}`
                        },
                        {
                            key: 'graphMentionsRelCount',
                            id: Date.now(),
                            description: 'This value shows the amount of mentions relationships in the graph database.',
                            value: `Graph Tweet Nodes: ${data.mentionsRelCount}`
                        },
                        {
                            key: 'graphTweetsRelCount',
                            id: Date.now(),
                            description: 'This value shows the amount of tweets relationships in the graph database.',
                            value: `Graph Tweet Nodes: ${data.tweetsRelCount}`
                        },
                        {
                            key: 'graphRetweetsRelCount',
                            id: Date.now(),
                            description: 'This value shows the amount of retweets relationships in the graph database.',
                            value: `Graph Retweet Nodes: ${data.retweetsRelCount}`
                        }
                    ]
                });
        },
        fetchRawDataStatus() {
            this.requestStatus({url: '/admin/status/rawData', context: 'rawData'})
                .then(data => {
                    this.rawDataStatusTiles = [
                        {
                            key: 'rawDataUser',
                            id: Date.now(),
                            description: 'This value shows the amount of users extracted from the provided dataset.',
                            value: `Raw Users: ${data.userCount}`
                        },
                        {
                            key: 'rawDataTweet',
                            id: Date.now(),
                            description: 'This value shows the amount of tweets extracted from the provided dataset.',
                            value: `Raw Tweets: ${data.statusCount}`
                        },
                        {
                            key: 'rawDataRetweet',
                            id: Date.now(),
                            description: 'This value shows the amount of retweets extracted from the provided dataset.',
                            value: `Raw Retweets: ${data.retweetCount}`
                        },
                        {
                            key: 'rawDataReply',
                            id: Date.now(),
                            description: 'This value shows the amount of replies extracted from the provided dataset.',
                            value: `Raw Replies: ${data.replyCount}`
                        },
                        {
                            key: 'rawDataQuote',
                            id: Date.now(),
                            description: 'This value shows the amount of quotes extracted from the provided dataset.',
                            value: `Raw Quotes: ${data.quoteCount}`
                        }
                    ]
                });
        },
        fetchStatusEndpoints() {
            this.fetchRawDataStatus();
            this.fetchGraphStatus();
        }
    }
}
</script>

<style lang="scss" scoped="true">
    @import "../../css/mixins";

    .status-tiles {
    }

    .status-tile {
        border: 1px solid;
        display: inline-block;
        height: 100px;
        margin: 4px;
        padding: 4px;
        text-align: center;
        width: calc(100% / 3 - 18px);
        @include vertical-anchor();

        &__content {
            display: inline-block;
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
