(ns triface.test.core
  (:use [triface.core])
  (:use [clojure.test])
  (:use [triface.debug])
  (:require [triface.db :as db])
  (:require [triface.model :as model])
  (:require [clojure.data.json :as json]))

(deftest content-list-test
  (binding [db/query (fn [query & args] (array-map :id 1 :name "model"))]
    (is (> (count (content-list "model")) 0))))

(deftest content-item-test
  (binding [db/query (fn [query & args] (array-map :id 1))]
    (is (> (count (content-item "model" 1)) 0))))

(deftest content-field-test
  (binding [db/query (fn [query & args] (vector (array-map :id 1 :name "model")))]
    (is (= "model" (content-field "model" 1 :name)))))

;; TODO: test timestamp fields
(deftest render-test
  (model/invoke-models)
  (let [model (render "model" {:id 1 :name "foo" :description "bar" :position 1 :nested false :locked true :abstract false :ancestor_id 0} {})]
    (is (not (model nil)))
    (is (= (model :name) "foo"))
    (is (= (model :description) "bar"))
    (is (= (model :position) 1))
    (is (= (model :nested) false))
    (is (= (model :abstract) false))
    (is (= (model :ancestor_id) 0))))

(deftest render-field-test
  (model/invoke-models)
  (is (= "yayay" (render-field "model" {:description "yayay"} "description" {}))))

;; actions ------------------------------------------------
;; happy-path action smoke-testing

;; GET home
(deftest home-action-test
  (let [response (json/read-json (home {}))]
    (is (not (response nil)))
    (is (not (= (error :message)(response :message))))))

;; GET list-all
(deftest list-all-action-test
  (model/invoke-models)
  (let [response (json/read-json (list-all {:slug "model"}))]
    (is (> (count response) 0))))

;; TODO
;; POST create-content
(deftest create-content-action-test
  (model/invoke-models))

;; GET model-spec
(deftest model-spec-action-test
  (model/invoke-models)
  (let [response (json/read-json (model-spec {:slug "model"}))]
    (is (> (count response) 0))
    (is (not (= (error :message) (response :message))))))

;; GET item-detail
(deftest item-detail-action-test
  (model/invoke-models)
  (let [response (json/read-json (item-detail {:slug "model" :id 1}))]
    (is (> (count response) 0))
    (is (not (= (error :message)(response :message))))))

;; TODO
;; PUT update-content
(deftest update-content-action-test
  (model/invoke-models))

;; TODO
;; PUT delete-content
(deftest delete-content-action-test
  (model/invoke-models))

;; GET field-detail
(deftest field-detail-action-test
  (model/invoke-models)
  (let [response (json/read-json(field-detail {:slug "model" :id 1 :field "name"}))]
    (is (= "model" response))))