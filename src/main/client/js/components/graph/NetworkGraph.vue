<template>
    <svg :id="id" :width="`${width}px`" :height="`${height}px`" :key="key" @mousemove="drag($event)" @mouseup="drop"
         style="background-color: white">
        <line v-for="link in links"
              :x1="link.source.x"
              :y1="link.source.y"
              :x2="link.target.x"
              :y2="link.target.y"
              stroke="black" stroke-width="1"/>

        <circle v-for="(node) in nodes" @mousedown="startDrag(node)"
                :cx="node.x"
                :cy="node.y"
                :r="2"
                fill="red"/>
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
        nodes: Array,
        links: Array
    },
    computed: {
        key() {
            return this.hashNetwork(this.nodes, this.links);
        }
    },
    data() {
        return {
            graphSimulation: null,
            selectedNode: null
        }
    },
    mounted() {
        this.$store.commit('sideBar/changeActiveModule', 'Graph');

        this.graphSimulation = d3.forceSimulation(this.nodes)
            .force('charge', d3.forceManyBody().strength(-10))
            .force('link', d3.forceLink(this.links))
            .force('center', d3.forceCenter(this.width / 2, this.height / 2));
    },
    methods: {
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
                `${nodes.map(node => JSON.stringify(node.index)).join('')}|${links
                    .map(link => `${JSON.stringify(link.source.index)}-${JSON.stringify(link.target.index)}`).join('')}`;

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
