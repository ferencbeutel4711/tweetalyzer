<template>
    <div class="sidebar">
        <router-link to="/" class="sidebar__link">
            <div class="sidebar__button" :class="{active: isButtonActive('Home')}">
                <i class="sidebar__button__icon material-icons">home</i>
                <p class="sidebar__button__label">Home</p>
            </div>
        </router-link>
        <router-link to="/graph" class="sidebar__link">
            <div class="sidebar__button" :class="{active: isButtonActive('Graph')}">
                <i class="sidebar__button__icon material-icons">games</i>
                <p class="sidebar__button__label">Graph</p>
            </div>
        </router-link>
        <router-link to="/settings" class="sidebar__link">
            <div class="sidebar__button" :class="{active: isButtonActive('Settings')}">
                <i class="sidebar__button__icon material-icons">build</i>
                <p class="sidebar__button__label">Settings</p>
            </div>
        </router-link>
    </div>
</template>

<script>
export default {
    mounted: function () {
        this.$store.commit('changeActiveModule', 'Home')
    },
    methods: {
        isButtonActive(button) {
            return this.$store.getters.activeModule === button;
        }
    }
}
</script>

<style lang="scss" scoped="true">
    .sidebar {
        background-color: rgb(90, 90, 90);
        display: inline-block;
        font-size: 16px;
        height: 100%;
        position: fixed;
        width: 100px;

        &__link {
            text-decoration: none;
        }

        &__button {
            color: white;
            margin: 4px;
            padding: 16px;
            text-align: center;

            &__icon {
                font-size: 36px;
            }

            &__label {
                font-size: 12px;
                margin: 0;
            }

            &:hover {
                background-color: rgba(255, 255, 255, 0.2) !important;
            }
        }

        &__button.active {
            background-color: rgba(255, 255, 255, 0.1);
        }
    }
</style>
