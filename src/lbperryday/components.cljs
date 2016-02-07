(ns lbperryday.components
  (:require [goog.events :as events])
  (:import [goog.events EventType]))

(def text-defaults {:font-family "Bangers";
                    :font-size "16px"
                    :class "move-area"})

(defn drag-move-fn [on-drag]
  (fn [evt]
    (on-drag (.-clientX evt) (.-clientY evt))))

(defn drag-end-fn [drag-move drag-end on-end]
  (fn [evt]
    (events/unlisten js/window EventType.MOUSEMOVE drag-move)
    (events/unlisten js/window EventType.MOUSEUP @drag-end)
    (on-end (.-clientX evt) (.-clientY evt))))

(defn dragging
  ([on-drag] (dragging on-drag (fn []) (fn [])))
  ([on-drag on-start on-end]
   (let [drag-move (drag-move-fn on-drag)
         drag-end-atom (atom nil)
         drag-end (drag-end-fn drag-move drag-end-atom on-end)]
     (on-start)
     (reset! drag-end-atom drag-end)
     (events/listen js/window EventType.MOUSEMOVE drag-move)
     (events/listen js/window EventType.MOUSEUP drag-end))))

(defn player-name [{:keys [on-drag on-start on-end]} {:keys [x y name]}]
  ^{:key (str "player-" name)}
  [:text
   (merge text-defaults
          {:on-mouse-down #(dragging on-drag on-start on-end)
           :x x
           :y y})
   name])

(defn board-space [x y color]
  ^{:key (str "space-" x "-" y)}
  [:rect
   {:x x
    :y y
    :width 125
    :height 70
    :stroke "black"
    :stroke-width 0.5
    :fill color}])


