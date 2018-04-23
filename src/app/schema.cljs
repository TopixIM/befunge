
(ns app.schema )

(def configs {:storage-key "befunge", :port 11004})

(def game {:board {}, :stack [], :direction :right, :interval 1024, :cursor {:x 0, :y 0}})

(def database {:sessions {}, :users {}, :game game, :running? false})

(def dev? (do ^boolean js/goog.DEBUG))

(def notification {:id nil, :kind nil, :text nil})

(def router {:name nil, :title nil, :data {}, :router nil})

(def session
  {:user-id nil,
   :id nil,
   :nickname nil,
   :router {:name :home, :data nil, :router nil},
   :notifications []})

(def settings {:x 32, :y 16, :size 20})

(def user {:name nil, :id nil, :nickname nil, :avatar nil, :password nil})
