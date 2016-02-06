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

(defn board-space [x y drop-shadow]
  ^{:key (str "space-" x "-" y)}
  [:g
   {:dangerouslySetInnerHTML

    {:__html (str "<rect x=\"" x "\" y=\"" y "\" width=\"125\" height=\"70\" stroke=\"black\" stroke-width=\"0.5px\" fill=\"LightGray" #_(colors/random-color) "\""
                  (when drop-shadow
                    " filter=\"url(#") drop-shadow ")\""
                  " />")}}]
  #_[:rect
   {:x x
    :y y
    :width 125
    :height 70
    :stroke "black"
    :stroke-width 0.5
    :filter "url(#blurFilter2)"
    :fill #_"LightGray" (colors/random-color)}])

(def meeples [{:beer "img/beer.png"}
              {:brisket "img/brisket.png"}
              {:card "img/card.png"}
              {:cheetos "img/cheetos.png"}
              {:chief "img/chief.png"}
              {:drum "img/drum.jpg"}
              {:guac "img/guac.png"}
              {:pontoon "img/pontoon.png"}
              {:tent "img/tent.jpg"}])

(defn meeple-image [x y meeple {:keys [on-drag]}]
  ^{:key (str meeple "-" x "-" y)}
  [:g
   {:dangerouslySetInnerHTML
    {:__html (str "<image xlink:href=\"" (first (vals meeple)) "\" "
                  " x=\"" x "\" y=\"" y "\" "
                  " width=\"" 50 "\" height=\"" 50 "\" "
                  " onmousedown=\"" #(dragging on-drag) " />")}}])
