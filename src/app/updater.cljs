
(ns app.updater
  (:require [app.updater.session :as session]
            [app.updater.user :as user]
            [app.updater.router :as router]
            [app.updater.game :as game]))

(defn updater [db op op-data sid op-id op-time]
  (let [f (case op
            :session/connect session/connect
            :session/disconnect session/disconnect
            :user/log-in user/log-in
            :user/sign-up user/sign-up
            :user/log-out user/log-out
            :session/remove-notification session/remove-notification
            :router/change router/change
            :game/write game/write
            :game/step game/step
            :game/reset game/reset-cursor
            :game/toggle-running game/toggle-running
            (do (println "Unknown op:" op) identity))]
    (f db op-data sid op-id op-time)))
