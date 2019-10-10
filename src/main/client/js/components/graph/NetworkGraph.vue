<template>
    <svg :id="id" :width="`${width}px`" :height="`${height}px`" :key="key" @mousemove="drag($event)" @mouseup="drop"
         style="background-color: rgb(200,200,200)">
        <line v-for="link in links"
              :x1="link.source.x"
              :y1="link.source.y"
              :x2="link.target.x"
              :y2="link.target.y"
              :stroke="colorForLink(link)" stroke-width="1"/>

        <circle v-for="(node) in nodes" @mousedown="startDrag(node)"
                :cx="node.x"
                :cy="node.y"
                :r="node.size"
                :fill="node.color"/>
    </svg>
</template>

<script>
import * as d3 from 'd3'

export default {
    components: {},
    props: {
        id: String,
        width: Number,
        height: Number,
        initialNodes: Array,
        initialLinks: Array,
        chargeForce: Number,
        linkForce: Number
    },
    computed: {
        key() {
            return this.hashNetwork(this.nodes, this.links);
        }
    },
    data() {
        const transformedNodes = this.initialNodes
            .map((node) => ({id: node.id, type: node.type, x: 400, y: 400, size: node.size, color: node.color}));
        return {
            nodes: transformedNodes,
            links: this.initialLinks
                .map((link) => {
                    let source = null;
                    let target = null;

                    const filteredSources = transformedNodes.filter((node) => link.source === node.id);
                    if (filteredSources.length > 0) {
                        source = filteredSources[0];
                    }

                    const filteredTargets = transformedNodes.filter((node) => link.target === node.id);
                    if (filteredTargets.length > 0) {
                        target = filteredTargets[0];
                    }

                    return source !== null && target !== null ? {
                        source: source,
                        target: target,
                        type: link.type
                    } : null;
                })
                .filter((link) => link !== null),
            graphSimulation: null,
            selectedNode: null
        }
    },
    mounted() {
        this.$store.commit('sideBar/changeActiveModule', 'Graph');

        this.graphSimulation = d3.forceSimulation(this.nodes)
            .force('charge', d3.forceManyBody().strength(this.chargeForce))
            .force('link', d3.forceLink(this.links).strength(this.linkForce))
            .force('center', d3.forceCenter(this.width / 2, this.height / 2))
            .force('bounds', this.boundingForce);
    },
    methods: {
        boundingForce() {
            const padding = 20;
            this.nodes.forEach(node => {
                if (node.x + node.size + padding > this.width) {
                    node.x = this.width - node.size - padding;
                } else if (node.x - node.size - padding < 0) {
                    node.x = node.size + padding;
                }
                if (node.y + node.size + padding > this.height) {
                    node.y = this.height - node.size - padding;
                } else if (node.y - node.size - padding < 0) {
                    node.y = node.size + padding;
                }
            })
        },
        colorForLink(link) {
            switch (link.type) {
                case "TWEETS":
                    return "blue";
                case "RETWEETS":
                    return "green";
                case "MENTIONS":
                    return "red";
                case "REPLIES_TO":
                    return "yellow";
                default:
                    return "black";
            }
        },
        startDrag(node) {
            if (this.selectedNode === null) {
                this.selectedNode = node;
                this.graphSimulation.alphaTarget(0.3).restart();
            }
        },
        drag(e) {
            if (this.selectedNode) {
                const svg = document.getElementById(this.id);
                this.selectedNode.fx = e.clientX - svg.getBoundingClientRect().left;
                this.selectedNode.fy = e.clientY - svg.getBoundingClientRect().top;
            }
        },
        drop() {
            if (this.selectedNode) {
                this.selectedNode = null;
                this.graphSimulation.alphaTarget(0);
                this.graphSimulation.alphaDecay(0.1);
            }
        },
        hashNetwork(nodes, links) {
            const mergedString =
                `${nodes.map(node => JSON.stringify(node.id)).join('')}|${links
                    .map(link => `${JSON.stringify(link.source)}-${JSON.stringify(link.target)}`).join('')}`;

            let hash = 0;
            if (mergedString.length === 0) {
                return hash;
            }
            for (let i = 0; i < mergedString.length; i++) {
                const char = mergedString.charCodeAt(i);
                hash = ((hash << 5) - hash) + char;
                hash = hash & hash;
            }

            return hash;
        }
    }
}
</script>

<style lang="scss" scoped="true">
</style>
