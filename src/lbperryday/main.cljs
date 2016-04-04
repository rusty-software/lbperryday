(ns lbperryday.main
  (:require [reagent.core :as reagent]
            [lbperryday.auto-init]
            [lbperryday.view.main :as view-main]))


(reagent/render-component
  [view-main/play-area]
  (. js/document (getElementById "app")))
