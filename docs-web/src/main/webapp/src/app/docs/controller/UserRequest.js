'use strict';

/**
 * UserRequest controller - 管理员查看和审核注册请求
 */
angular.module('docs').controller('UserRequestController', function($scope, Restangular) {
  $scope.requests = [];
  $scope.loading = false;

  /**
   * 加载所有注册申请
   */
  $scope.loadRequests = function() {
    $scope.loading = true;
    Restangular.all('user-request').getList().then(function(data) {
      $scope.requests = data.requests;
    }).catch(function(error) {
      alert('加载请求失败：' + (error.data && error.data.message || '未知错误'));
    }).finally(function() {
      $scope.loading = false;
    });
  };
  $scope.loadRequests();

  /**
   * 批准注册申请
   */
  $scope.approve = function(id) {
    if (!confirm('确定要批准此请求吗？')) return;
    Restangular.one('user-request', id).customPOST({}, 'approve').then(function() {
      alert('已批准');
      $scope.loadRequests();
    }).catch(function(error) {
      alert('批准失败：' + (error.data && error.data.message || '未知错误'));
    });
  };

  /**
   * 拒绝注册申请
   */
  $scope.reject = function(id) {
    if (!confirm('确定要拒绝此请求吗？')) return;
    Restangular.one('user-request', id).customPOST({}, 'reject').then(function() {
      alert('已拒绝');
      $scope.loadRequests();
    }).catch(function(error) {
      alert('拒绝失败：' + (error.data && error.data.message || '未知错误'));
    });
  };
});
