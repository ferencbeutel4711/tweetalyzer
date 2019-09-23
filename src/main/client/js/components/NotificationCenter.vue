<template>
    <div class="notification-center">
        <transition-group name="notifications" tag="div">
            <div class="notification" v-for="notification in notifications" :key="notification.id"
                 v-on:click="deleteNotification(notification.id)"
                 :class="statusToCssClass(notification.status)">
                <span class="notification__message">
                    ({{notification.count}}) {{notification.message}}
                </span>
                <div class="notification__close-button">
                    <i class="material-icons">close</i>
                </div>
            </div>
        </transition-group>
    </div>
</template>

<script>
export default {
    computed: {
        notifications: function () {
            return this.$store.getters['notificationCenter/notifications']
        }
    },
    methods: {
        statusToCssClass: function (state) {
            switch (state) {
                case 'OK':
                    return 'green';
                case 'WARNING':
                    return 'yellow';
                case 'ERROR':
                    return 'red';
                default:
                    return 'blue';
            }
        },
        deleteNotification: function (notificationId) {
            this.$store.commit('notificationCenter/deleteNotification', notificationId);
        }
    }
}
</script>

<style lang="scss" scoped="true">
    $notification-padding: 8px;
    $border-size: 1px;

    .notification-center {
    }

    .notifications {
        &-leave-active {
            position: absolute;
        }

        &-enter {
            opacity: 0;
            transform: translateY(30px);
        }

        &-leave-to {
            opacity: 0;
            transform: translateY(30px);
        }
    }

    .notification {
        border: $border-size solid black;
        margin-bottom: 2px;
        padding: $notification-padding;
        transition: all 0.66s;
        width: calc(100% - (#{$notification-padding} + #{$border-size}) * 2);

        &__message {
            display: inline-block;
            max-width: 95%;
            vertical-align: text-top;
        }

        &__close-button {
            cursor: pointer;
            display: inline-block;
            float: right;
            vertical-align: text-top;
        }
    }

    .blue {
        background-color: #7EA0F5;
    }

    .green {
        background-color: #8BE68A;
    }

    .yellow {
        background-color: #F4D77C;
    }

    .red {
        background-color: #F54E44;
    }
</style>
