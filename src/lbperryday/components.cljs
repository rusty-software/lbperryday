(ns lbperryday.components
  (:require [goog.events :as events]
            [lbperryday.html-colors :as colors])
  (:import [goog.events EventType]))

(def text-defaults {:font-size "x-small"})

(defn drag-move-fn [on-drag]
  (fn [evt]
    (on-drag (.-clientX evt) (.-clientY evt))))

(defn drag-end-fn [drag-move drag-end on-end]
  (fn [evt]
    (events/unlisten js/window EventType.MOUSEMOVE drag-move)
    (events/unlisten js/window EventType.MOUSEUP @drag-end)
    (on-end)))

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

(defn player-name [{:keys [on-drag]} {:keys [x y name]}]
  ^{:key (str "player-" name)}
  [:text
   (merge text-defaults
          {:on-mouse-down #(dragging on-drag)
           :x x
           :y y})
   name])

(defn board-space [x y]
  ^{:key (str "space-" x "-" y)}
  [:rect
   {:x x
    :y y
    :width 125
    :height 70
    :stroke "black"
    :stroke-width 0.5
    :fill "LightGray"}])

#_(defn player-name [{:keys [on-drag]} {:keys [x y name]}]
  ^{:key (str "player-" name)}
  [:circle
   {:on-mouse-down #(dragging on-drag)
    :cx x
    :cy y
    :r 5
    :fill "blue"}])
