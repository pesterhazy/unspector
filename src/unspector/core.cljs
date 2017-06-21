(ns unspector.core
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.object :as gobj]))

(enable-console-print!)

(println "This text is printed from src/unspector/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn inspector [& args]
  (into [:> (gobj/get (gobj/get js/window "ReactInspector") "Inspector")] args))

(defn hello-world []
  [inspector {:expand-level 3
              :data #js {:foo "bar"}}])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
