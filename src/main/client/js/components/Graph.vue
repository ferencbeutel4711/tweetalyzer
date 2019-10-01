<template>
    <div class="graph">
        <h1>Graph</h1>
        <NetworkGraph id="network" :width="775" :height="775" :nodes="nodes" :links="links"/>
    </div>
</template>

<script>
import * as d3 from 'd3';
import NetworkGraph from "./graph/NetworkGraph";

export default {
    components: {NetworkGraph},
    data() {
        const nodes = d3.range(100).map(i => ({index: i, x: 400, y: 400}));

        return {
            nodes,
            links: d3.range(99).map(i => ({source: nodes[Math.floor(Math.sqrt(i))], target: nodes[i + 1]}))
        }
    }
}
</script>

<style lang="scss" scoped="true">
</style>
