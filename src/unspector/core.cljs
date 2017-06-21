(ns unspector.core
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.Uri :as guri]
            [goog.object :as gobj]))

(enable-console-print!)

(defn extract [qd] (into {} (map (fn [k] [k (.get qd k)]) (.getKeys qd))))
(defn get-query-params []
  (-> (guri/parse js/location) .getQueryData extract))

(defn inspector [& args]
  (into [:> (gobj/get (gobj/get js/window "ReactInspector") "Inspector")] args))

(defn hello-world []
  [inspector {:expand-level 3
              :data (clj->js (get-query-params))}])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  )
