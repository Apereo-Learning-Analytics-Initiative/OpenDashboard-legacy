/**
 * Copyright 2014 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use $scope file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

angular.module('dashboard', [])
.controller('LtiConfigController', ['$scope', '$http', function($scope, $http) {

    var configUrl = "/ltiproxy/config";
    var launchUrl = "/ltiproxy/launch";
    $scope.addMode = false;
    $scope.editMode = false;
    $scope.launchMode = false;
    $scope.url = '';
    $scope.consumerKey = '';
    $scope.consumerSecret = '';

    var clearModel = function () {
        $scope.url = '';
        $scope.consumerKey = '';
        $scope.consumerSecret = '';
    }

    $scope.saveConfig = function saveConfig() {
        var data = '{'
            + '"userId":"",'
            + '"contextId":"",'
            + '"consumerKey":"' + $scope.consumerKey + '",'
            + '"consumerSecret":"' + $scope.consumerSecret + '",'
            + '"url":"' + $scope.url + '"'
            + '}';
        var responsePromise = $http.put(configUrl, data);
        responsePromise.success(function(data, status, headers, config) {
            $scope.setUpLaunchMode();
        });
        responsePromise.error(function(data, status, headers, config) {
        	alert("Call failed [status: " + status + ", messages: " + data.messages + "].");
        });
    };

    $scope.setUpAddMode = function setUpAddMode() {
        $scope.addMode = true;
        $scope.editMode = false;
        $scope.launchMode = false;
    }

    $scope.setUpEditMode = function setUpEditMode() {
        $scope.addMode = false;
        $scope.editMode = true;
        $scope.launchMode = false;
        var responsePromise = $http.get(configUrl);
        responsePromise.success(function(data, status, headers, config) {
            $scope.url = data.url;
            $scope.consumerKey = data.consumerKey;
            $scope.consumerSecret = data.consumerSecret;
        });
        responsePromise.error(function(data, status, headers, config) {
            if (status && status == '404') {
                clearModel();
            } else {
                alert("Call failed [status: " + status + ", messages: " + data.messages + "].");
            }
        });
    }

    $scope.setUpLaunchMode = function setUpLaunchMode() {
        $scope.addMode = false;
        $scope.editMode = false;
        $scope.launchMode = true;
        $('#ltiproxy-iframe').remove();
        $('#ltiproxy-iframe-div').html('<iframe class="iframe" src="' + launchUrl + '"></iframe>');
    }

    var setUpInitialPageMode = function setUpInitialPageMode() {
        var responsePromise = $http.get(configUrl);
        responsePromise.success(function(data, status, headers, config) {
            $scope.setupLaunchMode();
        });
        responsePromise.error(function(data, status, headers, config) {
            if (status && status == '404') {
                $scope.setUpAddMode();
            } else {
                alert("Call failed [status: " + status + ", messages: " + data.messages + "].");
            }
        });
    }

    setUpInitialPageMode();
}]);