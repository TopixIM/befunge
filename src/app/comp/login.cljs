
(ns app.comp.login
  (:require [respo.macros :refer [defcomp <> div input button span]]
            [respo.comp.space :refer [=<]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo-ui.core :as ui]
            [app.schema :as schema]
            [app.style :as style]
            [clojure.string :as string]
            [respo-md.comp.md :refer [comp-md comp-md-block]]))

(def initial-state {:username "", :password ""})

(defn on-input [state k] (fn [e dispatch! mutate!] (mutate! (assoc state k (:value e)))))

(defn on-submit [username password signup?]
  (fn [e dispatch!]
    (when (not (string/blank? username))
      (dispatch! (if signup? :user/sign-up :user/log-in) [username password])
      (.setItem js/localStorage (:storage-key schema/configs) [username password]))))

(defcomp
 comp-login
 (states)
 (let [state (or (:data states) initial-state)]
   (div
    {:style (merge ui/flex ui/center)}
    (div
     {:style {:font-size 32, :font-family ui/font-fancy, :font-weight 300}}
     (comp-md "A playground for [Befunge](https://dorianbrown.github.io/befunge/)!"))
    (=< nil 16)
    (div
     {}
     (div
      {:style {}}
      (div
       {}
       (input
        {:placeholder "Username",
         :value (:username state),
         :style ui/input,
         :on-input (on-input state :username)})))
     (=< nil 8)
     (div
      {:style {:text-align :right}}
      (span
       {:inner-text "Sign up",
        :style (merge style/link),
        :on-click (on-submit (:username state) (:password state) true)})
      (=< 8 nil)
      (span
       {:inner-text "Log in",
        :style (merge style/link),
        :on-click (on-submit (:username state) (:password state) false)}))))))
