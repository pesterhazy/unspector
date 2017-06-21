(ns unspector.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [goog.Uri :as guri]
            [goog.object :as gobj]))

(enable-console-print!)

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
  )
