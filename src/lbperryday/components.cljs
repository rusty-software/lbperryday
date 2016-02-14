(ns lbperryday.components
  (:require [goog.events :as events])
  (:import [goog.events EventType]))

(def space-images ["img/beer.png"
                   "img/brisket.png"
                   "img/card.png"
                   "img/cheetos.png"
                   "img/chief.png"
                   "img/drum.jpg"
                   "img/guac.png"
                   "img/pontoon.png"
                   "img/tent.jpg"])

(def audio-snippets {:scream {:name "audio-scream" :source "sounds/scream.mp3" :type "audio/mpeg"}
                     :chief {:name "audio-chief" :source "sounds/chief.mp3" :type "audio/mpeg"}
                     :rooster {:name "audio-rooster" :source "sounds/rooster.mp3" :type "audio/mpeg"}
                     :snoring {:name "audio-snoring" :source "sounds/snoring.mp3" :type "audio/mpeg"}
                     :splash {:name "audio-splash" :source "sounds/splash.mp3" :type "audio/mpeg"}
                     :free-pass {:name "audio-free-pass" :source "sounds/free_pass.mp3" :type "audio/mpeg"}
                     :power-down {:name "audio-power-down" :source "sounds/power_down.mp3" :type "audio/mpeg"}})

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

(defn space-image [img x y size]
  [:g
   {:dangerouslySetInnerHTML {:__html (str "<image xlink:href=\"" img "\" x=" x " y=" y " width=\"" size "\" height=\"" size "\" />")}}])

(defn audio-snippet [name source type]
  ^{:key name}
  [:audio {:id name
           :controls false}
   [:source {:src source
             :type type}]])

(defn end-game-text [name]
  [:div
   (str "Congratulations, " name "!  You've achieved LBP Nirvana!  Rub it in the faces of your less fortunate compatriots, and perhaps grab them another beverage.")])
