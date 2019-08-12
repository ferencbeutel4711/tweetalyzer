<template>
    <div class="home">
        <p>Welcome to the Tweetalyzer! Using this tool, you can analyze a number of features in a given Twitter Data
            Set.</p>
        <p>At the moment, this application is only usable locally utilising a running webserver. Please provide your
            dataset by inserting it directly into the webserver like described in the README.md file.</p>
        <h2>Features</h2>
        <div class="features">
            <div class="feature">
                <div class="feature__left-box">
                    <div class="feature__left-box__image feature__left-box__image--analyze-graph">
                        <h2 class="feature__left-box__label">Analyze the Graph</h2>
                    </div>
                </div>
                <div class="feature__right-box">
                    <div class="feature__right-box__content">
                        <ul>
                        <li>Utilize the full potential of the underlying graph database to perform complex queries fast
                            and with high efficiency</li>
                        <li>Explore the Graph in all its complexity in real time using the enriched graph rendering of
                            d3.js</li>
                        <li>...</li>
                        </ul>
                    </div>
                </div>
            </div>
            <hr class="divider">
            <div class="feature">
                <div class="feature__left-box">
                    <div class="feature__left-box__image feature__left-box__image--hashtag-search">
                        <h2 class="feature__left-box__label">Rich Search</h2>
                    </div>
                </div>
                <div class="feature__right-box">
                    <div class="feature__right-box__content">
                        <ul>
                            <li>You can filter the graph via Hashtag, User or Tweet</li>
                            <li>...</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    mounted: function () {
        this.$store.commit('sideBar/changeActiveModule', 'Home')
    }
}
</script>

<style lang="scss" scoped="true">
    @import "../../css/mixins";

    .divider {
        border: 0;
        height: 1px;
        margin: 16px 0;
        background-image: linear-gradient(to right, white 0%, #8c8b8b 50%, white 100%);
    }

    .feature {
        height: 200px;
        font-size: 0;

        &__left-box {
            box-shadow: inset 0 0 60px 26px black;
            float: left;
            font-size: 16px;
            height: 100%;
            text-align: center;
            vertical-align: top;
            width: 40%;

            &__label {
                display: inline-block;
                margin: 120px 0 0 0;
                color: white;
            }

            &__image {
                background-position: center;
                background-size: cover;
                height: 100%;
                position: relative;
                width: 100%;
                z-index: -1;

                &--analyze-graph {
                    background-image: url('/img/feature--graph01.jpg');
                }

                &--hashtag-search {
                    background-image: url('/img/feature--search01.jpg');
                }
            }
        }

        &__right-box {
            font-size: 16px;
            float: right;
            height: 100%;
            vertical-align: top;
            width: 60%;

            @include vertical-anchor();

            &__content {
                display: inline-block;
                margin-left: 16px;
                vertical-align: middle;

                p {
                    margin: 0;
                }
            }
        }
    }
</style>
