(ns find-the-8.prod
  (:require [find-the-8.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
