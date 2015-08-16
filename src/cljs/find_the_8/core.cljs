(ns find-the-8.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType])
    (:import goog.History))

;; -------------------------
;; State

(defonce initial-state {:level 0 :time-left 5})
(defonce app-data (atom initial-state))

;; -------------------------
;; Components

(defn fucking-8 []
  (if (= 0 (:time-left @app-data))
    [:span 8]
    [:span
      {:on-click
        #(swap! app-data assoc
           :level (inc (:level @app-data))
           :time-left (+ 5 (:time-left @app-data)))}
      8]))


(defn field [size width-cell]
  (let [total-elems (* size size)
        position-8 (rand-int total-elems)]
    [:div.flex-wrap {:style {:width (* size width-cell) :margin "auto"}}
      (for [i (range total-elems)]
        [:div.text-center.pointer
          {:style {:width width-cell} :key i}
          (if (= position-8 i)
            [fucking-8]
            9)])]))


(defn home-page []
  (def timer (js/setInterval #(swap! app-data assoc :time-left (dec (:time-left @app-data))) 1000))
  (fn []
    (let [level (:level @app-data)
          time-left (:time-left @app-data)
          game-over? (= 0 time-left)]
      (if game-over? (js/clearInterval timer))
      [:div.text-center
        [:div {:style {:opacity (if game-over? 0.5 1)}}
          [:h2 "level " level]
          [:h3 "You have " time-left  " seconds left to find the 8"]
          [field (+ 5 level) 20]]
        (if game-over?
          [:h4
            [:a
              {:on-click #(.reload js/location)}
              "Replay"]])])))

; (defn about-page []
;   [:div [:h2 "About find-the-8"]
;    [:div [:a {:href "#/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
; (secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

; (secretary/defroute "/about" []
;   (session/put! :current-page #'about-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
