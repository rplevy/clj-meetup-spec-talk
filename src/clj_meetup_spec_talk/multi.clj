(ns clj-meetup-spec-talk.multi
  (:require [clojure.spec :as s]))

;; from http://clojure.org/guides/spec

(defmulti event-type :event/type)

(defmethod event-type :event/search [_]
  (s/keys :req [:event/type :event/timestamp :search/url]))

(defmethod event-type :event/error [_]
  (s/keys :req [:event/type :event/timestamp :error/message :error/code]))

(s/def :event/event (s/multi-spec event-type :event/type))

(s/valid? :event/event
          {:event/type :event/search
           :event/timestamp 1463970123000
           :search/url "http://clojure.org"})

(s/valid? :event/event
          {:event/type :event/error
           :event/timestamp 1463970123000
           :error/message "Invalid host"
           :error/code 500})

(s/explain :event/event
           {:event/type :event/restart})

(s/explain :event/event
           {:event/type :event/search
            :search/url 200})
