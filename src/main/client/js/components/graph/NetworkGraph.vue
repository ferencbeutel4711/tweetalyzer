<template>
    <div>
        <svg :viewBox="`${viewBox.minX} ${viewBox.minY} ${viewBox.w} ${viewBox.h}`"
             :id="id" :width="`${width}px`" :height="`${height}px`" :key="key" @mousemove="drag($event)"
             @mouseup="drop" @mouseleave="drop" style="background-color: rgb(200,200,200)">
            <rect :x="0" :y="0" :width="viewBox.w" :height="viewBox.h" fill="rgba(255,0,0,0.2)" v-if="debugEnabled"/>
            <line v-for="link in links"
                  :x1="link.source.x"
                  :y1="link.source.y"
                  :x2="link.target.x"
                  :y2="link.target.y"
                  :stroke="colorForLink(link)" stroke-width="1"/>

            <circle v-for="node in nodes" @mousedown="startDrag(node)" @mouseover="displayLabel(node)"
                    @mouseleave="hideLabel"
                    :cx="node.x"
                    :cy="node.y"
                    :r="node.size"
                    :fill="colorForNode(node)"
                    stroke="black"
                    stroke-width="1"/>
            <g :id="node.id" v-for="node in nodes" :visibility="isVisible(node)">
                <rect :x="node.x > width / 2 ? node.x - (labelWidth * zoomFactor + 20) : node.x + 20"
                      :y="node.y < height / 2 ? node.y - 10 : node.y - (labelHeight * zoomFactor + 10)"
                      :width="labelWidth * zoomFactor" :height="labelHeight * zoomFactor" fill="rgba(255,255,255,0.4)">
                </rect>
                <foreignObject :height="labelHeight * zoomFactor" :width="labelWidth * zoomFactor"
                               :x="node.x > width / 2 ? node.x - (labelWidth * zoomFactor + 20) : node.x + 20"
                               :y="node.y < height / 2 ? node.y - 10 : node.y - (labelHeight * zoomFactor + 10)">
                    <ul :style="{paddingLeft: labelPadding, marginRight: labelMargin}">
                        <li :style="{fontSize: labelFontSize, lineHeight: labelFontSize}"
                            v-for="labelAttribute in getLabelAttributes(node)">
                            {{labelAttribute.name}}:
                            {{labelAttribute.text}}
                        </li>
                    </ul>
                </foreignObject>
            </g>
        </svg>
    </div>
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
        linkForce: Number,
        zoomFactor: Number
    },
    computed: {
        labelFontSize() {
            return `${10 * this.zoomFactor}px`;
        },
        labelMargin() {
            return `${16 * this.zoomFactor}px`;
        },
        labelPadding() {
            return `${20 * this.zoomFactor}px`;
        },
        key() {
            return this.hashNetwork(this.nodes, this.links);
        },
        debugEnabled() {
            return this.$store.getters['settings/isDebugEnabled'];
        }
    },
    watch: {
        zoomFactor() {
            this.zoom();
        },
    },
    data() {
        const transformedNodes = this.initialNodes
            .map((node) => {
                const baseNode = {
                    id: node.id,
                    type: node.type,
                    x: 400,
                    y: 400,
                    size: node.size,
                    color: node.color
                };

                switch (node.type) {
                    case "USER":
                        baseNode.name = node.name;
                        break;
                    case "TWEET":
                        baseNode.text = node.text;
                }

                return baseNode;
            });
        const viewBoxWidth = this.width * this.zoomFactor;
        const viewBoxHeight = this.height * this.zoomFactor;
        return {
            viewBox: {
                minX: this.zoomFactor === 1 ? 0 : (this.width - viewBoxWidth) / 2,
                minY: this.zoomFactor === 1 ? 0 : (this.height - viewBoxHeight) / 2,
                w: viewBoxWidth,
                h: viewBoxHeight
            },
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
            selectedNode: null,
            hoveredNode: null,
            labelWidth: 300,
            labelHeight: 150,
            hoverEnabled: true
        }
    },
    mounted() {
        if(this.debugEnabled) {
            console.log(`drawing ${this.initialNodes.length} nodes with ${this.initialLinks.length} relationships`);
        }
        this.$store.commit('sideBar/changeActiveModule', 'Graph');

        this.graphSimulation = d3.forceSimulation(this.nodes)
            .alphaDecay(0.1)
            .force('collide', d3.forceCollide().radius((node) => node.size))
            .force('charge', d3.forceManyBody().strength(this.chargeForce))
            .force('link', d3.forceLink(this.links).strength(this.linkForce))
            .force('center', d3.forceCenter(this.width / 2, this.height / 2))
            .force('bounds', this.boundingForce)
            .on('end', () => {
                this.hoverEnabled = true;
            });
    },
    methods: {
        zoom() {
            this.viewBox.w = this.width * this.zoomFactor;
            this.viewBox.h = this.height * this.zoomFactor;
            this.viewBox.minX = (this.width - this.viewBox.w) / 2;
            this.viewBox.minY = (this.height - this.viewBox.h) / 2;
        },
        getLabelAttributes(node) {
            switch (node.type) {
                case "TWEET":
                    return this.getTweetLabelAttributes(node);
                case "USER":
                    return this.getUserLabelAttributes(node);
                default:
                    return [];
            }
        },
        getUserLabelAttributes(node) {
            const labelAttributes = [];
            labelAttributes.push({
                idx: 1,
                name: 'Type',
                text: node.type
            });
            labelAttributes.push({
                idx: 2,
                name: 'Username',
                text: node.name
            });

            return labelAttributes;
        },
        getTweetLabelAttributes(node) {
            const labelAttributes = [];
            labelAttributes.push({
                idx: 1,
                name: 'Type',
                text: node.type
            });
            labelAttributes.push({
                idx: 2,
                name: 'Text',
                text: node.text
            });

            return labelAttributes;
        },
        isVisible(node) {
            return this.hoveredNode !== null && this.hoveredNode.id === node.id ? 'visible' : 'hidden';
        },
        boundingForce() {
            const padding = 20;
            const containingW = this.viewBox.w + this.viewBox.minX;
            const containingH = this.viewBox.h + this.viewBox.minY;
            this.nodes.forEach(node => {
                if (node.x + node.size + padding > containingW) {
                    node.x = containingW - node.size - padding;
                } else if (node.x - node.size - padding < this.viewBox.minX) {
                    node.x = this.viewBox.minX + node.size + padding;
                }
                if (node.y + node.size + padding > containingH) {
                    node.y = containingH - node.size - padding;
                } else if (node.y - node.size - padding < this.viewBox.minY) {
                    node.y = this.viewBox.minY + node.size + padding;
                }
            })
        },
        isLinkedTo(source, target) {
            return this.links
                .find((link) => link.source.id === source.id && link.target.id === target.id || link.source.id === target.id &&
                    link.target.id === source.id) !== undefined;
        },
        colorForNode(node) {
            if (this.hoverEnabled && this.hoveredNode && this.hoveredNode.id !== node.id
                && !this.isLinkedTo(node, this.hoveredNode)) {
                const regex = /rgb\((\d*),(\d*),(\d*)\)/;
                const newColorPattern = 'rgba($1,$2,$3,0.1)';

                return node.color.replace(regex, newColorPattern);
            }

            return node.color;
        },
        colorForLink(link) {
            const muteLinkColor = this.hoverEnabled && this.hoveredNode && this.hoveredNode.id !== link.source.id
                && this.hoveredNode.id !== link.target.id;
            switch (link.type) {
                case "TWEETS":
                    return muteLinkColor ? "rgba(32,191,85,0.1)" : "rgb(32,191,85)";
                case "RETWEETS":
                    return muteLinkColor ? "rgba(11,79,108,0.1)" : "rgb(11,79,108)";
                case "MENTIONS":
                    return muteLinkColor ? "rgba(70,204,240,0.1)" : "rgb(70,204,240)";
                case "REPLIES_TO":
                    return muteLinkColor ? "rgba(225,153,123,0.1)" : "rgb(225,153,123)";
                case "QUOTES":
                    return muteLinkColor ? "rgba(219,213,110,0.1)" : "rgb(219,213,110)";
                default:
                    return muteLinkColor ? "rgba(0,0,0,0.1)" : "rgb(0,0,0)";
            }
        },
        hideLabel() {
            if (this.hoverEnabled) {
                this.hoveredNode = null;
            }
        },
        displayLabel(node) {
            if (this.hoverEnabled) {
                this.hoveredNode = node;
            }
        },
        startDrag(node) {
            this.hoverEnabled = false;
            this.hoveredNode = null;
            if (this.selectedNode === null) {
                this.selectedNode = node;
                this.graphSimulation.alphaTarget(0.1).restart();
            }
        },
        drag(e) {
            if (this.selectedNode) {
                const svg = document.getElementById(this.id);
                this.selectedNode.fx = (e.clientX - svg.getBoundingClientRect().left) * this.zoomFactor + this.viewBox.minX;
                this.selectedNode.fy = (e.clientY - svg.getBoundingClientRect().top) * this.zoomFactor + this.viewBox.minY;
            }
        },
        drop() {
            if (this.selectedNode) {
                if (this.selectedNode.fx) {
                    this.selectedNode.x = this.selectedNode.fx;
                    this.selectedNode.fx = null;
                }
                if (this.selectedNode.fy) {
                    this.selectedNode.y = this.selectedNode.fy;
                    this.selectedNode.fy = null;
                }
                this.selectedNode = null;
                this.graphSimulation.alphaTarget(0);
            }
        },
        hashNetwork(nodes, links) {
            const mergedString = `${nodes.map(node => JSON.stringify(node.id)).join('')}|${links
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
