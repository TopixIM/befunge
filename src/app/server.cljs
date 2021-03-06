
(ns app.server
  (:require [app.schema :as schema]
            [app.service :refer [run-server! sync-clients!]]
            [app.updater :refer [updater]]
            [cljs.reader :refer [read-string]]
            [app.util :refer [try-verbosely!]]
            [app.reel :refer [reel-reducer refresh-reel reel-schema]]
            ["fs" :as fs]
            ["shortid" :as shortid]
            [app.node-env :as node-env]))

(declare dispatch!)

(declare run-the-game!)

(def initial-db
  (let [filepath (:storage-path node-env/configs)]
    (if (fs/existsSync filepath)
      (do
       (println "Found storage in:" (:storage-path node-env/configs))
       (read-string (fs/readFileSync filepath "utf8")))
      schema/database)))

(defonce *reel (atom (merge reel-schema {:base initial-db, :db initial-db})))

(defonce *reader-reel (atom @*reel))

(defn persist-db! []
  (println "Saving file on exit:" (:storage-path node-env/configs))
  (fs/writeFileSync
   (:storage-path node-env/configs)
   (pr-str (assoc (:db @*reel) :sessions {}))))

(defn run-the-game! [sid]
  (dispatch! :game/step nil sid)
  (js/setTimeout (fn [] (if (get-in @*reel [:db :game :running?]) (run-the-game! sid))) 200))

(defn dispatch! [op op-data sid]
  (let [op-id (.generate shortid), op-time (.valueOf (js/Date.)), db (get @*reel :db)]
    (println "Dispatch!" (str op) op-data sid)
    (try-verbosely!
     (cond
       (= op :effect/persist) (persist-db!)
       (= op :effect/run) (do (run-the-game! sid) (dispatch! :game/toggle-running true))
       :else
         (let [new-reel (reel-reducer @*reel updater op op-data sid op-id op-time)]
           (reset! *reel new-reel))))))

(defn on-exit! [code]
  (persist-db!)
  (println "exit code is:" (pr-str code))
  (.exit js/process))

(defn render-loop! []
  (if (not (identical? @*reader-reel @*reel))
    (do (reset! *reader-reel @*reel) (sync-clients! @*reader-reel)))
  (js/setTimeout render-loop! 60))

(defn main! []
  (run-server! #(dispatch! %1 %2 %3) (:port schema/configs))
  (render-loop!)
  (.on js/process "SIGINT" on-exit!)
  (js/setInterval #(persist-db!) (* 60 1000 10))
  (println "Server started."))

(defn reload! []
  (println "Code updated.")
  (reset! *reel (refresh-reel @*reel initial-db updater))
  (sync-clients! @*reader-reel))
