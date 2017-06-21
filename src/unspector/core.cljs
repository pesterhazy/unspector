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

(defn inspector [& args]
  (into [:> (gobj/get (gobj/get js/window "ReactInspector") "Inspector")] args))

(defn hello-world []
  [inspector {:expand-level 3
              :data (->> (get-query-params)
                         clj->js)}])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  )
