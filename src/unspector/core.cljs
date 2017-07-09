(ns unspector.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cognitect.transit :as transit]
            [clojure.string :as str]
            [goog.net.XhrIo :as xhr]
            [goog.Uri :as guri]
            [goog.object :as gobj]))

(enable-console-print!)

(def transit-reader (transit/reader :json))
(def transit-writer (transit/writer :json))

(defn send [m cb]
  (xhr/send "http://localhost:9999/action"
            (fn [e]
              (if (-> e .-target .isSuccess)
                (->> e
                     .-target
                     .getResponseText
                     (transit/read transit-reader)
                     cb)
                (prn [:network-request-failed])))
            "POST"
            (transit/write transit-writer m)))

(defn extract [qd] (map (fn [k] [k (.get qd k)]) (.getKeys qd)))
(defn transform [kvs]
  (reduce (fn [m [k v]]
            (assoc-in m (str/split k #"\.") v))
          {}
          kvs))
(defn get-query-params []
  (-> (guri/parse js/location) .getQueryData extract transform))

(defn get-hash-json []
  (when-let [[_ dat] (re-matches #"^#json:(.*)" js/location.hash)]
    (-> dat js/decodeURIComponent js/JSON.parse)))

(defn get-data []
  (or (get-hash-json) (get-query-params)))

(defn inspector [& args]
  (into [:> (gobj/get (gobj/get js/window "ReactInspector") "Inspector")] args))

(defn root-ui []
  [inspector {:expand-level 3
              :data (->> (get-data)
                         clj->js)}])

(reagent/render-component [root-ui]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  (send [1 2 3 4 5] prn))
