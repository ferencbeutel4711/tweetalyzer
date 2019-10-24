<template>
    <div class="graph">
        <h1>Graph</h1>
        <div>
            <button v-on:click="resetNetwork">Reset Component</button>
            <input v-model.number="limit" placeholder="limit">
            <input v-model.number="force.charge" placeholder="charge">
            <input v-model.number="force.link" placeholder="link">
            <br/>
            <input v-model="search.hashtag">
            <input v-model="search.username">
            <br/>
            <button @click="zoomIn()">zoom in</button>
            <button @click="zoomOut()">zoom out</button>
            <br/>
            <button v-on:click="updateData">Search</button>
        </div>
        <NetworkGraph id="network" :width="775" :height="775" :initial-nodes="nodes" :initial-links="links"
                      :charge-force="force.charge" :link-force="force.link" v-if="displayNetwork"
                      :zoom-factor="zoomFactor"/>
    </div>
</template>

<script>
import axios from 'axios';

import NetworkGraph from "./graph/NetworkGraph";
import {groupBy} from "../util/streaming";
import {colorFromBlueToYellow, colorFromRedToGreen} from "../util/colorPickers";
import {percentageInRange} from "../util/math";

export default {
    components: {NetworkGraph},
    computed: {
        trimmedHashtag() {
            return this.search.hashtag != null ? this.search.hashtag.trim().replace('#', '') : null;
        }
    },
    data() {
        return {
            nodes: [],
            links: [],
            force: {
                charge: -1,
                link: 2
            },
            limit: 100,
            displayNetwork: false,
            userToggled: true,
            search: {
                hashtag: null,
                username: null
            },
            zoomFactor: 1
        }
    },
    watch: {
        limit() {
            this.updateData();
        },
        force: {
            handler() {
                this.resetNetwork();
            },
            deep: true
        }
    },
    mounted() {
        this.updateData();
    },
    methods: {
        zoomIn() {
            this.zoomFactor = this.zoomFactor * 0.5;
        },
        zoomOut() {
            this.zoomFactor = this.zoomFactor / 0.5;
        },
        updateData() {
            this.displayNetwork = false;
            let hashtagAppendix = this.trimmedHashtag !== null && this.trimmedHashtag !== '' ? `&hashtag=${this.trimmedHashtag}` : '';
            const usernameAppendix = this.search.username !== null && this.search.username !== '' ?
                `&username=${this.search.username}` :
                '';
            axios.get(`/graph/user?limit=${this.limit}${hashtagAppendix}${usernameAppendix}`)
                .then((response) => {
                    if (response.status !== 200) {
                        console.error(`bad response from graph api: ${response.status} body: ${response.data}`);
                        this.$store.commit('notificationCenter/addNotification', {
                            id: `graph--fetch-network`,
                            status: 'ERROR',
                            message: `There was a bad response from the graph endpoint. Further information in the browser logs.`
                        })
                    } else {
                        const tweetsIndexBySource = groupBy(response.data.relationships.tweets, rs => rs.source);
                        const minTweets = Math.min.apply(Math, Array.from(tweetsIndexBySource.values()).map((o) => o.length));
                        const maxTweets = Math.max.apply(Math, Array.from(tweetsIndexBySource.values()).map((o) => o.length));

                        const reTweetsIndexBySource = groupBy(response.data.relationships.reTweets, rs => rs.source);
                        const minReTweets = Math.min.apply(Math, Array.from(reTweetsIndexBySource.values()).map((o) => o.length));
                        const maxReTweets = Math.max.apply(Math, Array.from(reTweetsIndexBySource.values()).map((o) => o.length));

                        const mentionsIndexBySource = groupBy(response.data.relationships.mentions, rs => rs.source);
                        const maxMentions = Math.max.apply(Math, Array.from(mentionsIndexBySource.values()).map((o) => o.length));
                        const minMentions = Math.min.apply(Math, Array.from(mentionsIndexBySource.values()).map((o) => o.length));

                        const repliesIndexBySource = groupBy(response.data.relationships.replies, rs => rs.source);
                        const maxReplies = Math.max.apply(Math, Array.from(repliesIndexBySource.values()).map((o) => o.length));
                        const minReplies = Math.min.apply(Math, Array.from(repliesIndexBySource.values()).map((o) => o.length));

                        const userNodes = response.data.nodes.users.map((user) => {
                            const tweets = tweetsIndexBySource.get(user.id);
                            const reTweets = reTweetsIndexBySource.get(user.id);
                            user.size = Math.max(8, percentageInRange(tweets ? tweets.length : 0, minTweets, maxTweets) * 20);
                            user.color = colorFromRedToGreen(reTweets ? reTweets.length : 0, minReTweets, maxReTweets);

                            return user;
                        });

                        const tweetNodes = response.data.nodes.tweets.map((tweet) => {
                            const mentions = mentionsIndexBySource.get(tweet.id);
                            const replies = repliesIndexBySource.get(tweet.id);
                            tweet.size = Math.max(5, percentageInRange(mentions ? mentions.length : 0, minMentions, maxMentions) * 10);
                            tweet.color = colorFromBlueToYellow(replies ? replies.length : 0, minReplies, maxReplies);

                            return tweet;
                        });

                        this.nodes = userNodes.concat(tweetNodes);
                        this.links = response.data.relationships.tweets
                            .concat(response.data.relationships.reTweets)
                            .concat(response.data.relationships.mentions)
                            .concat(response.data.relationships.replies);
                        this.displayNetwork = true;
                    }
                })
                .catch((error) => {
                    console.error(error);
                    this.$store.commit('notificationCenter/addNotification', {
                        id: `graph--fetch-network`,
                        status: 'ERROR',
                        message: `There was an error accessing the graph endpoint. Further information in the browser logs.`
                    })
                });
        },
        resetNetwork() {
            this.displayNetwork = false;
            this.$nextTick().then(() => {
                this.displayNetwork = true;
            })
        }
    }
}
</script>

<style lang="scss" scoped="true">
</style>
